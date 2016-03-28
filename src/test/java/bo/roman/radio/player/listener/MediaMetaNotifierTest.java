package bo.roman.radio.player.listener;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import bo.roman.radio.cover.RadioCoverInterface;
import bo.roman.radio.cover.model.Album;
import bo.roman.radio.cover.model.Radio;
import bo.roman.radio.cover.model.Song;
import bo.roman.radio.player.model.RadioPlayerEntity;
import bo.roman.radio.utilities.MediaMetaUtils;
import uk.co.caprica.vlcj.player.MediaMeta;

@RunWith(PowerMockRunner.class)
@PrepareForTest(MediaMetaUtils.class)
public class MediaMetaNotifierTest {
	
	private MediaMetaNotifier notifier;
	
	@Mock
	private MediaMeta meta;
	
	@Mock
	private RadioCoverInterface radioCover;
	
	@Mock
	private MediaMetaObserver observer;
	
	@Before
	public void setUp() {
		notifier = new MediaMetaNotifier(radioCover);
		notifier.registerObserver(observer);
		
		PowerMockito.mockStatic(MediaMetaUtils.class);
	}
	
	@Test
	public void testNotifyObservers() throws URISyntaxException {
		String radioName = "Test Radio";
		String songName = "aSong";
		String artist = "anArtist";
		URI testRadioLogo = new URI("/a/radio/logo.jpg");
		
		Optional<String> oRadioName = Optional.of(radioName);
		PowerMockito.when(MediaMetaUtils.findRadioName(meta)).thenReturn(oRadioName);
		Song testSong = new Song.Builder()
				.artist(artist)
				.name(songName)
				.build();
		
		Optional<Song> oSong = Optional.of(testSong);
		PowerMockito.when(MediaMetaUtils.buildSong(meta)).thenReturn(oSong);
		
		Radio testRadio = new Radio(radioName, testRadioLogo);
		Optional<Radio> oRadio = Optional.ofNullable(testRadio );
		when(radioCover.getRadioWithLogo(radioName)).thenReturn(oRadio);
		
		Optional<Album> oAlbum = Optional.of(new Album.Builder().artistName(artist).songName(songName).build());
		when(radioCover.getAlbumWithCoverAsync(songName, artist)).thenReturn(oAlbum);
		
		notifier.notifyObservers(meta);
		
		RadioPlayerEntity rpe = new RadioPlayerEntity(oRadio, oSong, oAlbum);
		verify(observer, times(1)).update(rpe);
	}
	
	@Test
	public void testNotifyObservers_noRadio() {
		String songName = "aSong";
		String artist = "anArtist";
		
		Optional<String> oRadioName = Optional.empty();
		PowerMockito.when(MediaMetaUtils.findRadioName(meta)).thenReturn(oRadioName);
		Song testSong = new Song.Builder()
				.artist(artist)
				.name(songName)
				.build();
		
		Optional<Song> oSong = Optional.of(testSong);
		PowerMockito.when(MediaMetaUtils.buildSong(meta)).thenReturn(oSong);
		
		Optional<Radio> oRadio = Optional.empty();
		when(radioCover.getRadioWithLogo("")).thenReturn(oRadio);
		
		Optional<Album> oAlbum = Optional.of(new Album.Builder().artistName(artist).songName(songName).build());
		when(radioCover.getAlbumWithCoverAsync(songName, artist)).thenReturn(oAlbum);
		
		notifier.notifyObservers(meta);
		
		RadioPlayerEntity rpe = new RadioPlayerEntity(oRadio, oSong, oAlbum);
		verify(observer, times(1)).update(rpe);
	}
	
	@Test
	public void testNotifyObservers_noSong() throws URISyntaxException {
		String radioName = "Test Radio";
		URI testRadioLogo = new URI("/a/radio/logo.jpg");
		
		Optional<String> oRadioName = Optional.of(radioName);
		PowerMockito.when(MediaMetaUtils.findRadioName(meta)).thenReturn(oRadioName);
		
		Optional<Song> oSong = Optional.empty();
		PowerMockito.when(MediaMetaUtils.buildSong(meta)).thenReturn(oSong);
		
		Radio testRadio = new Radio(radioName, testRadioLogo);
		Optional<Radio> oRadio = Optional.ofNullable(testRadio );
		when(radioCover.getRadioWithLogo(radioName)).thenReturn(oRadio);
		
		Optional<Album> oAlbum = Optional.empty();
		when(radioCover.getAlbumWithCoverAsync(null, null)).thenReturn(oAlbum);
		
		notifier.notifyObservers(meta);
		
		RadioPlayerEntity rpe = new RadioPlayerEntity(oRadio, oSong, oAlbum);
		verify(observer, times(1)).update(rpe);
	}
	
	@Test
	public void testNotifyObservers_noSongNoRadio() {
		Optional<String> oRadioName = Optional.empty();
		PowerMockito.when(MediaMetaUtils.findRadioName(meta)).thenReturn(oRadioName);
		
		Optional<Song> oSong = Optional.empty();
		PowerMockito.when(MediaMetaUtils.buildSong(meta)).thenReturn(oSong);
		
		Optional<Radio> oRadio = Optional.empty();
		when(radioCover.getRadioWithLogo("")).thenReturn(oRadio);
		
		Optional<Album> oAlbum = Optional.empty();
		when(radioCover.getAlbumWithCoverAsync(null, null)).thenReturn(oAlbum);
		
		notifier.notifyObservers(meta);
		
		RadioPlayerEntity rpe = new RadioPlayerEntity(oRadio, oSong, oAlbum);
		verify(observer, times(1)).update(rpe);
	}
	
	
	
	
}
