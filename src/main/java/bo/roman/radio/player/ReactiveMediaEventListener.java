package bo.roman.radio.player;

import static bo.roman.radio.utilities.LoggerUtils.logDebug;
import static bo.roman.radio.utilities.MediaMetaUtils.parseBuildSong;
import static bo.roman.radio.utilities.MediaMetaUtils.parseRadioName;

import java.util.Optional;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bo.roman.radio.cover.model.Song;
import bo.roman.radio.player.exception.RadioStopedException;
import bo.roman.radio.player.model.MediaPlayerInformation;
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
			Optional<Song> oSong = parseBuildSong(meta);
			Optional<String> oRadioName = parseRadioName(meta);
			meta.release();

			Function<Optional<String>, String> orNull = o -> o.orElse(null);
			String song = orNull.apply(oSong.map(Song::getName));
			String artist = orNull.apply(oSong.map(Song::getArtist));
			String radio = orNull.apply(oRadioName);

			emitter.onNext(new MediaPlayerInformation.Builder()//
					.artist(artist)//
					.song(song)//
					.radioName(radio)//
					.build());
		}
	}

	@Override
	public void error(MediaPlayer mediaPlayer) {
		log.info("Error detected while playing the radio.");
		emitter.onError(
				new RadioStopedException(parseRadioName(mediaPlayer.getMediaMeta()).orElseGet(() -> "unknown")));
	}

	@Override
	public void stopped(MediaPlayer mediaPlayer) {
		log.info("Radio stopped.");
		emitter.onComplete();
	}
}
