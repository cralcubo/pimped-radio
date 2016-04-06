package bo.roman.radio.cover;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import bo.roman.radio.cover.album.AmazonCoverFinder;
import bo.roman.radio.cover.album.AmazonUtil;
import bo.roman.radio.cover.model.Album;
import bo.roman.radio.cover.model.CoverArt;
import bo.roman.radio.utilities.HttpUtils;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.CoreMatchers.*;


@RunWith(PowerMockRunner.class)
@PrepareForTest({HttpUtils.class, AmazonUtil.class})
public class AmazonCoverFinderTest {
	
	private AmazonCoverFinder finder;
	
	private final static String NIRVANAXML_PATH = "src/test/resources/amazon-nirvana.xml";
	private final static String NOITEMSXML_PATH = "src/test/resources/amazon-noItems.xml";
	
	
	@Before
	public void setUp() {
		finder = new AmazonCoverFinder();
		PowerMockito.mockStatic(HttpUtils.class);
		PowerMockito.mockStatic(AmazonUtil.class);
	}
	
	@Test
	public void testFindCoverArt() throws IOException {
		String testArtist = "Nirvana";
		String testAlbum = "Nevermind";
		Optional<CoverArt> coverArt = doFindCoverArt(testArtist, testAlbum, NIRVANAXML_PATH);
		
		// Assertions
		assertThat(coverArt.isPresent(), is(true));
		CoverArt coverExpected = new CoverArt.Builder()
				.largeUri("http://ecx.images-amazon.com/images/I/51NyD8CUNIL.jpg")
				.mediumUri("http://ecx.images-amazon.com/images/I/51NyD8CUNIL._SL160_.jpg")
				.smallUri("http://ecx.images-amazon.com/images/I/51NyD8CUNIL._SL75_.jpg")
				.build();
		assertThat(coverArt.get(), is(equalTo(coverExpected)));
	}
	
	@Test
	public void testFindCoverArt_noAlbumFound() throws IOException {
		String testArtist = "Nirvana";
		String testAlbum = "Lithium";
		Optional<CoverArt> ca = doFindCoverArt(testArtist, testAlbum, NOITEMSXML_PATH);
		
		// Assert
		assertThat("No CoverArt was expected", ca.isPresent(), is(false));
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
	private Optional<CoverArt> doFindCoverArt(String testArtist, String testAlbum, String xmlFilePath) throws IOException {
		// Prepare Mock
		String url = "aURL";
		PowerMockito.when(AmazonUtil.generateGetRequestUrl(testArtist, testAlbum)).thenReturn(url);

		String xmlResponse = new String(Files.readAllBytes(Paths.get(xmlFilePath)));
		PowerMockito.when(HttpUtils.doGet(url)).thenReturn(xmlResponse);

		// Run Method
		Album album = new Album.Builder().artistName(testArtist).name(testAlbum).build();
		return finder.findCoverArt(album);
	}

}
