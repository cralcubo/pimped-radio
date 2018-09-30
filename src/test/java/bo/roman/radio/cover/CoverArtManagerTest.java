package bo.roman.radio.cover;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;

import java.net.URI;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.core.classloader.annotations.SuppressStaticInitializationFor;
import org.powermock.modules.junit4.PowerMockRunner;

import bo.roman.radio.cover.album.AlbumFindable;
import bo.roman.radio.cover.model.Album;
import bo.roman.radio.cover.model.CoverArt;
import bo.roman.radio.cover.model.Radio;
import bo.roman.radio.cover.station.CacheLogoUtil;
import bo.roman.radio.cover.station.RadioStationFindable;
import bo.roman.radio.utilities.ReflectionUtils;
import bo.roman.radio.utilities.SecretFileProperties;

@RunWith(PowerMockRunner.class)
@PrepareForTest({CacheLogoUtil.class, SecretFileProperties.class})
@SuppressStaticInitializationFor("bo.roman.radio.utilities.SecretFileProperties")
public class CoverArtManagerTest {
	private ICoverArtManager manager;
	
	private String testRadioName = "aTestRadio";
	private String testRadioId = "7777";
	
	private String testSong = "aSong";
	private String testArtist = "anArtist";
	private String testAlbum = "anAlbum";
	
	@Mock
	private RadioStationFindable radioFinder;
	@Mock
	private AlbumFindable albumFinder;
	
	
	@Before
	public void setUp() throws Exception {
		manager = new CoverArtManager(albumFinder, radioFinder);
		PowerMockito.mockStatic(CacheLogoUtil.class);
		PowerMockito.mockStatic(SecretFileProperties.class);
		when(SecretFileProperties.get("lastfm.apiKey")).thenReturn("aKey");
	}
	
	@Test
	public void testGetAlbum_noAlbums() {
		when(albumFinder.findAlbums(testSong, testArtist)).thenReturn(Collections.emptyList());
		
		Optional<Album> album = manager.getAlbumWithCover(testSong, testArtist);
		assertThat("No Album expected.", album.isPresent(), is(false));
	}
	
	@Test
	public void testGetAlbum_bestMatch() {
		CoverArt rectangularCover = new CoverArt.Builder().maxHeight(500).maxWidth(499).largeUri("http://rect.uri").build();
		Album a1 = new Album.Builder().artistName(testArtist).songName(testSong).name(testAlbum).coverArt(Optional.of(rectangularCover)).build();
		
		CoverArt squareCover = new CoverArt.Builder().maxHeight(500).maxWidth(500).largeUri("http://square.uri").build();
		Album a2 = new Album.Builder().artistName(testArtist).songName(testSong).name(testAlbum).coverArt(Optional.of(squareCover)).build();
		
		Album a3 = new Album.Builder().artistName(testArtist + " ft. DMX").songName(testSong).name(testAlbum).coverArt(Optional.of(squareCover)).build();
		Album a4 = new Album.Builder().artistName(testArtist).songName(testSong + "(Remix)").name(testAlbum).coverArt(Optional.of(squareCover)).build();
		
		List<Album> albums = Arrays.asList(a1, a2, a3, a4);
		
		// Mock
		when(albumFinder.findAlbums(testSong, testArtist)).thenReturn(albums );
		
		Optional<Album> oAlbum = manager.getAlbumWithCover(testSong, testArtist);
		
		assertThat(oAlbum.get(), is(a2));
	}
	
	@Test
	public void testGetAlbum_bestMatchAlbum() {
		String song = "Breed";
		String band = "Nirvana";
		String albumName = "Nevermind";
		
		CoverArt rectangularCover = new CoverArt.Builder().maxHeight(500).maxWidth(499).largeUri("http://rect.uri").build();
		CoverArt squareCover = new CoverArt.Builder().maxHeight(500).maxWidth(500).largeUri("http://rect.uri").build();
		
		Album a1 = new Album.Builder().artistName(band).songName(song).name(albumName).coverArt(Optional.of(squareCover)).build();
		Album a2 = new Album.Builder().artistName(band).songName(song + " (Remastered)").name(albumName + " (Remastered)").coverArt(Optional.of(squareCover)).build();
		Album a3 = new Album.Builder().artistName(band + "(Unplugged)").songName(song + " (Unplugged)").name(albumName + " (Unplugged)").coverArt(Optional.of(squareCover)).build();
		Album a4 = new Album.Builder().artistName(band).songName(song).name(song + "(Explicit)").coverArt(Optional.of(rectangularCover)).build();
		Album a5 = new Album.Builder().artistName(band).songName(song).name(song).coverArt(Optional.of(squareCover)).build();
		
		List<Album> albums = Arrays.asList(a1, a2, a3, a4, a5);
		
		// Mock
		when(albumFinder.findAlbums(song, band)).thenReturn(albums);
		
		Optional<Album> oAlbum = manager.getAlbumWithCover(song, band);
		
		assertThat(oAlbum.get(), is(a5));
	}
	
	@Test
	public void testGetAlbum_closeMatch() {
		CoverArt rectangularCover = new CoverArt.Builder().maxHeight(500).maxWidth(499).largeUri("http://rect.uri").build();
		Album a1 = new Album.Builder().artistName("Nirvana -" + testArtist).songName(testSong).name(testAlbum).coverArt(Optional.of(rectangularCover)).build();
		
		CoverArt squareCover = new CoverArt.Builder().maxHeight(500).maxWidth(500).largeUri("http://square.uri").build();
		Album a2 = new Album.Builder().artistName(testArtist).songName(testSong + "(Remix 95)").name(testAlbum).coverArt(Optional.of(squareCover)).build();
		Album a3 = new Album.Builder().artistName(testArtist + " ft. DMX").songName(testSong).name(testAlbum).coverArt(Optional.of(squareCover)).build();
		Album a4 = new Album.Builder().artistName(testArtist).songName(testSong + " (Remix)").name(testAlbum).coverArt(Optional.of(rectangularCover)).build();
		
		List<Album> albums = Arrays.asList(a1, a2, a3, a4);
		
		// Mock
		when(albumFinder.findAlbums(testSong, testArtist)).thenReturn(albums );
		
		Optional<Album> oAlbum = manager.getAlbumWithCover(testSong, testArtist);
		
		assertThat(oAlbum.get(), is(a3));
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
		assertThat(oRadio.isPresent(), is(false));
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

}
