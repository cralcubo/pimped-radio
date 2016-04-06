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
	
	private final static String XML_PATH = "src/test/resources/amazon-nirvana.xml";
	
	@Before
	public void setUp() {
		finder = new AmazonCoverFinder();
		PowerMockito.mockStatic(HttpUtils.class);
		PowerMockito.mockStatic(AmazonUtil.class);
	}
	
	@Test
	public void testFindCoverUrl() throws IOException {
		String testArtist = "Nirvana";
		String testAlbum = "Nevermind";
		
		// Prepare Mock
		String url = "aURL";
		PowerMockito.when(AmazonUtil.generateGetRequestUrl(testArtist, testAlbum)).thenReturn(url);
		
		String xmlResponse = new String(Files.readAllBytes(Paths.get(XML_PATH)));
		PowerMockito.when(HttpUtils.doGet(url)).thenReturn(xmlResponse );
		
		// Run Method
		Album album = new Album.Builder().artistName(testArtist).name(testAlbum).build();
		Optional<CoverArt> coverArt = finder.findCoverUrl(album);
		
		// Assertions
		assertThat(coverArt.isPresent(), is(true));
		CoverArt coverExpected = new CoverArt.Builder()
				.largeUri("http://ecx.images-amazon.com/images/I/51NyD8CUNIL.jpg")
				.mediumUri("http://ecx.images-amazon.com/images/I/51NyD8CUNIL._SL160_.jpg")
				.smallUri("http://ecx.images-amazon.com/images/I/51NyD8CUNIL._SL75_.jpg")
				.build();
		assertThat(coverArt.get(), is(equalTo(coverExpected)));
	}

}
