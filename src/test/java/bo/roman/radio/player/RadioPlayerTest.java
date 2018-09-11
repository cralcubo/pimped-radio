package bo.roman.radio.player;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import uk.co.caprica.vlcj.player.MediaPlayer;

@RunWith(MockitoJUnitRunner.class)
public class RadioPlayerTest {

	private IRadioPlayer radioPlayer;
	@Mock
	private MediaPlayer mediaPlayer;

	@Before
	public void setUp() {
		radioPlayer = new RadioPlayer(mediaPlayer);
	}

	@Test
	public void testPlay() {
		String aUrl = "testUrl";

		Mockito.when(mediaPlayer.playMedia(aUrl)).thenReturn(true);

		Thread t = new Thread(() -> radioPlayer.play(aUrl));
		t.start();
		t.interrupt();
	}

	@Test
	public void testStop() {
		radioPlayer.stop();
		Mockito.verify(mediaPlayer).stop();
	}

	@Test
	public void testClose() {
		radioPlayer.close();
		Mockito.verify(mediaPlayer).release();
	}

	@Test
	public void testSetVol() {
		int vol = 15;
		radioPlayer.setVolume(vol);
		Mockito.verify(mediaPlayer).setVolume(vol);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSetVol_over() {
		int vol = 115;
		radioPlayer.setVolume(vol);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSetVol_under() {
		int vol = -15;
		radioPlayer.setVolume(vol);
	}

}
