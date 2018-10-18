package bo.roman.radio.cover.station.tunein;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;

import org.apache.xerces.util.URI;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.core.classloader.annotations.SuppressStaticInitializationFor;
import org.powermock.modules.junit4.PowerMockRunner;

import bo.roman.radio.cover.model.Radio;
import bo.roman.radio.utilities.HttpUtils;
import bo.roman.radio.utilities.SecretFileProperties;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ HttpUtils.class, SecretFileProperties.class })
@SuppressStaticInitializationFor("bo.roman.radio.utilities.SecretFileProperties")
public class TuneInRadioFinderTest {
	private final static String TUNEIN_URL = "https://tunein.com/search/?query=";

	private static final String RESOURCES_PATH = "src/test/resources/tuneIn/";
	private static final String radioParadise = RESOURCES_PATH + "radioParadise.html";
	private static final String multipleMatches = RESOURCES_PATH + "multipleMatch.html";
	private static final String noJson = RESOURCES_PATH + "noJson.html";
	private static final String noStations = RESOURCES_PATH + "noStations.html";

	private TuneInRadioFinder finder = new TuneInRadioFinder();

	public TuneInRadioFinderTest() {
		PowerMockito.mockStatic(SecretFileProperties.class);
		PowerMockito.when(SecretFileProperties.get("app.name")).thenReturn("aName");
		PowerMockito.when(SecretFileProperties.get("app.version")).thenReturn("aVersion");
	}

	@Before
	public void setUp() {
		PowerMockito.mockStatic(HttpUtils.class);

	}

	private String readFile(String path) throws IOException {
		return Files.readAllLines(Paths.get(path)).stream().reduce("", String::concat);
	}

	@Test
	public void findStation() throws Exception {
		String radioName = "Radio Paradise";

		PowerMockito.when(HttpUtils.doGet(TUNEIN_URL + radioName)).thenReturn(readFile(radioParadise));

		Optional<Radio> found = finder.findRadioStation(radioName);

		// Assertions
		assertTrue(found.isPresent());
		Radio r = found.get();
		assertThat(r.getName(), is(radioName));
		URI expectedUri = new URI("https:\u002F\u002Fcdn-radiotime-logos.tunein.com\u002Fs13606q.png");
		assertThat(r.getLogoUri().get().toString(), is(expectedUri.toString()));
	}

	@Test
	public void findStationMultipleMatches() throws Exception {
		String radioName = "Radio Paradise";

		PowerMockito.when(HttpUtils.doGet(TUNEIN_URL + radioName)).thenReturn(readFile(multipleMatches));

		Optional<Radio> found = finder.findRadioStation(radioName);

		// Assertions
		assertTrue(found.isPresent());
		Radio r = found.get();
		assertThat(r.getName(), is(radioName));
		URI expectedUri = new URI("https:\u002F\u002Fcdn-radiotime-logos.tunein.com\u002Fs13606q.png");
		assertThat(r.getLogoUri().get().toString(), is(expectedUri.toString()));
	}

	@Test
	public void findStationNoJson() throws Exception {
		String radioName = "Radio Paradise";

		PowerMockito.when(HttpUtils.doGet(TUNEIN_URL + radioName)).thenReturn(readFile(noJson));

		Optional<Radio> found = finder.findRadioStation(radioName);

		// Assertions
		assertThat(found, is(Optional.empty()));
	}

	@Test
	public void findStationNoStations() throws IOException {
		String radioName = "Radio Paradise";

		PowerMockito.when(HttpUtils.doGet(TUNEIN_URL + radioName)).thenReturn(readFile(noStations));

		Optional<Radio> found = finder.findRadioStation(radioName);

		// Assertions
		assertThat(found, is(Optional.empty()));
	}

	@Test
	public void findStationNoMatch() throws IOException {
		String radioName = "Radio Latina";

		PowerMockito.when(HttpUtils.doGet(TUNEIN_URL + radioName)).thenReturn(readFile(radioParadise));

		Optional<Radio> found = finder.findRadioStation(radioName);

		// Assertions
		assertThat(found, is(Optional.empty()));
	}

	@Test
	public void findStationSimilarMatch() throws IOException {
		String radioName = "Rad Paradiso";

		PowerMockito.when(HttpUtils.doGet(TUNEIN_URL + radioName)).thenReturn(readFile(radioParadise));

		Optional<Radio> found = finder.findRadioStation(radioName);

		// Assertions
		assertTrue(found.isPresent());
		Radio r = found.get();
		assertThat(r.getName(), is("Radio Paradise"));
		URI expectedUri = new URI("https:\u002F\u002Fcdn-radiotime-logos.tunein.com\u002Fs13606q.png");
		assertThat(r.getLogoUri().get().toString(), is(expectedUri.toString()));
	}

	@Test
	public void findStationSameBeginMatch() throws IOException {
		String radioName = "Radio para";

		PowerMockito.when(HttpUtils.doGet(TUNEIN_URL + radioName)).thenReturn(readFile(radioParadise));

		Optional<Radio> found = finder.findRadioStation(radioName);

		// Assertions
		assertTrue(found.isPresent());
		Radio r = found.get();
		assertThat(r.getName(), is("Radio Paradise"));
		URI expectedUri = new URI("https:\u002F\u002Fcdn-radiotime-logos.tunein.com\u002Fs13606q.png");
		assertThat(r.getLogoUri().get().toString(), is(expectedUri.toString()));
	}

}
