package bo.roman.radio.cover.album;

import static org.powermock.api.mockito.PowerMockito.*;

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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ HttpUtils.class, AmazonConnectionUtil.class })
public class AmazonAlbumFinderTest {

	private final static String ROOTH_PATH = "src/test/resources/amazon/";
	private final static String BREEDXML_PATH = ROOTH_PATH + "amazon-nirvana-breed.xml";

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

	private AmazonAlbumFinder finder;

	@Before
	public void setUp() {
		finder = new AmazonAlbumFinder();
		mockStatic(HttpUtils.class);
		mockStatic(AmazonConnectionUtil.class);
	}

	@Test
	public void testFindAlbums() throws IOException {
		String testArtist = "Nirvana";
		String testSong = "Breed";
		String testKeyword = String.format("%s,%s", testSong, testArtist);

		// Prepare Mock
		String testUrl = "http://aUrl";
		when(AmazonConnectionUtil.generateSearchByKeywordRequestUrl(testKeyword)).thenReturn(testUrl);
		String xmlResponse = new String(Files.readAllBytes(Paths.get(BREEDXML_PATH)));
		when(HttpUtils.doGet(testUrl)).thenReturn(xmlResponse);

		List<Album> albums = finder.findAlbums(testSong, testArtist);

		// We expect 10 albums to be returned
		assertThat(albums.size(), is(10));

		for (Album a : albums) {
			assertThat("Song name unexpected: " + a.getSongName(), a.getSongName().contains(testSong), is(true));
		}

	}

}
