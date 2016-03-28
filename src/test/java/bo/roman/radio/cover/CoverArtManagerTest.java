package bo.roman.radio.cover;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import java.util.Random;

import org.apache.http.client.ClientProtocolException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
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
import bo.roman.radio.cover.model.Radio;
import bo.roman.radio.cover.station.CacheLogoUtil;
import bo.roman.radio.cover.station.RadioStationFindable;
import bo.roman.radio.utilities.ReflectionUtils;

@RunWith(PowerMockRunner.class)
@PrepareForTest(CacheLogoUtil.class)
public class CoverArtManagerTest {
	private final Logger log = LoggerFactory.getLogger(CoverArtManagerTest.class);
	
	private CoverArtManager manager;
	
	// Mocks
	@Mock
	private AlbumFindable albumFinder;
	@Mock
	private CoverArtFindable coverFinder;
	@Mock
	private RadioStationFindable radioFinder;
	
	private String testRadioName = "aTestRadio";
	private String testRadioId = "7777";
	
	
	@Before
	public void setUp() throws Exception {
		manager = new CoverArtManager(albumFinder, coverFinder, radioFinder);
		PowerMockito.mockStatic(CacheLogoUtil.class);
	}
	
	@Test
	public void testGetAlbumWithCoverAsync() throws IOException {
		String song = "In bloom";
		String artist = "Nirvana";
		
		// Mock the albums found 
		Album a1 = new Album.Builder().name("Nevermind").mbid("1").build();
		Album a2 = new Album.Builder().name("Nevermind").mbid("2").build();
		Album a3 = new Album.Builder().name("Nevermind").mbid("3").build();
		Album a4 = new Album.Builder().name("Nevermind").mbid("4").build();
		Album a5 = new Album.Builder().name("Nevermind").mbid("5").build();
		Album a6 = new Album.Builder().name("Nevermind").mbid("6").build();
		Album a7 = new Album.Builder().name("Nirvana Unplugged").mbid("7").build();
		Album a8 = new Album.Builder().name("Nirvana Unplugged").mbid("8").build();
		Album a9 = new Album.Builder().name("Nirvana Unplugged").mbid("9").build();
		Album a10 = new Album.Builder().name("Nirvana Unplugged").mbid("10").build();
		when(albumFinder.findAlbums(song, artist)).thenReturn(Arrays.asList(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10));
		
		// Find the cover arts
		Random rnd = new Random(System.currentTimeMillis());
		String linkMocked1 = "http://coverartarchive.org/release/12345MBID/1357.jpg";
		when(coverFinder.findCoverUrl("1")).thenAnswer(new CoverUrlAnswer(linkMocked1, rnd));
		String linkMocked2 = "http://another.mocked/link2.jpg";
		when(coverFinder.findCoverUrl("2")).thenAnswer(new CoverUrlAnswer(linkMocked2, rnd));
		String linkMocked3 = "http://another.mocked/link3.jpg";
		when(coverFinder.findCoverUrl("3")).thenAnswer(new CoverUrlAnswer(linkMocked3, rnd));
		String linkMocked4 = "http://another.mocked/link4.jpg";
		when(coverFinder.findCoverUrl("4")).thenAnswer(new CoverUrlAnswer(linkMocked4, rnd));
		String linkMocked5 = "http://another.mocked/link5.jpg";
		when(coverFinder.findCoverUrl("5")).thenAnswer(new CoverUrlAnswer(linkMocked5, rnd));
		String linkMocked6 = "http://another.mocked/link6.jpg";
		when(coverFinder.findCoverUrl("6")).thenAnswer(new CoverUrlAnswer(linkMocked6, rnd));
		String linkMocked7 = "http://another.mocked/link7.jpg";
		when(coverFinder.findCoverUrl("7")).thenAnswer(new CoverUrlAnswer(linkMocked7, rnd));
		String linkMocked8 = "http://another.mocked/link8.jpg";
		when(coverFinder.findCoverUrl("8")).thenAnswer(new CoverUrlAnswer(linkMocked8, rnd));
		String linkMocked9 = "http://another.mocked/link9.jpg";
		when(coverFinder.findCoverUrl("9")).thenAnswer(new CoverUrlAnswer(linkMocked9, rnd));
		String linkMocked10 = "http://another.mocked/link10.jpg";
		when(coverFinder.findCoverUrl("10")).thenAnswer(new CoverUrlAnswer(linkMocked10, rnd));
		
		// Run the method to test
		Optional<Album> oAlbum = manager.getAlbumWithCoverAsync(song, artist);
		
		// Assert
		assertAlbumIsPresent(oAlbum, artist, song, new URL(linkMocked1));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testGetAlbumWithCover_LastMBIDsAsync() throws IOException {
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
		Optional<Album> oAlbum = manager.getAlbumWithCoverAsync(song, artist);
		
		//Assert
		assertAlbumIsPresent(oAlbum, artist, song, new URL(linkMocked));
	}
	
	@Test
	public void testGetNoAlbumWithCover_NoMBIDsAsync() throws IOException {
		String song = "In bloom";
		String artist = "Nirvana";
		
		// Mock the albums found (No Albums) 
		when(albumFinder.findAlbums(song, artist)).thenReturn(Collections.emptyList());
		
		// Run the method to test
		Optional<Album> album = manager.getAlbumWithCoverAsync(song, artist);
		// Assertions
		assertThat(album.isPresent(), is(false));
	}
	
	@Test
	public void testGetNoAlbumWithCover_NoCoverArtAsync() throws IOException {
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
		Optional<Album> album = manager.getAlbumWithCoverAsync(song, artist);
		// Assertions
		assertThat(album.isPresent(), is(true));
		assertThat(album.get(), is(equalTo(a1)));
	}
	
	@Test
	public void testGetNoAlbumWithCover_NoCoverArtAsunc() throws IOException {
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
		Optional<Album> album = manager.getAlbumWithCoverAsync(song, artist);
		// Assertions
		assertThat(album.isPresent(), is(true));
		assertThat(album.get(), is(equalTo(a1)));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testGetNoAlbumWithCover_NoCoverArt404Async() throws IOException {
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
		Optional<Album> album = manager.getAlbumWithCoverAsync(song, artist);
		// Assertions
		assertThat(album.isPresent(), is(true));
		assertThat(album.get(), is(equalTo(a1)));
	}
	
	@Test
	public void testNoAlbumReturned_noSongAsync() {
		Optional<Album> emptyAlbum1 = manager.getAlbumWithCoverAsync(null, "anArtist");
		assertThat(emptyAlbum1.isPresent(), is(false));
		
		Optional<Album> emptyAlbum2 = manager.getAlbumWithCoverAsync("", "anArtist");
		assertThat(emptyAlbum2.isPresent(), is(false));
		
		Optional<Album> emptyAlbum3 = manager.getAlbumWithCoverAsync(" ", "anArtist");
		assertThat(emptyAlbum3.isPresent(), is(false));
	}
	
	@Test
	public void testNoAlbumReturned_noArtistAsync() {
		Optional<Album> emptyAlbum1 = manager.getAlbumWithCoverAsync("aSong", null);
		assertThat(emptyAlbum1.isPresent(), is(false));
		
		Optional<Album> emptyAlbum2 = manager.getAlbumWithCoverAsync("aSong", "");
		assertThat(emptyAlbum2.isPresent(), is(false));
		
		Optional<Album> emptyAlbum3 = manager.getAlbumWithCoverAsync("aSong", " ");
		assertThat(emptyAlbum3.isPresent(), is(false));
	}
	
	@Test
	public void testNoAlbumReturned_noArtist_noSongAsync() {
		Optional<Album> emptyAlbum1 = manager.getAlbumWithCoverAsync("", null);
		assertThat(emptyAlbum1.isPresent(), is(false));
		
		Optional<Album> emptyAlbum2 = manager.getAlbumWithCoverAsync(null, "");
		assertThat(emptyAlbum2.isPresent(), is(false));
		
		Optional<Album> emptyAlbum3 = manager.getAlbumWithCoverAsync(" ", " ");
		assertThat(emptyAlbum3.isPresent(), is(false));
		
		Optional<Album> emptyAlbum4 = manager.getAlbumWithCoverAsync(null, null);
		assertThat(emptyAlbum4.isPresent(), is(false));
	}
	
	@Test
	public void testGetRadio() throws Exception {
		// Prepare Mock
		PowerMockito.when(CacheLogoUtil.isCached(testRadioName)).thenReturn(false);

		Radio radioFound = new Radio.Builder().id(testRadioId).name(testRadioName).build();
		when(radioFinder.findRadioStation(testRadioName)).thenReturn(Optional.of(radioFound));

		PowerMockito.when(CacheLogoUtil.cacheRadioLogo(testRadioName, radioFound.getLogoUri())).thenReturn(true);
		
		Optional<Radio> oRadio = manager.getRadioWithLogo(testRadioName);
		
		// Assertions
		assertThat(oRadio.isPresent(), is(true));
		assertThat(oRadio.get().getName(), is(equalTo(testRadioName)));
		
		String pageLogoTemplate = (String) ReflectionUtils.getPrivateConstant(radioFound, "PAGELOGO_TEMPLATE");
		URI expectedLogoUrl = new URI(String.format(pageLogoTemplate, testRadioId));
		assertThat(expectedLogoUrl, is(equalTo(oRadio.flatMap(Radio::getLogoUri).get())));
	}
	
	@Test
	public void testGetCachedRadio() throws Exception {
		URI testLogoUri = new URI("a/test/path");
		// Prepare Mock
		PowerMockito.when(CacheLogoUtil.isCached(testRadioName)).thenReturn(true);
		
		Path cachedLogoPath = Mockito.mock(Path.class);
		when(cachedLogoPath.toUri()).thenReturn(testLogoUri);
		PowerMockito.when(CacheLogoUtil.getCachedLogoPath(testRadioName)).thenReturn(cachedLogoPath);
		
		Optional<Radio> oRadio = manager.getRadioWithLogo(testRadioName);
		
		// Asserts
		assertThat("Radio is present", oRadio.isPresent(), is(true));
		Radio r = oRadio.get();
		assertThat(r.getName(), is(equalTo(testRadioName)));
		
		assertThat(r.getLogoUri().get(), is(equalTo(testLogoUri)));
	} 
	
	@Test
	public void testGetRadio_notFound() throws Exception {
		// Prepare Mock
		PowerMockito.when(CacheLogoUtil.isCached(testRadioName)).thenReturn(false);
		
		when(radioFinder.findRadioStation(testRadioName)).thenReturn(Optional.empty());
		
		Optional<Radio> oRadio = manager.getRadioWithLogo(testRadioName);
		
		// Assert
		assertThat(oRadio.isPresent(), is(true));
		
		String defaultLogoPath = (String) ReflectionUtils.getPrivateConstant(manager, "DEFAULTLOGO_PATH");
		Radio expectedRadio = new Radio(testRadioName, new URI(defaultLogoPath));
		Radio radioFound = oRadio.get();
		
		assertThat(radioFound, is(equalTo(expectedRadio)));
		// The default radio has a link to the file with a default
		// radio logo, this logo file must exist.
		String radioLog = oRadio.flatMap(Radio::getLogoUri).map(URI::getPath).get();
		File logoFile = new File(radioLog);
		
		assertTrue("Logo File Path=" + radioLog + " is supposed to exist.", logoFile.exists());
	}
	
	@Test
	public void testNoRadio_empty() {
		Optional<Radio> oRadio = manager.getRadioWithLogo("");
		// Assertions
		assertThat(oRadio.isPresent(), is(false));
	}
	
	@Test
	public void testNoRadio_null() {
		Optional<Radio> oRadio = manager.getRadioWithLogo(null);
		// Assertions
		assertThat(oRadio.isPresent(), is(false));
	}
	
	/* *** Utilities *** */
	
	private void assertAlbumIsPresent(Optional<Album> oAlbum, String artist, String song, URL url) throws MalformedURLException {
		assertThat(oAlbum.isPresent(), is(true));
		assertThat(oAlbum.get().getArtistName(), is(equalTo(artist)));
		assertThat(oAlbum.get().getSongName(), is(equalTo(song)));
		assertThat(oAlbum.get().getCoverUri().get().toURL(), is(equalTo(url)));
	}
	
	/**
	 * Class that will emulate
	 * the call made to retrieve a URL
	 * with the cover art of an album.
	 * 
	 * @author christian
	 *
	 */
	private class CoverUrlAnswer implements Answer<Optional<String>> {
		
		private final String expectedLink;
		private final Random rnd;
		
		public CoverUrlAnswer(String link, Random rnd) {
			this.expectedLink = link;
			this.rnd = rnd;
		}

		@Override
		public Optional<String> answer(InvocationOnMock invocation) throws Throwable {
			// Set a delay between 1 and 3 secs
			long duration = Math.abs(rnd.nextLong() % 2000) + 1000;
			log.info("Setting a delay to emulate the time spent on retrieveing the URL[{}]. Duration {} ms.", expectedLink, duration);
			
			return Optional.of(expectedLink);
		}
		
	}
}
