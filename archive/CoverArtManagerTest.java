package bo.roman.radio.cover;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;

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
import bo.roman.radio.cover.album.AmazonCoverFinder;
import bo.roman.radio.cover.album.CoverArchiveFinder;
import bo.roman.radio.cover.model.Album;
import bo.roman.radio.cover.model.CoverArt;
import bo.roman.radio.cover.model.Radio;
import bo.roman.radio.cover.station.CacheLogoUtil;
import bo.roman.radio.cover.station.RadioStationFindable;
import bo.roman.radio.utilities.ReflectionUtils;

@RunWith(PowerMockRunner.class)
@PrepareForTest(CacheLogoUtil.class)
public class CoverArtManagerTest {
	private final Logger log = LoggerFactory.getLogger(CoverArtManagerTest.class);
	
	private CoverArtManagerImpl manager;
	
	// Mocks
	@Mock
	private AlbumFindable albumFinder;
	@Mock
	private CoverArchiveFinder coverArchiveFinder;
	@Mock
	private AmazonCoverFinder amazonFinder;
	@Mock
	private RadioStationFindable radioFinder;
	
	private String testRadioName = "aTestRadio";
	private String testRadioId = "7777";
	
	
	@Before
	public void setUp() throws Exception {
		manager = new CoverArtManagerImpl(albumFinder, coverArchiveFinder, amazonFinder, radioFinder);
		PowerMockito.mockStatic(CacheLogoUtil.class);
	}
	
	@Test
	public void testGetAlbumWithCoverAsync() throws IOException{
		String song = "In bloom";
		String artist = "Nirvana";
		// Mock the albums found 
		Album a1 = new Album.Builder().name("Nevermind").artistName(artist).songName(song).mbid("1").build();
		Album a2 = new Album.Builder().name("Nevermind").artistName(artist).songName(song).mbid("2").build();
		Album a3 = new Album.Builder().name("Nevermind").artistName(artist).songName(song).mbid("3").build();
		Album a4 = new Album.Builder().name("Nevermind").artistName(artist).songName(song).mbid("4").build();
		Album a5 = new Album.Builder().name("Nevermind").artistName(artist).songName(song).mbid("5").build();
		Album a6 = new Album.Builder().name("Nevermind").artistName(artist).songName(song).mbid("6").build();
		Album a7 = new Album.Builder().name("Nirvana Unplugged").artistName(artist).songName(song).mbid("7").build();
		Album a8 = new Album.Builder().name("Nirvana Unplugged").artistName(artist).songName(song).mbid("8").build();
		Album a9 = new Album.Builder().name("Nirvana Unplugged").artistName(artist).songName(song).mbid("9").build();
		Album a10 = new Album.Builder().name("Nirvana Unplugged").artistName(artist).songName(song).mbid("10").build();
		when(albumFinder.findAlbums(song, artist)).thenReturn(Arrays.asList(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10));
		
		// Find the cover arts
		Random rnd = new Random(System.currentTimeMillis());
		String linkMocked1 = "http://coverartarchive.org/release/12345MBID/1357.jpg";
		when(coverArchiveFinder.findCoverArt(a1)).thenAnswer(new CoverUrlAnswer(linkMocked1, rnd));
		String linkMocked2 = "http://another.mocked/link2.jpg";
		when(coverArchiveFinder.findCoverArt(a2)).thenAnswer(new CoverUrlAnswer(linkMocked2, rnd));
		String linkMocked3 = "http://another.mocked/link3.jpg";
		when(coverArchiveFinder.findCoverArt(a3)).thenAnswer(new CoverUrlAnswer(linkMocked3, rnd));
		String linkMocked4 = "http://another.mocked/link4.jpg";
		when(coverArchiveFinder.findCoverArt(a4)).thenAnswer(new CoverUrlAnswer(linkMocked4, rnd));
		String linkMocked5 = "http://another.mocked/link5.jpg";
		when(coverArchiveFinder.findCoverArt(a5)).thenAnswer(new CoverUrlAnswer(linkMocked5, rnd));
		String linkMocked6 = "http://another.mocked/link6.jpg";
		when(coverArchiveFinder.findCoverArt(a6)).thenAnswer(new CoverUrlAnswer(linkMocked6, rnd));
		String linkMocked7 = "http://another.mocked/link7.jpg";
		when(coverArchiveFinder.findCoverArt(a7)).thenAnswer(new CoverUrlAnswer(linkMocked7, rnd));
		String linkMocked8 = "http://another.mocked/link8.jpg";
		when(coverArchiveFinder.findCoverArt(a8)).thenAnswer(new CoverUrlAnswer(linkMocked8, rnd));
		String linkMocked9 = "http://another.mocked/link9.jpg";
		when(coverArchiveFinder.findCoverArt(a9)).thenAnswer(new CoverUrlAnswer(linkMocked9, rnd));
		String linkMocked10 = "http://another.mocked/link10.jpg";
		when(coverArchiveFinder.findCoverArt(a10)).thenAnswer(new CoverUrlAnswer(linkMocked10, rnd));
		
		// Run the method to test
		Optional<Album> oAlbum = manager.getAlbumWithCoverAsync(song, artist);
		
		// Assert
		assertAlbumIsPresent(oAlbum, artist, song, new URL(linkMocked1));
	}
	
	@Test
	public void testGetAlbumWithCoverAmazonAsync_noArtist() throws IOException {
		String coverUri = "http://aUri/pic.jpg";
		String song = "In bloom by Nirvana";
		String artist= "";
		
		// Mock the albums found (No Albums) 
		when(albumFinder.findAlbums(song, artist)).thenReturn(Collections.emptyList());
		
		// Mock Amazon Finder
		CoverArt ca = new CoverArt.Builder().mediumUri(coverUri).build();
		when(amazonFinder.findCoverArt(new Album.Builder().songName(song).artistName(artist).name("").build())).thenReturn(Optional.of(ca));
		
		// Run the method to test
		Optional<Album> oAlbum = manager.getAlbumWithCoverAsync(song, artist);
		
		// Assertions
		assertThat(oAlbum.isPresent(), is(true));
		Album album = oAlbum.get();
		assertThat(album.getName(), is(equalTo("")));
		assertThat(album.getSongName(), is(equalTo(song)));
		assertThat(album.getArtistName(), is(equalTo("")));
		assertThat(album.getCoverArt().get(), is(equalTo(ca)));
	}
	
	@Test
	public void testGetAlbumWithCoverAmazonAsync() throws IOException{
		String song = "In bloom";
		String artist = "Nirvana";
		// Mock the albums found 
		Album a1 = new Album.Builder().name("Nevermind").artistName(artist).songName(song).mbid("1").build();
		Album a2 = new Album.Builder().name("Nevermind").artistName(artist).songName(song).mbid("2").build();
		Album a3 = new Album.Builder().name("Nevermind").artistName(artist).songName(song).mbid("3").build();
		Album a4 = new Album.Builder().name("Nevermind").artistName(artist).songName(song).mbid("4").build();
		Album a5 = new Album.Builder().name("Nevermind").artistName(artist).songName(song).mbid("5").build();
		Album a6 = new Album.Builder().name("Nevermind").artistName(artist).songName(song).mbid("6").build();
		Album a7 = new Album.Builder().name("Nirvana Unplugged").artistName(artist).songName(song).mbid("7").build();
		Album a8 = new Album.Builder().name("Nirvana Unplugged").artistName(artist).songName(song).mbid("8").build();
		Album a9 = new Album.Builder().name("Nirvana Unplugged").artistName(artist).songName(song).mbid("9").build();
		Album a10 = new Album.Builder().name("Nirvana Unplugged").artistName(artist).songName(song).mbid("10").build();
		when(albumFinder.findAlbums(song, artist)).thenReturn(Arrays.asList(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10));
		
		// No cover arts found in CoverArtArchive mock
		Random rnd = new Random(System.currentTimeMillis());
		when(coverArchiveFinder.findCoverArt(a1)).thenAnswer(new CoverUrlAnswer(Optional.empty(), rnd));
		when(coverArchiveFinder.findCoverArt(a2)).thenAnswer(new CoverUrlAnswer(Optional.empty(), rnd));
		when(coverArchiveFinder.findCoverArt(a3)).thenAnswer(new CoverUrlAnswer(Optional.empty(), rnd));
		when(coverArchiveFinder.findCoverArt(a4)).thenAnswer(new CoverUrlAnswer(Optional.empty(), rnd));
		when(coverArchiveFinder.findCoverArt(a5)).thenAnswer(new CoverUrlAnswer(Optional.empty(), rnd));
		when(coverArchiveFinder.findCoverArt(a6)).thenAnswer(new CoverUrlAnswer(Optional.empty(), rnd));
		when(coverArchiveFinder.findCoverArt(a7)).thenAnswer(new CoverUrlAnswer(Optional.empty(), rnd));
		when(coverArchiveFinder.findCoverArt(a8)).thenAnswer(new CoverUrlAnswer(Optional.empty(), rnd));
		when(coverArchiveFinder.findCoverArt(a9)).thenAnswer(new CoverUrlAnswer(Optional.empty(), rnd));
		when(coverArchiveFinder.findCoverArt(a10)).thenAnswer(new CoverUrlAnswer(Optional.empty(), rnd));
		
		// Albums expected by Amazon
		String linkMocked1 = "http://coverartarchive.org/release/12345MBID/1357.jpg";
		when(amazonFinder.findCoverArt(a1)).thenAnswer(new CoverUrlAnswer(linkMocked1, rnd));
		String linkMocked7 = "http://another.mocked/link7.jpg";
		when(amazonFinder.findCoverArt(a7)).thenAnswer(new CoverUrlAnswer(linkMocked7, rnd));
		
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
		Album a1 = new Album.Builder().name("Nevermind").songName(song).artistName(artist).mbid("1").build();
		Album a2 = new Album.Builder().name("Nevermind").songName(song).artistName(artist).mbid("2").build();
		Album a3 = new Album.Builder().name("Nevermind").songName(song).artistName(artist).mbid("3").build();
		Album a4 = new Album.Builder().name("Nevermind").songName(song).artistName(artist).mbid("4").build();
		Album a5 = new Album.Builder().name("Nirvana Unplugged").songName(song).artistName(artist).mbid("5").build();
		when(albumFinder.findAlbums(song, artist)).thenReturn(Arrays.asList(a1, a2, a3, a4, a5));
		
		// Find the cover arts
		// The first 04 cover albums MBID does not have a cover link, just the 05th one. 
		when(coverArchiveFinder.findCoverArt(a1)).thenReturn(Optional.empty());
		when(coverArchiveFinder.findCoverArt(a2)).thenThrow(ClientProtocolException.class);
		when(coverArchiveFinder.findCoverArt(a3)).thenReturn(Optional.empty());
		when(coverArchiveFinder.findCoverArt(a4)).thenThrow(ClientProtocolException.class);
		
		String linkMocked = "http://coverartarchive.org/release/12345MBID/1357.jpg";
		when(coverArchiveFinder.findCoverArt(a5)).thenReturn(Optional.of(new CoverArt.Builder().largeUri(linkMocked).build()));
		
		// Run the method to test
		Optional<Album> oAlbum = manager.getAlbumWithCoverAsync(song, artist);
		
		//Assert
		assertAlbumIsPresent(oAlbum, artist, song, new URL(linkMocked));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testGetAlbumWithCoverAmazon_LastMBIDsAsync() throws IOException {
		String song = "In bloom";
		String artist = "Nirvana";
		
		// Mock the albums found 
		Album a1 = new Album.Builder().name("Nevermind").songName(song).artistName(artist).mbid("1").build();
		Album a2 = new Album.Builder().name("Nevermind").songName(song).artistName(artist).mbid("2").build();
		Album a3 = new Album.Builder().name("Nevermind").songName(song).artistName(artist).mbid("3").build();
		Album a4 = new Album.Builder().name("Nevermind").songName(song).artistName(artist).mbid("4").build();
		Album a5 = new Album.Builder().name("Nirvana Unplugged").songName(song).artistName(artist).mbid("5").build();
		Album a6 = new Album.Builder().name("Nirvana Ultimate").songName(song).artistName(artist).mbid("6").build();
				
		when(albumFinder.findAlbums(song, artist)).thenReturn(Arrays.asList(a1, a2, a3, a4, a5, a6));
		
		// CoverArtArchive Finder mock
		when(coverArchiveFinder.findCoverArt(a1)).thenReturn(Optional.empty());
		when(coverArchiveFinder.findCoverArt(a2)).thenReturn(Optional.empty());
		when(coverArchiveFinder.findCoverArt(a3)).thenReturn(Optional.empty());
		when(coverArchiveFinder.findCoverArt(a4)).thenReturn(Optional.empty());
		when(coverArchiveFinder.findCoverArt(a5)).thenReturn(Optional.empty());
		when(coverArchiveFinder.findCoverArt(a6)).thenReturn(Optional.empty());
		
		// Amazon Finder Mock
		when(amazonFinder.findCoverArt(a1)).thenThrow(ClientProtocolException.class);
		when(amazonFinder.findCoverArt(a5)).thenReturn(Optional.empty());
		String linkMocked = "http://coverartarchive.org/release/12345MBID/1357.jpg";
		when(amazonFinder.findCoverArt(a6)).thenReturn(Optional.of(new CoverArt.Builder().largeUri(linkMocked).build()));
		
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
		
		// Mock Amazon Finder
		when(amazonFinder.findCoverArt(new Album.Builder().songName(song).artistName(artist).name("").build())).thenReturn(Optional.empty());
		
		// Run the method to test
		Optional<Album> album = manager.getAlbumWithCoverAsync(song, artist);
		// Assertions
		assertThat(album.isPresent(), is(false));
	}
	
	@Test
	public void testGetAlbumWithCover_NoMBIDsAsyncAmazon() throws IOException {
		String song = "In bloom";
		String artist = "Nirvana";
		
		// Mock the albums found (No Albums) 
		when(albumFinder.findAlbums(song, artist)).thenReturn(Collections.emptyList());
		
		String linkMocked = "http://coverartarchive.org/release/12345MBID/1357.jpg";
		CoverArt cover = new CoverArt.Builder().largeUri(linkMocked).build();
		// Mock Amazon Finder
		when(amazonFinder.findCoverArt(new Album.Builder().songName(song).artistName(artist).name("").build())).thenReturn(Optional.of(cover ));
		
		// Run the method to test
		Optional<Album> album = manager.getAlbumWithCoverAsync(song, artist);
		
		// Assertions
		assertAlbumIsPresent(album, artist, song, new URL(linkMocked));
	}
	
	@Test
	public void testGetNoAlbumWithCover_NoCoverArtAsync() throws IOException {
		String song = "In bloom";
		String artist = "Nirvana";
		
		// Mock the albums found 
		Album a1 = new Album.Builder().name("Nevermind").artistName(artist).songName(song).mbid("1").build();
		Album a2 = new Album.Builder().name("Nevermind").artistName(artist).songName(song).mbid("2").build();
		when(albumFinder.findAlbums(song, artist)).thenReturn(Arrays.asList(a1, a2));
		
		// Find the cover arts
		when(coverArchiveFinder.findCoverArt(a1)).thenReturn(Optional.empty());
		when(coverArchiveFinder.findCoverArt(a2)).thenReturn(Optional.ofNullable(null));
		
		// Mock Amazon
		when(amazonFinder.findCoverArt(a1)).thenReturn(Optional.empty());
		
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
		Album a1 = new Album.Builder().name("Nevermind").artistName(artist).songName(song).mbid("1").build();
		Album a2 = new Album.Builder().name("Nevermind").artistName(artist).songName(song).mbid("2").build();
		when(albumFinder.findAlbums(song, artist)).thenReturn(Arrays.asList(a1, a2));
		
		// Find the cover arts
		when(coverArchiveFinder.findCoverArt(a1)).thenThrow(ClientProtocolException.class); // 404 from Server
		when(coverArchiveFinder.findCoverArt(a2)).thenThrow(ClientProtocolException.class); // 404 from Server
		
		// Mock Amazon
		when(amazonFinder.findCoverArt(a1)).thenThrow(ClientProtocolException.class);
		
		// Run the method to test
		Optional<Album> album = manager.getAlbumWithCoverAsync(song, artist);
		// Assertions
		assertThat(album.isPresent(), is(true));
		assertThat(album.get(), is(equalTo(a1)));
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
		
		Radio expectedRadio = new Radio(testRadioName, Optional.empty());
		Radio radioFound = oRadio.get();
		
		assertThat(radioFound, is(equalTo(expectedRadio)));
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
		assertThat(oAlbum.flatMap(Album::getCoverArt).flatMap(CoverArt::getLargeUri).get().toURL(), is(equalTo(url)));
	}
	
	/**
	 * Class that will emulate
	 * the call made to retrieve a URL
	 * with the cover art of an album.
	 * 
	 * @author christian
	 *
	 */
	private class CoverUrlAnswer implements Answer<Optional<CoverArt>> {
		private final Random rnd;
		private final Optional<CoverArt> opArt;
		
		public CoverUrlAnswer(String expLink, Random rnd) {
			this.opArt = Optional.of(new CoverArt.Builder().largeUri(expLink).build());
			this.rnd = rnd;
		}
		
		public CoverUrlAnswer(Optional<CoverArt> expCovArt, Random rnd) {
			this.opArt = expCovArt;
			this.rnd = rnd;
		}

		@Override
		public Optional<CoverArt> answer(InvocationOnMock invocation) throws Throwable {
			// Set a delay between 1 and 3 secs
			long duration = Math.abs(rnd.nextLong() % 2000) + 1000;
			log.info("Setting a delay to emulate the time spent on retrieveing the URL[{}]. Duration {} ms.", opArt, duration);
			
			return opArt;
		}
		
	}
}
