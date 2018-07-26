package bo.roman.radio.player.listener;

import static bo.roman.radio.utilities.LoggerUtils.logDebug;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bo.roman.radio.cover.model.Radio;
import bo.roman.radio.cover.model.Song;
import bo.roman.radio.player.codec.CodecCalculator;
import bo.roman.radio.player.model.CodecInformation;
import bo.roman.radio.player.model.ErrorInformation;
import bo.roman.radio.player.model.RadioPlayerEntity;
import bo.roman.radio.utilities.MediaMetaUtils;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import uk.co.caprica.vlcj.player.MediaMeta;
import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerEventAdapter;

/**
 * This class will use javaRx to create an observable that will push content
 * every time the meta type of the radio player is modified.
 * 
 * @author Christian Roman Rua
 *
 */
public class ReactiveMediaEventListener extends MediaPlayerEventAdapter {
	private static final Logger log = LoggerFactory.getLogger(ReactiveMediaEventListener.class);

	private static final int METATYPE_NOWPLAYING = 12; // Now Playing has changed

	private final RadioInformationFinder radioInfoFinder;

	private final Observable<RadioPlayerEntity> radioPlayerObservable;
	private final Observable<CodecInformation> codecInfoObservable;
	private final Observable<ErrorInformation> errorInfoObservable;

	private Listener<ErrorInformation> errorListener = new Listener<>();
	private Listener<RadioPlayerEntity> radioListener = new Listener<>();
	private Listener<CodecInformation> codecListener = new Listener<>();

	public ReactiveMediaEventListener(RadioInformationFinder radioInfoFinder) {
		this.radioPlayerObservable = Observable.create(radioListener);
		this.codecInfoObservable = Observable.create(codecListener);
		this.errorInfoObservable = Observable.create(errorListener);

		this.radioInfoFinder = radioInfoFinder;
	}
	
	public Observable<RadioPlayerEntity> getRadioPlayerObservable() {
		return radioPlayerObservable;
	}
	
	public Observable<CodecInformation> getCodecInfoObservable() {
		return codecInfoObservable;
	}
	
	public Observable<ErrorInformation> getErrorInfoObservable() {
		return errorInfoObservable;
	}

	/**
	 * Create Observables every time info is updated
	 */
	@Override
	public void mediaMetaChanged(MediaPlayer mediaPlayer, int metaType) {
		logDebug(log, () -> String.format("Media Meta Changed[metaType=%s]", metaType));
		
		if (metaType == METATYPE_NOWPLAYING) { // 12 -> Changed NowPlying
			MediaMeta meta = mediaPlayer.getMediaMeta();

			// Parse the info sent by the meta to find the name of the radio
			// and the song that is being playing
			final Optional<String> oRadioName = MediaMetaUtils.findRadioName(meta);
			final Optional<Song> oSong = MediaMetaUtils.buildSong(meta);
			log.info("Changed MediaMeta. Radio[{}] and Song[{}]", oRadioName, oSong);
			logDebug(log, () -> "NowPlaying=" + meta.getNowPlaying());

			meta.release();

			// Find info about the radio station and the song that is playing
			CompletableFuture<Void> futureRpe = CompletableFuture.runAsync(() -> {
				RadioPlayerEntity rpe = radioInfoFinder.find(oRadioName, oSong);
				setStreamUrl(rpe, mediaPlayer.mrl());
				radioListener.push(rpe);
			});

			// Find the info about the codec of the stream
			CompletableFuture<Void> futureCodec = CompletableFuture.runAsync(() -> {
				Optional<CodecInformation> oci = CodecCalculator.calculate(mediaPlayer);
				oci.ifPresent(codecListener::push);
			});

			futureRpe.join();
			futureCodec.join();
		}
	}

	@Override
	public void error(MediaPlayer mediaPlayer) {
		String streamName = "";
		if (mediaPlayer.getMediaMeta() != null) {
			streamName = mediaPlayer.getMediaMeta().getTitle();
		}

		log.error("There was an error trying to play the stream [{}]", streamName);
		errorListener.push(new ErrorInformation("Error playing stream.", streamName));
	}

	private void setStreamUrl(RadioPlayerEntity rpe, String streamUrl) {
		Optional<Radio> oRadio = rpe.getRadio();
		if (oRadio.isPresent()) {
			logDebug(log, () -> "Radio object is present, setting Radio StreamUrl:" + streamUrl);
			Radio r = oRadio.get();
			r.setStreamUrl(streamUrl);
		}
	}

	private static class Listener<T> implements ObservableOnSubscribe<T> {
		private ObservableEmitter<T> emmiter;

		@Override
		public void subscribe(ObservableEmitter<T> emmiter) throws Exception {
			this.emmiter = emmiter;
		}

		public void push(T errorInformation) {
			emmiter.onNext(errorInformation);
		}
	}

}
