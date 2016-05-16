package bo.roman.radio.cover.album;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import bo.roman.radio.cover.album.AmazonCoverFinder;
import bo.roman.radio.cover.album.AmazonCoverFinder.SearchType;
import bo.roman.radio.cover.album.AmazonConnectionUtil;
import bo.roman.radio.cover.model.Album;
import bo.roman.radio.cover.model.CoverArt;
import bo.roman.radio.utilities.HttpUtils;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.CoreMatchers.*;


@RunWith(PowerMockRunner.class)
@PrepareForTest({HttpUtils.class, AmazonConnectionUtil.class})
public class AmazonCoverFinderTest {
	
	private AmazonCoverFinder finder;
	
	private final static String ROOTH_PATH = "src/test/resources/amazon/";
	private final static String NIRVANAXML_PATH = ROOTH_PATH + "amazon-nirvana.xml";
	private final static String NOITEMSXML_PATH = ROOTH_PATH + "amazon-noItems.xml";
	private static final String SEARCHKEYWORDXML_PATH = ROOTH_PATH + "amazon-nirvanaKeyword.xml";
	private static final String NOALBUMSXML_PATH = ROOTH_PATH + "amazon-noMusic.xml";
	private static final String SEARCHKEYWORDCLOSEXML_PATH = ROOTH_PATH + "amazon-nirvanaKeywordClose.xml";
	private static final String NIRVANACLOSEXML_PATH = ROOTH_PATH + "amazon-nirvanaClose.xml";
	private static final String CESAREAXML_PATH = ROOTH_PATH + "amazon-cesarea.xml";
	private static final String WHITESNAKEXML_PATH = ROOTH_PATH + "amazon-whitesnake.xml";
	private static final String NOBIGIMAGEXML_PATH = ROOTH_PATH + "amazon-noBigImages.xml";
	private static final String MAXSIZEXML_PATH = ROOTH_PATH + "amazon-maxSize.xml";
	
	@Before
	public void setUp() {
		finder = new AmazonCoverFinder();
		PowerMockito.mockStatic(HttpUtils.class);
		PowerMockito.mockStatic(AmazonConnectionUtil.class);
	}
	
	@Test
	public void testFindCesareaCover() throws IOException {
		String artist = "Cesária Évora";
		String albumName = "Sodade";
		Album album = new Album.Builder().name(albumName).artistName(artist).build();
		Optional<CoverArt> coverArt = doFindCoverArtByAlbum(album, CESAREAXML_PATH);
		
		// Assertions
		assertThat(coverArt.isPresent(), is(true));
		CoverArt coverExpected = new CoverArt.Builder()
				.mediumUri("http://ecx.images-amazon.com/images/I/51xF9uNf0lL.jpg")
				.smallUri("http://ecx.images-amazon.com/images/I/51xF9uNf0lL._SL160_.jpg")
				.tinyUri("http://ecx.images-amazon.com/images/I/51xF9uNf0lL._SL75_.jpg")
				.build();
		assertThat(coverArt.get(), is(equalTo(coverExpected)));
		
	}
	
	@Test
	public void testFindWhitesnakeCover() throws IOException {
		String artist = "Whitesnake";
		String albumName = "Live at Donington 1990";
		Album album = new Album.Builder().name(albumName).artistName(artist).build();
		Optional<CoverArt> coverArt = doFindCoverArtByAlbum(album, WHITESNAKEXML_PATH);
		
		// Assertions
		assertThat(coverArt.isPresent(), is(true));
		CoverArt coverExpected = new CoverArt.Builder()
				.mediumUri("http://ecx.images-amazon.com/images/I/61wHk0XqESL.jpg")
				.smallUri("http://ecx.images-amazon.com/images/I/61wHk0XqESL._SL160_.jpg")
				.tinyUri("http://ecx.images-amazon.com/images/I/61wHk0XqESL._SL75_.jpg")
				.build();
		assertThat(coverArt.get(), is(equalTo(coverExpected)));
	}
	
	@Test
	public void testFindCover_maxSize() throws IOException {
		String artist = "Whitesnake";
		String albumName = "Live at Donington 1990";
		Album album = new Album.Builder().name(albumName).artistName(artist).build();
		Optional<CoverArt> coverArt = doFindCoverArtByAlbum(album, MAXSIZEXML_PATH);
		
		// Assertions
		assertThat(coverArt.isPresent(), is(true));
		CoverArt coverExpected = new CoverArt.Builder()
				.mediumUri("http://ecx.images-amazon.com/images/I/61wHk0XqESL.jpg")
				.smallUri("http://ecx.images-amazon.com/images/I/61wHk0XqESL._SL160_.jpg")
				.tinyUri("http://ecx.images-amazon.com/images/I/61wHk0XqESL._SL75_.jpg")
				.build();
		assertThat(coverArt.get(), is(equalTo(coverExpected)));
	}
	
	@Test
	public void testFindCover_notBigEnoughImage() throws IOException {
		String artist = "Whitesnake";
		String albumName = "Live at Donington 1990";
		Album album = new Album.Builder().name(albumName).artistName(artist).build();
		Optional<CoverArt> coverArt = doFindCoverArtByAlbum(album, NOBIGIMAGEXML_PATH);
		
		// Assertions
		assertThat(coverArt.isPresent(), is(false));
	}
	
	@Test
	public void testFindCoverArt() throws IOException {
		String testArtist = "Nirvana";
		String testAlbumName = "Nevermind";
		Album testAlbum = new Album.Builder().name(testAlbumName).artistName(testArtist).build();
		Optional<CoverArt> coverArt = doFindCoverArtByAlbum(testAlbum, NIRVANAXML_PATH);
		
		// Assertions
		assertThat(coverArt.isPresent(), is(true));
		CoverArt coverExpected = new CoverArt.Builder()
				.mediumUri("http://ecx.images-amazon.com/images/I/51NyD8CUNIL.jpg")
				.smallUri("http://ecx.images-amazon.com/images/I/51NyD8CUNIL._SL160_.jpg")
				.tinyUri("http://ecx.images-amazon.com/images/I/51NyD8CUNIL._SL75_.jpg")
				.build();
		assertThat(coverArt.get(), is(equalTo(coverExpected)));
	}
	
	@Test
	public void testFindCoverByAlbum_noExact() throws IOException {
		String testArtist = "Nirvana";
		String testAlbumName = "Nevermind";
		Album testAlbum = new Album.Builder().name(testAlbumName).artistName(testArtist).build();
		Optional<CoverArt> coverArt = doFindCoverArtByAlbum(testAlbum, NIRVANACLOSEXML_PATH);
		
		// Assertions
		assertThat(coverArt.isPresent(), is(true));
		CoverArt coverExpected = new CoverArt.Builder()
				.mediumUri("http://ecx.images-amazon.com/images/I/51NyD8CUNIL.jpg")
				.smallUri("http://ecx.images-amazon.com/images/I/51NyD8CUNIL._SL160_.jpg")
				.tinyUri("http://ecx.images-amazon.com/images/I/51NyD8CUNIL._SL75_.jpg")
				.build();
		assertThat(coverArt.get(), is(equalTo(coverExpected)));
	}
	
	@Test
	public void testFindCoverArt_noAlbumFound() throws IOException {
		String testArtist = "Nirvana";
		String testAlbumName = "Lithium";
		Album testAlbum = new Album.Builder().name(testAlbumName).artistName(testArtist).build();
		Optional<CoverArt> ca = doFindCoverArtByAlbum(testAlbum, NOITEMSXML_PATH);
		
		// Assert
		assertThat("No CoverArt was expected", ca.isPresent(), is(false));
	}
	
	@Test
	public void testFindCoverArtByKeyWord() throws IOException {
		String songName = "Territorial Pissings";
		String artistName = "Nirvana";
		Album album = new Album.Builder().songName(songName).artistName(artistName).build();
		Optional<CoverArt> coverArt = doFindCoverArtByKeyword(album, SEARCHKEYWORDXML_PATH);
		
		// Assertions
		assertThat(coverArt.isPresent(), is(true));
		CoverArt coverExpected = new CoverArt.Builder()
				.mediumUri("http://ecx.images-amazon.com/images/I/51okp3JbucL.jpg")
				.smallUri("http://ecx.images-amazon.com/images/I/51okp3JbucL._SL160_.jpg")
				.tinyUri("http://ecx.images-amazon.com/images/I/51okp3JbucL._SL75_.jpg")
				.build();
		assertThat(coverArt.get(), is(equalTo(coverExpected)));
		
	}
	
	@Test
	public void testFindCoverByKeyword_notFound() throws IOException {
		String testArtist = "Nirvana";
		String testAlbumName = "Lithium";
		Album testAlbum = new Album.Builder().name(testAlbumName).artistName(testArtist).build();
		Optional<CoverArt> ca = doFindCoverArtByAlbum(testAlbum, NOALBUMSXML_PATH);
		
		// Assert
		assertThat("No CoverArt was expected", ca.isPresent(), is(false));
	}
	
	@Test
	public void testFindCoverByKeyword_noExact() throws IOException {
		String songName = "Territorial Pissings";
		String artistName = "Nirvana";
		Album album = new Album.Builder().songName(songName).artistName(artistName).build();
		Optional<CoverArt> coverArt = doFindCoverArtByKeyword(album, SEARCHKEYWORDCLOSEXML_PATH);
		
		// Assertions
		assertThat(coverArt.isPresent(), is(true));
		CoverArt coverExpected = new CoverArt.Builder()
				.mediumUri("http://ecx.images-amazon.com/images/I/51okp3JbucL.jpg")
				.smallUri("http://ecx.images-amazon.com/images/I/51okp3JbucL._SL160_.jpg")
				.tinyUri("http://ecx.images-amazon.com/images/I/51okp3JbucL._SL75_.jpg")
				.build();
		assertThat(coverArt.get(), is(equalTo(coverExpected)));
	}
	
	@Test
	public void testFindCoverUrl_noAlbumInfo() throws IOException {
		// Test null album
		Optional<CoverArt> caNullAlbum = finder.findCoverArt(null);
		assertThat("No CoverArt was expected", caNullAlbum.isPresent(), is(false));
		
		// Test empty album
		Album empAlb = new Album.Builder().build();
		Optional<CoverArt> caEmpAlb = finder.findCoverArt(empAlb);
		assertThat("No CoverArt was expected", caEmpAlb.isPresent(), is(false));
		
		// Test no Album name
		Album noName = new Album.Builder().artistName("anArtist").build();
		Optional<CoverArt> caNoName = finder.findCoverArt(noName);
		assertThat("No CoverArt was expected", caNoName.isPresent(), is(false));
		
		// Test no artistName
		Album noArtist = new Album.Builder().name("aName").build();
		Optional<CoverArt> caNoArtist = finder.findCoverArt(noArtist);
		assertThat("No CoverArt was expected", caNoArtist.isPresent(), is(false));
	}
	
	/* *** Utilities *** */
	
	private Optional<CoverArt> doFindCoverArtByAlbum(Album testAlbum, String xmlFilePath) throws IOException {
		return doFindCoverArtBySearchType(testAlbum, xmlFilePath, SearchType.SEARCHBY_ALBUM);
	}
	
	private Optional<CoverArt> doFindCoverArtByKeyword(Album testAlbum, String xmlFilePath) throws IOException {
		return doFindCoverArtBySearchType(testAlbum, xmlFilePath, SearchType.SEARCHBY_KEYWORD);
	}
	
	private Optional<CoverArt> doFindCoverArtBySearchType(Album testAlbum, String xmlFilePath, SearchType searchType) throws IOException {
		// Prepare Mock
		String url = "aURL";
		switch (searchType) {
		case SEARCHBY_ALBUM:
			PowerMockito.when(AmazonConnectionUtil.generateSearchByAlbumRequestUrl(testAlbum)).thenReturn(url);
			break;
		case SEARCHBY_KEYWORD:
			PowerMockito.when(AmazonConnectionUtil.generateSearchByKeywordRequestUrl(String.format("%s,%s", testAlbum.getSongName(), testAlbum.getArtistName()))).thenReturn(url);
			break;
		default:
			Assert.fail("Unexpected searchType to run a test.");
		}
		String xmlResponse = new String(Files.readAllBytes(Paths.get(xmlFilePath)));
		PowerMockito.when(HttpUtils.doGet(url)).thenReturn(xmlResponse);

		// Run Method
		return finder.findCoverArt(testAlbum);
	}

}
