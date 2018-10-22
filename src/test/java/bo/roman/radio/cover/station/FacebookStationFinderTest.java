package bo.roman.radio.cover.station;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.core.classloader.annotations.SuppressStaticInitializationFor;
import org.powermock.modules.junit4.PowerMockRunner;

import bo.roman.radio.cover.model.Radio;
import bo.roman.radio.cover.station.facebook.FacebookRadioStationFinder;
import bo.roman.radio.cover.station.facebook.FacebookUtil;
import bo.roman.radio.utilities.ReflectionUtils;

@Ignore
@RunWith(PowerMockRunner.class)
@PrepareForTest(FacebookUtil.class)
@SuppressStaticInitializationFor("bo.roman.radio.cover.station.FacebookUtil")
public class FacebookStationFinderTest {
	/* Pages JSON Mock */
	private static final String RESOURCES_PATH = "src/test/resources/radio/";
	private static final String RADIOPARADISE_PATH = RESOURCES_PATH + "radio-paradise.json";
	private static final String RADIOXESTEREO_PATH = RESOURCES_PATH + "radio-xestereo.json";
	private static final String RADIOPASION_PATH = RESOURCES_PATH + "radio-pasion.json";
	private static final String RADIORUSSIA_PATH = RESOURCES_PATH + "radio-russia.json";
	private static final String RADIOSTEREO_PATH = RESOURCES_PATH + "radio-stereo97.json";
	private static final String RADIOCLASSICRAP_PATH = RESOURCES_PATH + "radio-classicRap.json";

	private final String SEARCHPAGE_TEMPLATE;
	private final String PAGELOGO_TEMPLATE;
	
	private FacebookRadioStationFinder finder;
	
	public FacebookStationFinderTest() throws Exception {
		finder = new FacebookRadioStationFinder();
		
		// Using reflection to retrieve the constants.
		SEARCHPAGE_TEMPLATE = (String) ReflectionUtils.getPrivateConstant(finder, "SEARCHPAGE_TEMPLATE");
		PAGELOGO_TEMPLATE = (String) ReflectionUtils.getPrivateConstant(new Radio.Builder().build(), "PAGELOGO_TEMPLATE");
	}
	
	
	@Before
	public void setUp() throws IOException {
		PowerMockito.mockStatic(FacebookUtil.class);
	}
	
	@Test
	public void testFindRadioStation_exactMatch() throws IOException {
		String radioName = "Radio Paradise";
		Optional<Radio> oRadioFound = doFindRadioPage(radioName, Paths.get(RADIOPARADISE_PATH));
		
		// Assert
		assertThat(oRadioFound.isPresent(), is(true));
		
		String expectedId = "25316021579";
		Radio expectedRadio = new Radio.Builder().id(expectedId).build();
		Radio radioFound = oRadioFound.get();
		
		assertThat(expectedRadio, is(equalTo(radioFound)));
		assertThat(radioFound.getName(), is(radioName));
		URL logoUrl = radioFound.getLogoUri().get().toURL();
		URL expectedUrl = new URL(String.format(PAGELOGO_TEMPLATE, expectedId));
		assertThat(logoUrl, is(equalTo(expectedUrl)));
	}
	
	@Test
	public void testFindRadioStation_multipleMatch() throws IOException {
		String radioName = "la x estereo";
		Optional<Radio> oRadioFound = doFindRadioPage(radioName, Paths.get(RADIOXESTEREO_PATH));
		
		// Assert
		assertThat(oRadioFound.isPresent(), is(true));

		Radio radioFound = oRadioFound.get();
		Radio expected1 = new Radio.Builder().id("222983771052625").build();
		Radio expected2 = new Radio.Builder().id("1736014773295495").build();
		assertTrue("The radio found was not expected.", radioFound.equals(expected1) || radioFound.equals(expected2));
	}
	
	@Test
	public void testFindRadio_closeMatch() throws IOException {
		String radioName = "Radio pasión";
		Optional<Radio> oRadioFound = doFindRadioPage(radioName, Paths.get(RADIOPASION_PATH));
		
		// Assert
		assertThat(oRadioFound.isPresent(), is(true));
		
		Radio radioFound = oRadioFound.get();
		Radio expectedRadio = new Radio.Builder().id("114920161882070").build();
		assertThat(radioFound, is(equalTo(expectedRadio)));
	}
	
	@Test
	public void testFindRadio_closeMatchRadioRemoved() throws IOException {
		String radioName = "Radio Stereo 97";
		Optional<Radio> oRadioFound = doFindRadioPage(radioName, Paths.get(RADIOSTEREO_PATH));

		// Assert
		assertThat(oRadioFound.isPresent(), is(true));

		Radio radioFound = oRadioFound.get();
		Radio expectedRadio = new Radio.Builder().id("161206687230282").build();
		assertThat(radioFound, is(equalTo(expectedRadio)));
	}
	
	@Test
	public void testFindRadio_noMatch() throws IOException {
		String radioName = "Radio latina";
		Optional<Radio> oRadioFound = doFindRadioPage(radioName, Paths.get(RADIOPASION_PATH));
		
		// Assert
		assertThat(oRadioFound.isPresent(), is(false));
	}

	@Test
	public void testFindRadio_weirdChars() throws IOException {
		String radioName = "Русский Час";
		Optional<Radio> oRadioFound = doFindRadioPage(radioName, Paths.get(RADIORUSSIA_PATH));

		// Assert
		assertThat(oRadioFound.isPresent(), is(true));
		
		Radio radioFound = oRadioFound.get();
		Radio expectedRadio = new Radio.Builder().id("610514639052614").build();
		assertThat(radioFound, is(equalTo(expectedRadio)));

	}
	
	@Test
	public void testFindRadio_noCamelCase() throws IOException {
		String radioName = "ClassicRap";
		Optional<Radio> oRadioFound = doFindRadioPage(radioName, Paths.get(RADIOCLASSICRAP_PATH));

		// Assert
		assertThat(oRadioFound.isPresent(), is(true));

		Radio radioFound = oRadioFound.get();
		Radio expectedRadio = new Radio.Builder().id("119749068044268").build();
		assertThat(radioFound, is(equalTo(expectedRadio)));
	}
	
	/* *** Utils *** */
	private Optional<Radio> doFindRadioPage(String radioName, Path mockPath) throws IOException {
		// Prepare Mock
		String radioJsonMock = new String(Files.readAllBytes(mockPath));
		String searchQuery = String.format(SEARCHPAGE_TEMPLATE, radioName);
		Optional<String> optReturnJson = Optional.of(radioJsonMock);
		PowerMockito.when(FacebookUtil.doSearch(searchQuery)).thenReturn(optReturnJson);

		return finder.findRadioStation(radioName);
	}

}
