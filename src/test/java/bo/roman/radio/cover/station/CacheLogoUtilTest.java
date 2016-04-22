package bo.roman.radio.cover.station;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import org.junit.After;
import org.junit.Test;

public class CacheLogoUtilTest {
	private static final String RESOURCES_FOLDER = "src/test/resources/images/";
	private static final String RADIOPARADISE_JPG = RESOURCES_FOLDER + "radio-paradise.jpg";
	private static final String JSON_FILE = RESOURCES_FOLDER + "radio-paradise.json";
	private final String radioName = "aTestRadio";
	
	@After
	public void tearDown() throws IOException {
		if(CacheLogoUtil.isCached(radioName)) {
			Path cachedLogoPath = CacheLogoUtil.getCachedLogoPath(radioName);
			Files.delete(cachedLogoPath);
		}
	}
	
	/**
	 * Test that will check:
	 * 
	 * 01. That a radioLogo is not cached.
	 * 02. Cache the logo of the radio.
	 * 03. Assert the expected name of the cached radio logo.
	 * 
	 * @throws URISyntaxException
	 */
	@Test
	public void testCacheRadioLogo() throws URISyntaxException {
		// First verify that the radio is not cached already
		assertThat("Check if testRadio is cached", CacheLogoUtil.isCached(radioName), is(false));
		
		URI logoUri = Paths.get(RADIOPARADISE_JPG).toUri();
		CacheLogoUtil.cacheRadioLogo(radioName, Optional.of(logoUri));
		
		// Assert on the cached logo
		Path cachedLogoPath = CacheLogoUtil.getCachedLogoPath(radioName);
		assertThat(CacheLogoUtil.isCached(radioName), is(true));
		String cacheFolder = System.getProperty("java.io.tmpdir");
		assertThat(cachedLogoPath, is(equalTo(Paths.get(String.format("%s%s.jpg", cacheFolder, radioName)))));
	}
	
	@Test
	public void testDoNotCache_emptyUri() {
		CacheLogoUtil.cacheRadioLogo(radioName, Optional.empty());
		
		// assert
		assertThat(CacheLogoUtil.isCached(radioName), is(false));
	}
	
	@Test
	public void testDoNotCache_noImage() throws MalformedURLException, URISyntaxException {
		CacheLogoUtil.cacheRadioLogo(radioName, Optional.of(Paths.get(JSON_FILE).toUri()));
		// assert
		assertThat(CacheLogoUtil.isCached(radioName), is(false));
	}
	
	

}
