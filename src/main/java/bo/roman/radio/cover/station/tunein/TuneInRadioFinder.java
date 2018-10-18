package bo.roman.radio.cover.station.tunein;

import static bo.roman.radio.utilities.StringUtils.exists;
import static java.util.Optional.empty;
import static java.util.stream.Collectors.groupingBy;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

import bo.roman.radio.cover.model.Radio;
import bo.roman.radio.cover.station.RadioStationFindable;
import bo.roman.radio.cover.station.tunein.TuneInStations.SearchResults.ContainerItems.Children;
import bo.roman.radio.utilities.HttpUtils;
import bo.roman.radio.utilities.PhraseCalculator;
import bo.roman.radio.utilities.PhraseCalculator.PhraseMatch;

public class TuneInRadioFinder implements RadioStationFindable {
	private static final Logger log = LoggerFactory.getLogger(TuneInRadioFinder.class);

	private final static String TUNEIN_URL = "https://tunein.com/search/?query=";

	private static final Pattern jsonPattern = Pattern.compile("\"search\":\\{.*\\},\"player\"");

	private static final String CONTAINERTYPE_STATIONS = "Stations";

	private static Gson parser = new Gson();
	
	private Optional<Radio> cachedRadio = Optional.empty();
	
	@Override
	public Optional<Radio> getCachedRadio() {
		return cachedRadio;
	}

	@Override
	public Optional<Radio> findRadioStation(String radioName) {
		Optional<Radio> radio;
		// Do a HttpRequest to TuneIn
		try {
			TuneInStations stations = parseResponse(HttpUtils.doGet(TUNEIN_URL + radioName));
			// Find the best radio retrieved from TuneIn
			radio = findBestRadio(stations, radioName);
		} catch (IOException e) {
			log.error("There was an error sending a radio request to TuneIn");
			radio = empty();
		} catch (NoJsonException e) {
			log.warn("No JSON content was found querying for the radio: " + radioName);
			radio = empty();
		}
		cachedRadio = radio;
		return radio;
	}

	private Optional<Radio> findBestRadio(TuneInStations tuneinData, String radioName) {
		Map<PhraseMatch, List<Children>> stations = tuneinData.searchResults.containerItems.stream()//
				.filter(ci -> ci.containerType.equals(CONTAINERTYPE_STATIONS))//
				.flatMap(ci -> ci.children.stream())//
				.filter(c -> exists(c.image) && exists(c.title))//
				.collect(
						groupingBy(station -> PhraseCalculator.phrase(radioName).calculateSimilarityTo(station.title)));
		
		Predicate<PhraseMatch> nonNullEmpty = pm -> stations.get(pm) != null && !stations.get(pm).isEmpty();
		
		if (nonNullEmpty.test(PhraseMatch.EXACT)) {
			log.info("Exact match found for {}", radioName);
			return toRadio(stations.get(PhraseMatch.EXACT).get(0));
		}

		if (nonNullEmpty.test(PhraseMatch.SIMILAR)) {
			log.info("Similar match found for {}", radioName);
			return toRadio(stations.get(PhraseMatch.SIMILAR).get(0));
		}

		if (nonNullEmpty.test(PhraseMatch.SAME_BEGIN)) {
			log.info("Same begin match found for {}", radioName);
			return toRadio(stations.get(PhraseMatch.SAME_BEGIN).get(0));
		}

		// No radio found
		log.info("No radio found for {}", radioName);
		return Optional.empty();
	}

	private Optional<Radio> toRadio(Children station) {
		Optional<URI> uri;
		try {
			uri = Optional.of(new URI(station.image));
		} catch (URISyntaxException e) {
			uri = Optional.empty();
		}

		return Optional.of(new Radio.Builder().name(station.title).logoUri(uri).build());
	}

	private TuneInStations parseResponse(String httpResponse) throws NoJsonException {
		Matcher m = jsonPattern.matcher(httpResponse);
		String json = null;
		while (m.find()) {
			json = m.group();
		}
		if (json == null) {
			throw new NoJsonException();
		}
		// Clean JSON from sentinels: "search" and "player"
		json = json.replace("\"search\":", "").replace(",\"player\"", "");
		
		// Parse the JSON response with GSON
		return parser.fromJson(json, TuneInStations.class);
	}

	private static class NoJsonException extends Exception {
		private static final long serialVersionUID = 1L;
	}
}
