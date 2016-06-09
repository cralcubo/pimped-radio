package bo.roman.radio.player.listener;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import bo.roman.radio.cover.CoverArtManager;
import bo.roman.radio.cover.model.Album;
import bo.roman.radio.cover.model.Radio;
import bo.roman.radio.cover.model.Song;
import bo.roman.radio.player.model.RadioPlayerEntity;

@RunWith(MockitoJUnitRunner.class)
public class RadioInformationFinderTest {

	private RadioInformationFinder finder;

	@Mock
	private CoverArtManager coverManager;

	@Before
	public void setUp() {
		finder = new RadioInformationFinder(coverManager);
	}

	@Test
	public void testFind() {
		String radioName = "testRadio";
		String artist = "Nirvana";
		String songName = "In Bloom";
		String albumName = "Nevermind";

		Optional<String> oRadioName = Optional.of(radioName);
		Song song = new Song.Builder().artist(artist).name(songName).build();

		Optional<Song> oSong = Optional.of(song);
		Optional<Album> oAlbum = Optional
				.of(new Album.Builder().artistName(artist).songName(songName).name(albumName).build());
		Optional<Radio> oRadio = Optional.of(new Radio.Builder().name(radioName).build());
		// Mock
		Mockito.when(coverManager.getAlbumWithCover(songName, artist)).thenReturn(oAlbum);
		Mockito.when(coverManager.getRadioWithLogo(radioName)).thenReturn(oRadio);

		RadioPlayerEntity rpe = finder.find(oRadioName, oSong);

		// Assertions
		assertThat(rpe, is(new RadioPlayerEntity(oRadio, oSong, oAlbum)));
	}

	@Test
	public void testFind_noInfo() {
		// Mock
		Mockito.when(coverManager.getAlbumWithCover("", "")).thenReturn(Optional.empty());
		Mockito.when(coverManager.getRadioWithLogo("")).thenReturn(Optional.empty());

		RadioPlayerEntity rpe = finder.find(Optional.empty(), Optional.empty());
		// Assertions
		assertThat(rpe, is(new RadioPlayerEntity(Optional.empty(), Optional.empty(), Optional.empty())));
	}

	@Test
	public void testFind_noSong() {
		String radioName = "aRadio";
		Optional<Radio> oRadio = Optional.of(new Radio.Builder().name(radioName).build());
		// Mock
		Mockito.when(coverManager.getAlbumWithCover("", "")).thenReturn(Optional.empty());
		Mockito.when(coverManager.getRadioWithLogo(radioName)).thenReturn(oRadio);

		RadioPlayerEntity rpe = finder.find(Optional.of(radioName), Optional.empty());
		// Assertions
		assertThat(rpe, is(new RadioPlayerEntity(oRadio, Optional.empty(), Optional.empty())));
	}

	@Test
	public void testFind_noRadio() {
		String artist = "Nirvana";
		String songName = "In Bloom";
		String albumName = "Nevermind";
		Song song = new Song.Builder().artist(artist).name(songName).build();

		Optional<Song> oSong = Optional.of(song);
		Optional<Album> oAlbum = Optional.of(new Album.Builder().artistName(artist).songName(songName).name(albumName).build());
		// Mock
		Mockito.when(coverManager.getAlbumWithCover(songName, artist)).thenReturn(oAlbum);
		Mockito.when(coverManager.getRadioWithLogo("")).thenReturn(Optional.empty());

		RadioPlayerEntity rpe = finder.find(Optional.empty(), oSong);
		// Assertions
		assertThat(rpe, is(new RadioPlayerEntity(Optional.empty(), oSong, oAlbum)));
	}

}
