package bo.roman.radio.player;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bo.roman.radio.player.listener.MediaMetaNotifier;
import bo.roman.radio.player.listener.PrintRadioPlayerObserver;
import bo.roman.radio.player.listener.RadioPlayerEventListener;
import uk.co.caprica.vlcj.player.MediaMeta;
import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerEventAdapter;

public class RadioPlayerTest {
	private final static Logger log = LoggerFactory.getLogger(RadioPlayerTest.class);
	// http://50.7.74.82:8193
	// http://7609.live.streamtheworld.com:80/977_ALTERN_SC
	// http://stream-tx3.radioparadise.com/aac-128
	// http://icecast.omroep.nl:80/3fm-bb-mp3
	// http://mostpop.servemp3.com:8000
	// http://72.13.82.82:5100/
	// http://5.135.223.251:9000
	// http://streaming.radionomy.com/Classic-Rap
	// http://88.208.218.19:9106/stream

	private static final String RADIO_STREAM = "http://stream-tx3.radioparadise.com/aac-64";
	private RadioPlayer player;

	@Before
	public void setUp() {
		player = new RadioPlayer();
		player.addEventsListener(new RadioPlayerEventListener(player));
	}

	@Test
	public void testPlayerInStream() {
		// Create a Thread to play the song
		Thread playerThread = new Thread(() -> player.play(RADIO_STREAM));
		// Start the RadioPlayer:
		playerThread.start();

		// Wait for 30 seconds and stop
		try {
			// Forever
			playerThread.join();
			
			log.info("Waiting 15 secs before stopping it...");
			Thread.sleep(30 * 1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// Stop player
		player.stop();
	}

	private static class TestRadioPlayerEventListener extends MediaPlayerEventAdapter {
		private final static Logger log = LoggerFactory.getLogger(TestRadioPlayerEventListener.class);

		@Override
		public void mediaMetaChanged(MediaPlayer mediaPlayer, int metaType) {
			log.info("Media Meta Changed[metaType={}]", metaType);
			
//			MediaMeta meta = mediaPlayer.getMediaMeta();
//			MediaMetaNotifier notifier = new MediaMetaNotifier(meta);
//			notifier.registerObserver(new PrintRadioPlayerObserver());
//			notifier.notifyObservers();
//					
//			meta.release();
		}
		
		@Override
		public void error(MediaPlayer mediaPlayer) {
			System.out.println(".:. ERROR! Stop player");
			mediaPlayer.release();
			System.exit(1);
		}
		
		@Override
		public void playing(MediaPlayer mediaPlayer) {
			System.out.println(".:. >: PLAYING");
		}
		
		@Override
		public void stopped(MediaPlayer mediaPlayer) {
			System.out.println(".:. .:. PLayer Stopped!");
		}
	}

}
