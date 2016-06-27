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
import bo.roman.radio.utilities.PhraseCalculator;
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
	private static final String RIHANNAXML_PATH = ROOTH_PATH + "amazon-rihanna-work.xml";
	private static final String KASKADEXML_PATH = ROOTH_PATH + "amazon-kaskade.xml";
	private static final String PINKFLOYDXML_PATH = ROOTH_PATH + "amazon-pinkfloyd.xml";
	private static final String ECHOESXML_PATH = ROOTH_PATH + "amazon-echoes.xml";

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
		
		doFindAlbumsTest(testSong, testArtist, BREEDXML_PATH, 2);
	}
	
	@Test
	public void testFindAlbums_Album() throws IOException {
		String testArtist = "Nirvana";
		String testSong = "Nevermind";
		
		doFindAlbumsTest(testSong, testArtist, NIRVANAXML_PATH, 3);
	}
	
	@Test
	public void testFindAlbums_AlbumSwapedInfo() throws IOException {
		String testArtist = "Nevermind";
		String testSong = "Nirvana";
		
		doFindAlbumsTest_swapped(testSong, testArtist, NIRVANAXML_PATH, 3);
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
		
		doFindAlbumsTest(song, artist, NIRVANACLOSEXML_PATH, 5);
	}
	
	@Test
	public void testFindAlbums_Whitesnake() throws IOException{
		String artist = "Whitesnake";
		String song = "Live at Donington 1990";
		
		doFindAlbumsTest(song, artist, WHITESNAKEXML_PATH, 5);
	}
	
	@Test
	public void testFindAlbums_WhitesnakeSwapped() throws IOException{
		String song = "Whitesnake";
		String artist = "Live at Donington 1990";
		
		doFindAlbumsTest_swapped(song, artist, WHITESNAKEXML_PATH, 5);
	}
	
	@Test
	public void testFindAlbum_Rihanna() throws IOException {
		String artist = "Rihanna";
		String song = "Work";
		
		doFindAlbumsTest(song, artist, RIHANNAXML_PATH, 2);
	}
	
	@Test
	public void testFindAlbum_RihannaSwapped() throws IOException {
		String artist = "Work";
		String song = "Rihanna";
		
		doFindAlbumsTest_swapped(song, artist, RIHANNAXML_PATH, 2);
	}
	
	@Test
	public void testFindAlbum_Kaskade() throws IOException {
		String song = "I Remember";
		String artist = "Kaskade with Deadmau5";
		
		doFindAlbumsTest(song, artist, KASKADEXML_PATH, 6);
	}
	
	@Test
	public void testFindAlbum_PinkFloyd() throws IOException {
		String song = "Echoes";
		String artist = "Pink Floyd";
		
		doFindAlbumsTest(song, artist, PINKFLOYDXML_PATH, 0);
	}
	
	@Test
	public void testFindAlbum_NoArtist() throws IOException {
		String song = "Echoes";
		doFindAlbumsTest(song, ECHOESXML_PATH, 1);
	}
	
	/* *** Utilities *** */
	private void doFindAlbumsTest_swapped(String song, String artist, String xmlFilePath, int numbAlbums) throws IOException {
		List<Album> albums = findAlbums(song, artist, xmlFilePath, numbAlbums);

		for (Album a : albums) {
			assertThat("Artist name unexpected: " + a.getArtistName(), PhraseCalculator.phrase(song).atLeastContains(a.getArtistName()), is(true));
			assertThat("Song name unexpected: " + a.getSongName(), PhraseCalculator.phrase(artist).atLeastContains(a.getSongName()), is(true));
		}
	}
	
	private void doFindAlbumsTest(String song, String artist, String xmlFilePath, int numbAlbums) throws IOException {
		List<Album> albums = findAlbums(song, artist, xmlFilePath, numbAlbums);

		for (Album a : albums) {
			assertThat("Artist name unexpected: " + a.getArtistName(), PhraseCalculator.phrase(artist).atLeastContains(a.getArtistName()), is(true));
			assertThat("Song name unexpected: " + a.getSongName(), PhraseCalculator.phrase(song).atLeastContains(a.getSongName()), is(true));
		}
	}
	
	private void doFindAlbumsTest(String song, String xmlFilePath, int numbAlbums) throws IOException {
		List<Album> albums = findAlbums(song, "", xmlFilePath, numbAlbums);

		for (Album a : albums) {
			assertThat("Song name unexpected: " + a.getSongName(), PhraseCalculator.phrase(song).atLeastContains(a.getSongName()), is(true));
		}
	}
	
	private List<Album> findAlbums(String song, String artist, String xmlFilePath, int numbAlbums) throws IOException {
		String testKeyword = String.format(NOWPLAYING_TEMPL, song, artist);
		
		// Prepare Mock
		String testUrl = "http://aUrl";
		when(AmazonConnectionUtil.generateSearchByKeywordRequestUrl(testKeyword)).thenReturn(testUrl);
		String xmlResponse = new String(Files.readAllBytes(Paths.get(xmlFilePath)));
		when(HttpUtils.doGet(testUrl)).thenReturn(xmlResponse);
		
		List<Album> albums = finder.findAlbums(song, artist);
		assertThat("Number of Albums.", albums.size(), is(numbAlbums));
		
		return albums;
	}

}
