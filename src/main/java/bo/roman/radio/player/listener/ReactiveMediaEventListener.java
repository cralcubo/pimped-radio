package bo.roman.radio.player.listener;

import static bo.roman.radio.utilities.LoggerUtils.logDebug;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bo.roman.radio.cover.model.Codec;
import bo.roman.radio.cover.model.Song;
import bo.roman.radio.player.codec.CodecCalculator;
import bo.roman.radio.player.exception.RadioStopException;
import bo.roman.radio.player.model.MediaPlayerInformation;
import bo.roman.radio.utilities.MediaMetaUtils;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import uk.co.caprica.vlcj.player.MediaMeta;
import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerEventAdapter;

public class ReactiveMediaEventListener extends MediaPlayerEventAdapter
		implements ObservableOnSubscribe<MediaPlayerInformation> {
	private static final Logger log = LoggerFactory.getLogger(ReactiveMediaEventListener.class);
	private static final int METATYPE_NOWPLAYING = 12;
	private ObservableEmitter<MediaPlayerInformation> emitter;

	@Override
	public void subscribe(ObservableEmitter<MediaPlayerInformation> emitter) throws Exception {
		logDebug(log, () -> "Subscribing new ObservableEmitter: " + emitter);
		this.emitter = emitter;
	}

	@Override
	public void mediaMetaChanged(MediaPlayer mediaPlayer, int metaType) {
		if (metaType == METATYPE_NOWPLAYING) { // 12 -> Changed NowPlying
			MediaMeta meta = mediaPlayer.getMediaMeta();
			logDebug(log, () -> "MediaMeta changed=" + meta);
			final Optional<String> oRadioName = MediaMetaUtils.findRadioName(meta);
			final Optional<Song> oSong = MediaMetaUtils.buildSong(meta);
			final Optional<Codec> oCodec = CodecCalculator.calculateCodec(mediaPlayer);
			log.info("Changed MediaMeta. Radio[{}] and Song[{}]", oRadioName, oSong);
			meta.release();

			// Convert all the parsed info to MediaPlayerInformation an notify it
			emitter.onNext(new MediaPlayerInformation(oCodec, oSong, oRadioName));
		}
	}

	@Override
	public void error(MediaPlayer mediaPlayer) {
		log.info("Error detected while playing the radio.");
		final Optional<String> streamUrl = Optional.ofNullable(mediaPlayer.getMediaMeta())//
				.map(MediaMeta::getTitle);
		
		final Optional<String> radio = MediaMetaUtils.findRadioName(mediaPlayer.getMediaMeta());

		emitter.onError(new RadioStopException(streamUrl, radio));
	}
	
	@Override
	public void stopped(MediaPlayer mediaPlayer) {
		log.info("Radio stopped.");
		emitter.onComplete();
	}
}
