package bo.roman.radio.player;

import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bo.roman.radio.cover.CoverArtManager;
import bo.roman.radio.cover.RadioCoverInterface;
import bo.roman.radio.cover.model.Album;
import bo.roman.radio.cover.model.Radio;
import bo.roman.radio.cover.model.Song;
import bo.roman.radio.utilities.LoggerUtils;
import bo.roman.radio.utilities.MediaMetaUtils;
import uk.co.caprica.vlcj.player.MediaMeta;
import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerEventAdapter;

public class RadioPlayerTest {
	private final static Logger log = LoggerFactory.getLogger(RadioPlayerTest.class);
	// http://7609.live.streamtheworld.com:80/977_ALTERN_SC
	// http://stream-tx3.radioparadise.com/aac-128
	// http://icecast.omroep.nl:80/3fm-bb-mp3
	// http://mostpop.servemp3.com:8000
	// http://72.13.82.82:5100/
	// http://5.135.223.251:9000
	// http://streaming.radionomy.com/Classic-Rap
	// http://88.208.218.19:9106/stream

	private static final String RADIO_STREAM = "http://stream-tx3.radioparadise.com/aac-128";
	private RadioPlayer player;

	@Before
	public void setUp() {
		player = new RadioPlayer(new RadioPlayerEventListener());
	}

//	@Test
	public void testPlayerInStream() {
		// Create a Thread to play the song
		Thread playerThread = new Thread(() -> player.play(RADIO_STREAM));
		// Start the RadioPlayer:
		playerThread.start();

		// Wait for 30 seconds and stop
		try {
			// Forever
			playerThread.join();
			
			log.info("Waiting 5 minutes before stopping it...");
			Thread.sleep(5 * 60 * 1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// Stop player
		player.stop();
	}

	private static class RadioPlayerEventListener extends MediaPlayerEventAdapter {
		private final static Logger log = LoggerFactory.getLogger(RadioPlayerEventListener.class);

		@Override
		public void mediaMetaChanged(MediaPlayer mediaPlayer, int metaType) {
			log.info("Media Meta Changed[metaType={}]", metaType);
			MediaMeta meta = mediaPlayer.getMediaMeta();
			
			// Instantiate the Cover Art Manager
			RadioCoverInterface coverManager = new CoverArtManager();
			
			Optional<Song> optSong = MediaMetaUtils.buildSong(meta);
			// Radio Station
			String radioName = MediaMetaUtils.parseRadioName(meta.getTitle());
			Optional<Radio> oRadio = coverManager.getRadioWithLogo(radioName);
			
			if(optSong.isPresent()) {
				Song song = optSong.get();
				LoggerUtils.logDebug(log, () -> String.format("Starting a new Thread to retrieve the CoverArt of %s", song));
				// Create Cover Thread
				Thread coverThread = new Thread(() -> {
					Optional<Album> oRichAlbum = coverManager.getAlbumWithCover(song.getName(), song.getArtist());
					log.info("Playing {}", song);
					oRichAlbum.ifPresent(a -> log.info("From {}", a));
					oRadio.ifPresent(r -> log.info("In {}", r));
					
				});
				coverThread.start();
			} else {
				optSong.ifPresent(s -> log.info("Playing {}", s));
				oRadio.ifPresent(r -> log.info("In {}", r));
			}
			
			meta.release();
		}
	}

}
