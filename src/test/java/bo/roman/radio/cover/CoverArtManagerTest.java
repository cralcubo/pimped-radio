package bo.roman.radio.cover;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import org.apache.http.client.ClientProtocolException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import bo.roman.radio.cover.model.Album;
import bo.roman.radio.cover.model.Radio;

@RunWith(MockitoJUnitRunner.class)
public class CoverArtManagerTest {
	private CoverArtManager manager;
	
	// Mocks
	@Mock
	private AlbumFindable albumFinder;
	@Mock
	private CoverArtFindable coverFinder;
	
	@Before
	public void setUp() {
		manager = new CoverArtManager(albumFinder, coverFinder);
	}
	
	@Test
	public void testGetAlbumWithCover() throws IOException {
		String song = "In bloom";
		String artist = "Nirvana";
		
		// Mock the albums found 
		Album a1 = new Album.Builder().name("Nevermind").mbid("1").build();
		Album a2 = new Album.Builder().name("Nevermind").mbid("2").build();
		when(albumFinder.findAlbums(song, artist)).thenReturn(Arrays.asList(a1, a2));
		
		// Find the cover arts
		String linkMocked = "http://coverartarchive.org/release/12345MBID/1357.jpg";
		when(coverFinder.findCoverUrl("1")).thenReturn(Optional.of(linkMocked));
		
		// Run the method to test
		Optional<Album> oAlbum = manager.getAlbumWithCover(song, artist);
		
		// Assert
		assertAlbumIsPresent(oAlbum, artist, song, linkMocked);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testGetAlbumWithCover_LastMBIDs() throws IOException {
		String song = "In bloom";
		String artist = "Nirvana";
		
		// Mock the albums found 
		Album a1 = new Album.Builder().name("Nevermind").mbid("1").build();
		Album a2 = new Album.Builder().name("Nevermind").mbid("2").build();
		Album a3 = new Album.Builder().name("Nevermind").mbid("3").build();
		Album a4 = new Album.Builder().name("Nevermind").mbid("4").build();
		Album a5 = new Album.Builder().name("Nirvana Unplugged").mbid("5").build();
		when(albumFinder.findAlbums(song, artist)).thenReturn(Arrays.asList(a1, a2, a3, a4, a5));
		
		// Find the cover arts
		// The first 04 cover albums MBID does not have a cover link, just the 05th one. 
		when(coverFinder.findCoverUrl("1")).thenReturn(Optional.empty());
		when(coverFinder.findCoverUrl("2")).thenThrow(ClientProtocolException.class);
		when(coverFinder.findCoverUrl("3")).thenReturn(Optional.empty());
		when(coverFinder.findCoverUrl("4")).thenThrow(ClientProtocolException.class);
		
		String linkMocked = "http://coverartarchive.org/release/12345MBID/1357.jpg";
		when(coverFinder.findCoverUrl("5")).thenReturn(Optional.of(linkMocked));
		
		// Run the method to test
		Optional<Album> oAlbum = manager.getAlbumWithCover(song, artist);
		
		//Assert
		assertAlbumIsPresent(oAlbum, artist, song, linkMocked);
	}
	
	@Test
	public void testGetNoAlbumWithCover_NoMBIDs() throws IOException {
		String song = "In bloom";
		String artist = "Nirvana";
		
		// Mock the albums found (No Albums) 
		when(albumFinder.findAlbums(song, artist)).thenReturn(Collections.emptyList());
		
		// Run the method to test
		Optional<Album> album = manager.getAlbumWithCover(song, artist);
		// Assertions
		assertThat(album.isPresent(), is(false));
	}
	
	@Test
	public void testGetNoAlbumWithCover_NoCoverArt() throws IOException {
		String song = "In bloom";
		String artist = "Nirvana";
		
		// Mock the albums found 
		Album a1 = new Album.Builder().name("Nevermind").mbid("1").build();
		Album a2 = new Album.Builder().name("Nevermind").mbid("2").build();
		when(albumFinder.findAlbums(song, artist)).thenReturn(Arrays.asList(a1, a2));
		
		// Find the cover arts
		when(coverFinder.findCoverUrl("1")).thenReturn(Optional.empty());
		when(coverFinder.findCoverUrl("2")).thenReturn(Optional.ofNullable(null));
		
		// Run the method to test
		Optional<Album> album = manager.getAlbumWithCover(song, artist);
		// Assertions
		assertThat(album.isPresent(), is(false));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testGetNoAlbumWithCover_NoCoverArt404() throws IOException {
		String song = "In bloom";
		String artist = "Nirvana";
		
		// Mock the albums found 
		Album a1 = new Album.Builder().name("Nevermind").mbid("1").build();
		Album a2 = new Album.Builder().name("Nevermind").mbid("2").build();
		when(albumFinder.findAlbums(song, artist)).thenReturn(Arrays.asList(a1, a2));
		
		// Find the cover arts
		when(coverFinder.findCoverUrl("1")).thenThrow(ClientProtocolException.class); // 404 from Server
		when(coverFinder.findCoverUrl("2")).thenThrow(ClientProtocolException.class); // 404 from Server
		
		// Run the method to test
		Optional<Album> album = manager.getAlbumWithCover(song, artist);
		// Assertions
		assertThat(album.isPresent(), is(false));
	}
	
	@Test
	public void testGetRadio() {
		String radioName = "aRadio";
		
		Optional<Radio> oRadio = manager.getRadioWithCover(radioName);
		
		// Assertions
		assertThat(oRadio.isPresent(), is(true));
		assertThat(oRadio.get().getName(), is(equalTo(radioName)));
		
		String link = oRadio.get().getCoverUrl();
		File file = Paths.get(link).toFile();
		assertThat(file.exists(), is(true));
	}
	
	@Test
	public void testNoRadio_empty() {
		Optional<Radio> oRadio = manager.getRadioWithCover("");
		// Assertions
		assertThat(oRadio.isPresent(), is(false));
	}
	
	@Test
	public void testNoRadio_null() {
		Optional<Radio> oRadio = manager.getRadioWithCover(null);
		// Assertions
		assertThat(oRadio.isPresent(), is(false));
	}
	
	@Test
	public void testNoAlbumReturned_noSong() {
		Optional<Album> emptyAlbum1 = manager.getAlbumWithCover(null, "anArtist");
		assertThat(emptyAlbum1.isPresent(), is(false));
		
		Optional<Album> emptyAlbum2 = manager.getAlbumWithCover("", "anArtist");
		assertThat(emptyAlbum2.isPresent(), is(false));
		
		Optional<Album> emptyAlbum3 = manager.getAlbumWithCover(" ", "anArtist");
		assertThat(emptyAlbum3.isPresent(), is(false));
	}
	
	@Test
	public void testNoAlbumReturned_noArtist() {
		Optional<Album> emptyAlbum1 = manager.getAlbumWithCover("aSong", null);
		assertThat(emptyAlbum1.isPresent(), is(false));
		
		Optional<Album> emptyAlbum2 = manager.getAlbumWithCover("aSong", "");
		assertThat(emptyAlbum2.isPresent(), is(false));
		
		Optional<Album> emptyAlbum3 = manager.getAlbumWithCover("aSong", " ");
		assertThat(emptyAlbum3.isPresent(), is(false));
	}
	
	@Test
	public void testNoAlbumReturned_noArtist_noSong() {
		Optional<Album> emptyAlbum1 = manager.getAlbumWithCover("", null);
		assertThat(emptyAlbum1.isPresent(), is(false));
		
		Optional<Album> emptyAlbum2 = manager.getAlbumWithCover(null, "");
		assertThat(emptyAlbum2.isPresent(), is(false));
		
		Optional<Album> emptyAlbum3 = manager.getAlbumWithCover(" ", " ");
		assertThat(emptyAlbum3.isPresent(), is(false));
		
		Optional<Album> emptyAlbum4 = manager.getAlbumWithCover(null, null);
		assertThat(emptyAlbum4.isPresent(), is(false));
	}
	
	/* *** Utilities *** */
	
	private void assertAlbumIsPresent(Optional<Album> oAlbum, String artist, String song, String url) {
		assertThat(oAlbum.isPresent(), is(true));
		assertThat(oAlbum.get().getArtistName(), is(equalTo(artist)));
		assertThat(oAlbum.get().getSongName(), is(equalTo(song)));
		assertThat(oAlbum.get().getCoverUrl(), is(equalTo(url)));
	}
	
	
	

}
