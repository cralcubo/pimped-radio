package bo.roman.radio.cover.album;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import bo.roman.radio.cover.model.Album;
import bo.roman.radio.utilities.HttpUtils;
import bo.roman.radio.utilities.ReflectionUtils;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ HttpUtils.class, AmazonConnectionUtil.class })
public class AmazonAlbumFinderTest {

	private final static String ROOTH_PATH = "src/test/resources/amazon/";
	private final static String BREEDXML_PATH = ROOTH_PATH + "amazon-nirvana-breed.xml";
	private final static String NIRVANAXML_PATH = ROOTH_PATH + "amazon-nirvana-nevermind.xml";
	private static final String CESAREAXML_PATH = ROOTH_PATH + "amazon-cesarea.xml";
	private final static String NOITEMSXML_PATH = ROOTH_PATH + "amazon-noItems.xml";
	private static final String NOBIGIMAGEXML_PATH = ROOTH_PATH + "amazon-noBigImages.xml";
	private static final String NOMUSICXML_PATH = ROOTH_PATH + "amazon-noMusic.xml";
	private static final String NIRVANACLOSEXML_PATH = ROOTH_PATH + "amazon-nirvanaClose.xml";
	private static final String WHITESNAKEXML_PATH = ROOTH_PATH + "amazon-whitesnake.xml";

	private AmazonAlbumFinder finder;
	
	private static String NOWPLAYING_TEMPL;

	@Before
	public void setUp() throws Exception {
		finder = new AmazonAlbumFinder();
		mockStatic(HttpUtils.class);
		mockStatic(AmazonConnectionUtil.class);
		NOWPLAYING_TEMPL = (String) ReflectionUtils.getPrivateConstant(finder, "NOWPLAYING_TEMPL");
	}

	@Test
	public void testFindAlbums_Track() throws IOException {
		String testArtist = "Nirvana";
		String testSong = "Breed";
		
		doFindAlbumsTest(testSong, testArtist, BREEDXML_PATH, 10);
	}
	
	@Test
	public void testFindAlbums_Album() throws IOException {
		String testArtist = "Nirvana";
		String testSong = "Nevermind";
		
		doFindAlbumsTest(testSong, testArtist, NIRVANAXML_PATH, 5);
	}
	
	@Test
	public void testFindAlbums_Cesarea() throws IOException {
		String artist = "Cesaria Evora";
		String song = "Sodade";
		
		doFindAlbumsTest(song, artist, CESAREAXML_PATH, 5);
	}
	
	@Test
	public void testFindAlbums_noItems() throws IOException {
		String artist = "Nirvana";
		String song = "Territorial Pissings";
		
		doFindAlbumsTest(song, artist, NOITEMSXML_PATH, 0);
	}
	
	@Test
	public void testFindAlbums_noBigImages() throws IOException {
		String artist = "Nirvana";
		String song = "Territorial Pissings";
		
		doFindAlbumsTest(song, artist, NOBIGIMAGEXML_PATH, 0);
	}
	
	@Test
	public void testFindAlbums_noAlbums() throws IOException {
		String artist = "jacques vriens";
		String song = "achtste-groepers huilen niet";
		
		doFindAlbumsTest(song, artist, NOMUSICXML_PATH, 0);
	}
	
	@Test
	public void testFindAlbums_closeKeyword() throws IOException{
		String artist = "Nirvana";
		String song = "Nevermind";
		
		doFindAlbumsTest(song, artist, NIRVANACLOSEXML_PATH, 6);
	}
	
	@Test
	public void testFindAlbums_Whitesnake() throws IOException{
		String artist = "Whitesnake";
		String song = "Live at Donington 1990";
		
		doFindAlbumsTest(song, artist, WHITESNAKEXML_PATH, 5);
	}
	
	
	
	
	/* *** Utilities *** */
	private void doFindAlbumsTest(String song, String artist, String xmlFilePath, int numbAlbums) throws IOException {
		String testKeyword = String.format(NOWPLAYING_TEMPL, song, artist);
		
		// Prepare Mock
		String testUrl = "http://aUrl";
		when(AmazonConnectionUtil.generateSearchByKeywordRequestUrl(testKeyword)).thenReturn(testUrl);
		String xmlResponse = new String(Files.readAllBytes(Paths.get(xmlFilePath)));
		when(HttpUtils.doGet(testUrl)).thenReturn(xmlResponse);
		
		List<Album> albums = finder.findAlbums(song, artist);
		
		albums.forEach(System.out::println);

		assertThat("Number of Albums.", albums.size(), is(numbAlbums));
	}

}
