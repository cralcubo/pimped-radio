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
	public void testGetCoverLink() throws IOException {
		String song = "In bloom";
		String artist = "Nirvana";
		
		// Mock the albums found 
		Album a1 = new Album.Builder().title("Nevermind").mbid("1").build();
		Album a2 = new Album.Builder().title("Nevermind").mbid("2").build();
		when(albumFinder.findAlbums(song, artist)).thenReturn(Arrays.asList(a1, a2));
		
		// Find the cover arts
		String linkMocked = "http://coverartarchive.org/release/12345MBID/1357.jpg";
		when(coverFinder.findCoverUrl("1")).thenReturn(Optional.of(linkMocked));
		
		// Run the method to test
		Optional<String> coverLink = manager.getCoverUrl(song, artist);
		
		assertThat(coverLink.get(), is(equalTo(linkMocked)));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testGetCoverLink_LastMBIDs() throws IOException {
		String song = "In bloom";
		String artist = "Nirvana";
		
		// Mock the albums found 
		Album a1 = new Album.Builder().title("Nevermind").mbid("1").build();
		Album a2 = new Album.Builder().title("Nevermind").mbid("2").build();
		Album a3 = new Album.Builder().title("Nevermind").mbid("3").build();
		Album a4 = new Album.Builder().title("Nevermind").mbid("4").build();
		Album a5 = new Album.Builder().title("Nirvana Unplugged").mbid("5").build();
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
		Optional<String> coverLink = manager.getCoverUrl(song, artist);
		
		assertThat(coverLink.get(), is(equalTo(linkMocked)));
	}
	
	@Test
	public void testGetNoCoverLink_NoMBIDs() throws IOException {
		String song = "In bloom";
		String artist = "Nirvana";
		
		// Mock the albums found (No Albums) 
		when(albumFinder.findAlbums(song, artist)).thenReturn(Collections.emptyList());
		
		// No request sent to find covers
		Optional<String> coverLink = manager.getCoverUrl(song, artist);
		
		assertThat(coverLink.isPresent(), is(false));
	}
	
	@Test
	public void testGetNoCoverLink_NoCoverArt() throws IOException {
		String song = "In bloom";
		String artist = "Nirvana";
		
		// Mock the albums found 
		Album a1 = new Album.Builder().title("Nevermind").mbid("1").build();
		Album a2 = new Album.Builder().title("Nevermind").mbid("2").build();
		when(albumFinder.findAlbums(song, artist)).thenReturn(Arrays.asList(a1, a2));
		
		// Find the cover arts
		when(coverFinder.findCoverUrl("1")).thenReturn(Optional.empty());
		when(coverFinder.findCoverUrl("2")).thenReturn(Optional.ofNullable(null));
		
		// Run the method to test
		Optional<String> coverLink = manager.getCoverUrl(song, artist);
		
		assertThat(coverLink.isPresent(), is(false));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testGetNoCoverLink_NoCoverArt404() throws IOException {
		String song = "In bloom";
		String artist = "Nirvana";
		
		// Mock the albums found 
		Album a1 = new Album.Builder().title("Nevermind").mbid("1").build();
		Album a2 = new Album.Builder().title("Nevermind").mbid("2").build();
		when(albumFinder.findAlbums(song, artist)).thenReturn(Arrays.asList(a1, a2));
		
		// Find the cover arts
		when(coverFinder.findCoverUrl("1")).thenThrow(ClientProtocolException.class); // 404 from Server
		when(coverFinder.findCoverUrl("2")).thenThrow(ClientProtocolException.class); // 404 from Server
		
		// Run the method to test
		Optional<String> coverLink = manager.getCoverUrl(song, artist);
		
		assertThat(coverLink.isPresent(), is(false));
	}
	
	@Test
	public void testRadioPlayerIcon() {
		Optional<String> playerLink = manager.getRadioPlayerPath();
		assertThat(playerLink.isPresent(), is(true));
		
		String link = playerLink.get();
		File file = Paths.get(link).toFile();
		assertThat(file.exists(), is(true));
	}

}
