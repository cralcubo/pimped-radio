package bo.roman.radio.cover.album;

import static org.junit.Assert.fail;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.CoreMatchers.is;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;

import org.apache.http.client.ClientProtocolException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import bo.roman.radio.cover.album.CoverArtArchiveFinder;
import bo.roman.radio.cover.model.Album;
import bo.roman.radio.cover.model.CoverArt;
import bo.roman.radio.utilities.HttpUtils;
import bo.roman.radio.utilities.ImageUtil;
import bo.roman.radio.utilities.ReflectionUtils;

@RunWith(PowerMockRunner.class)
@PrepareForTest({HttpUtils.class, ImageUtil.class})
public class CoverArtArchiverFinderTest {

	// Utilities constants
	private static final String JSONSOURCE_ROOT = "src/test/resources/coverart/";
	private static final String JSON_SOURCE_OK = JSONSOURCE_ROOT + "cover-art.json";
	private static final String JSON_SOURCE_NOFRONT = JSONSOURCE_ROOT + "cover-art-nofront.json";
	private static final String MBID = "12345MBID";
	
	private final String RELEASEREQUEST_TEMPLATE;

	// Test properties
	private CoverArtArchiveFinder finder;
	private String coverArtJson;
	private String coverArtJson_NoFront;
	
	public CoverArtArchiverFinderTest() throws Exception {
		finder = new CoverArtArchiveFinder();
		// Get the values of the constants from CoverArtArchiveFinder
		RELEASEREQUEST_TEMPLATE = (String) ReflectionUtils.getPrivateConstant(finder, "RELEASEREQUEST_TEMPLATE");
	}

	@Before
	public void setUp() {
		PowerMockito.mockStatic(HttpUtils.class);
		PowerMockito.mockStatic(ImageUtil.class);
		try {
			coverArtJson = new String(Files.readAllBytes(Paths.get(JSON_SOURCE_OK)));
			coverArtJson_NoFront = new String(Files.readAllBytes(Paths.get(JSON_SOURCE_NOFRONT)));
		} catch (IOException e) {
			e.printStackTrace();
			fail("Error getting the file=" + JSON_SOURCE_OK);
		}
	}

//	@Test
//	public void testRequestToCoverArt() throws IOException {
//		String imageUri = "http://coverartarchive.org/release/12345MBID/1357-500.jpg";
//		PowerMockito.when(ImageUtil.isBigEnough(imageUri)).thenReturn(true);
//		
//		testRequestCover(coverArtJson, Optional.of(new CoverArt.Builder()
//				.largeUri("http://coverartarchive.org/release/12345MBID/1357.jpg")
//				.mediumUri(imageUri)
//				.smallUri("http://coverartarchive.org/release/12345MBID/1357-250.jpg")
//				.build()));
//	}
//	
//	@Test
//	public void testRequestToCoverArt_SmallImage() throws IOException {
//		String imageUri = "http://coverartarchive.org/release/12345MBID/1357-500.jpg";
//		PowerMockito.when(ImageUtil.isBigEnough(imageUri)).thenReturn(false);
//		
//		testRequestCover(coverArtJson, Optional.empty());
//	}

	@Test
	public void testRequestCA_NoCover() throws IOException {
		testRequestCover(coverArtJson_NoFront, Optional.empty());
	}

	@SuppressWarnings("unchecked")
	@Test(expected = IOException.class)
	public void testRequestToCoverArt_failed() throws IOException {
		String requestLink = String.format(RELEASEREQUEST_TEMPLATE, MBID);

		// Prepare
		PowerMockito.when(HttpUtils.doGet(requestLink)).thenThrow(ClientProtocolException.class);

		finder.findCoverArt(new Album.Builder().mbid(MBID).build());
	}
	
	@Test
	public void testRequestCoverArt_NoJSONObject() throws IOException {
		testRequestCover("", Optional.empty());
	}
	
	/* **Utilities** */
	private void testRequestCover(String jsonObject, Optional<CoverArt> optCoverExpected) throws IOException {
		String requestLink = String.format(RELEASEREQUEST_TEMPLATE, MBID);

		// Prepare
		PowerMockito.when(HttpUtils.doGet(requestLink)).thenReturn(jsonObject);

		// Run
		Optional<CoverArt> coverArt = finder.findCoverArt(new Album.Builder().mbid(MBID).build());
		
		assertThat(coverArt.isPresent(), is(optCoverExpected.isPresent()));
		
		optCoverExpected.ifPresent(ca -> 
						assertThat(ca, is(equalTo(coverArt.get()))));
		
	}

}
