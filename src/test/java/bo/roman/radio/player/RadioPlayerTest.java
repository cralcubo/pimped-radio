package bo.roman.radio.player;

import org.junit.Before;
import org.junit.Test;

import bo.roman.radio.player.listener.MediaMetaNotifier;
import bo.roman.radio.player.listener.MediaMetaSubject;
import bo.roman.radio.player.listener.PrintRadioPlayerObserver;
import bo.roman.radio.player.listener.RadioPlayerEventListener;

public class RadioPlayerTest {
//	private final static Logger log = LoggerFactory.getLogger(RadioPlayerTest.class);
	// http://50.7.74.82:8193
	// http://7609.live.streamtheworld.com:80/977_ALTERN_SC
	// http://stream-tx3.radioparadise.com/aac-128
	// http://icecast.omroep.nl:80/3fm-bb-mp3
	// http://server1.radiodanz.com:8020/
	// http://streaming.radionomy.com/Classic-Rap
	// http://icecast.omroep.nl:80/radio4-bb-mp3
	// http://195.154.182.222:27147/973
	// http://listen.181fm.com/181-90salt_128k.mp3
	// http://listen.181fm.com/181-hairband_128k.mp3
	
	private static final String RADIO_STREAM = "http://icecast.omroep.nl:80/radio4-bb-mp3";
	private RadioPlayer player;

	@Before
	public void setUp() {
		player = new RadioPlayer();
		MediaMetaSubject mms = new MediaMetaNotifier();
		mms.registerObserver(new PrintRadioPlayerObserver());
		player.addEventsListener(new RadioPlayerEventListener(player, mms));
	}

	@Test
	public void testPlayerInStream() {
		// Create a Thread to play the song
		Thread playerThread = new Thread(() -> player.play(RADIO_STREAM));
		// Start the RadioPlayer:
		playerThread.start();
//		player.setVolume(80);
		try {
			// Forever
			playerThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// Stop player
		player.stop();
	}
}
