package bo.roman.radio.cover;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import org.apache.http.client.ClientProtocolException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bo.roman.radio.cover.album.AlbumFindable;
import bo.roman.radio.cover.album.CoverArtFindable;
import bo.roman.radio.cover.model.Album;
import bo.roman.radio.cover.station.CacheLogoUtil;
import bo.roman.radio.cover.station.RadioStationFindable;

@RunWith(PowerMockRunner.class)
@PrepareForTest(CacheLogoUtil.class)
public class CoverArtManagerThreadsTest {
	
private final Logger log = LoggerFactory.getLogger(CoverArtManagerThreadsTest.class);
	
	private CoverArtManager manager;
	
	// Mocks
	@Mock
	private AlbumFindable albumFinder;
	@Mock
	private CoverArtFindable coverFinder;
	@Mock
	private RadioStationFindable radioFinder;
	
	@Before
	public void setUp() throws Exception {
		manager = new CoverArtManager(albumFinder, coverFinder, radioFinder);
		PowerMockito.mockStatic(CacheLogoUtil.class);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testGetAlbumWithCoverAsync_threeSongsSimultaneously() throws Exception {
		
		// First thread mock search
		String song1 = "In Bloom";
		String artist1 = "Nirvana";
		Album a1 = new Album.Builder().name("Nevermind").mbid("1").build();
		Album a2 = new Album.Builder().name("Nevermind").mbid("2").build();
		Album a3 = new Album.Builder().name("Nevermind").mbid("3").build();
		
		when(albumFinder.findAlbums(song1, artist1)).thenAnswer(new AlbumListAnswer(1000, Arrays.asList(a1, a2, a3), song1, artist1));
		when(coverFinder.findCoverUrl("1")).thenReturn(Optional.empty());
		when(coverFinder.findCoverUrl("2")).thenThrow(ClientProtocolException.class);
		String link1 = "http://coverartarchive.org/release/12345MBID/1357.jpg";
		when(coverFinder.findCoverUrl("3")).thenReturn(Optional.of(link1));
		
		// Second thread mock search
		String song3 = "Imagine";
		String artist3 = "John Lennon";
		Album a31 = new Album.Builder().name("Imagine").mbid("10").build();
		Album a32 = new Album.Builder().name("Imagine").mbid("20").build();
		Album a33 = new Album.Builder().name("Imagine").mbid("30").build();
		when(albumFinder.findAlbums(song3, artist3)).thenAnswer(new AlbumListAnswer(500, Arrays.asList(a31, a32, a33), song3, artist3));
		String link31 = "http://link/1";
		when(coverFinder.findCoverUrl("10")).thenReturn(Optional.of(link31));
		when(coverFinder.findCoverUrl("20")).thenThrow(ClientProtocolException.class);
		String link33 = "http://link/3";
		when(coverFinder.findCoverUrl("30")).thenReturn(Optional.of(link33));
		
		// Send two threads consecutively to find the albums
		List<Optional<Album>> albums = new ArrayList<>();
		
		Thread t1 = new Thread(() -> albums.add(manager.getAlbumWithCoverAsync(song1, artist1)));
		Thread t2 = new Thread(() -> albums.add(manager.getAlbumWithCoverAsync(null, null)));
		Thread t3 = new Thread(() -> albums.add(manager.getAlbumWithCoverAsync(song3, artist3)));
		
		t1.start();
		t1.join();
		t2.start();
		t2.join();
		t3.start();
		t3.join();
		
		// Asserts
		Optional<Album> oAlbum1 = albums.get(0);
		assertAlbumIsPresent(oAlbum1, artist1, song1, new URL(link1));
		Optional<Album> oAlbum2 = albums.get(1);
		assertThat(oAlbum2.isPresent(), is(false));
		Optional<Album> oAlbum3 = albums.get(2);
		assertAlbumIsPresent(oAlbum3, artist3, song3, new URL(link31));
		
		/*
		CompletableFuture<Optional<Album>> futAlb1 = CompletableFuture.supplyAsync(() -> manager.getAlbumWithCoverAsync(song1, artist1));
		futAlb1.thenAccept(oa -> albums.add(oa));
		delay(1000);
		CompletableFuture<Optional<Album>> futAlb2 = CompletableFuture.supplyAsync(() -> manager.getAlbumWithCoverAsync("", null));
		futAlb2.thenAccept(oa -> albums.add(oa));
		delay(1000);
		CompletableFuture<Optional<Album>> futAlb3 = CompletableFuture.supplyAsync(() -> manager.getAlbumWithCoverAsync(song3, artist3));
		futAlb3.thenAccept(oa -> albums.add(oa));
		
		// Assertions
		Optional<Album> oAlbum1 = futAlb1.join();
		assertAlbumIsPresent(oAlbum1, artist1, song1, new URL(link1));
		Optional<Album> oAlbum2 = futAlb2.join();
		assertThat(oAlbum2.isPresent(), is(false));
		Optional<Album> oAlbum3 = futAlb3.join();
		assertAlbumIsPresent(oAlbum3, artist3, song3, new URL(link31));
		
		// Assert the order expected
		assertThat("Order unexpected.", albums.get(0), is(equalTo(oAlbum1)));
		assertThat("Order unexpected.", albums.get(1), is(equalTo(oAlbum2)));
		assertThat("Order unexpected.", albums.get(2), is(equalTo(oAlbum3)));*/
	}
	
	/* *** Utilities *** */
	
    private void delay(int i) {
		try {
			Thread.sleep(i);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private void assertAlbumIsPresent(Optional<Album> oAlbum, String artist, String song, URL url) throws MalformedURLException {
		assertThat(oAlbum.isPresent(), is(true));
		assertThat(oAlbum.get().getArtistName(), is(equalTo(artist)));
		assertThat(oAlbum.get().getSongName(), is(equalTo(song)));
		assertThat(oAlbum.get().getCoverUri().get().toURL(), is(equalTo(url)));
	}
	
	private class AlbumListAnswer implements Answer<List<Album>> {
		
		private final long delay;
		private final List<Album> response;
		private final String song;
		private final String artist;
		
		public AlbumListAnswer(long delay, List<Album> response, String song, String artist) {
			this.delay = delay;
			this.response = response;
			this.song = song;
			this.artist = artist;
		}

		@Override
		public List<Album> answer(InvocationOnMock invocation) throws Throwable {
			log.info("Setting a delay of {} ms. to get all the albums for [{} - {}]", delay, song, artist);
			Thread.sleep(delay);
			return response;
		}
		
	}

}
