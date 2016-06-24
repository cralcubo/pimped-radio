package bo.roman.radio.cover.station;

import static bo.roman.radio.utilities.LoggerUtils.logDebug;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

import bo.roman.radio.cover.model.Radio;
import bo.roman.radio.utilities.LoggerUtils;
import bo.roman.radio.utilities.PhraseCalculator;
import bo.roman.radio.utilities.PhraseCalculator.PhraseMatch;

public class FacebookRadioStationFinder implements RadioStationFindable {

	private static final Logger log = LoggerFactory.getLogger(FacebookRadioStationFinder.class);
	
	private static final String SEARCHPAGE_TEMPLATE = "q='%s'&type=page&fields=id,name,category,picture";
	
	private static final String RADIOSTATION_CATEGORY = "Radio Station";
	
	private final Gson gsonParser;
	
	public FacebookRadioStationFinder() {
		gsonParser = new Gson();
	}

	@Override
	public Optional<Radio> findRadioStation(String radioName) {
		// First find all the radios that match the radio name
		List<Radio> radios = findAllRadioPages(radioName);
		
		// Sort and select the most relevant radio
		Optional<Radio> oRadio = findBestRadio(radios, radioName);
		
		log.info("Radio page found in Facebook: {}", oRadio);
		
		return oRadio;
	}
	
	/**
	 * Retrieve all the albums doing a request to 
	 * Facebook.
	 * All the radios that have a Page in Facebook
	 * that matches the name sent to it and which category
	 * is 'Radio Station' will be returned. 
	 * 
	 * @param radioName
	 * @return
	 */
	private List<Radio> findAllRadioPages(String radioName) {
		String searchQuery = String.format(SEARCHPAGE_TEMPLATE, radioName);
		Optional<String> optResponse = FacebookUtil.doSearch(searchQuery);
		
		if(optResponse.isPresent()) {
			// Convert to Radio
			Radio.Radios parsedRadios = gsonParser.fromJson(optResponse.get(), Radio.Radios.class);
			
			// Return all the pages which category is 'Radio Station'.
			List<Radio> radiosFound = parsedRadios.getData().stream()
					.filter(r -> RADIOSTATION_CATEGORY.equals(r.getCategory()))
					.filter(Radio::hasLogo)
					.peek(r -> r.setName(StringEscapeUtils.unescapeJava(r.getName())))
					.collect(Collectors.toList());
			
			log.info("Radios with the name [{}] found={}", radioName, radiosFound.size());
			logDebug(log, () -> radiosFound.toString());
			
			return radiosFound;
		}
		
		return Collections.emptyList();
	}
	
	/**
	 * From all the Radio Pages, filter them by exact name
	 * and return it.
	 * 
	 * If that is not the case, find the radio which name
	 * is closely related to the expected radio.
	 * 
	 * @param radios
	 * @return
	 */
	private Optional<Radio> findBestRadio(List<Radio> radios, String radioName) {
		log.info("Finding best match for Radio: {}", radioName);
		// Group all the radios by the similarity that their name have
		// with the radioName used to searched them.
		Map<PhraseMatch, List<Radio>> radioGroups = radios.stream()
				.collect(Collectors.groupingBy(r -> PhraseCalculator.phrase(radioName).calculateSimilarityTo(r.getName())));
		LoggerUtils.logDebug(log, () -> radioGroups.toString());
		
		Optional<Radio> exactMatch = findRadioByMatch(radioGroups, PhraseMatch.EXACT);
		if(exactMatch.isPresent()) {
			log.info("Exact match found for {}", radioName);
			return exactMatch;
		}
		
		Optional<Radio> similarMatch = findRadioByMatch(radioGroups, PhraseMatch.SIMILAR);
		Optional<Radio> similarBeginMatch = findRadioByMatch(radioGroups, PhraseMatch.SAME_BEGIN);
		if(similarBeginMatch.isPresent() || similarMatch.isPresent()) {
			Radio closeRadio = similarMatch.orElseGet(() -> similarBeginMatch.get());
			log.info("Close match found for {} is {}", radioName, closeRadio);
			return Optional.of(closeRadio);
		}
		
		Optional<Radio> cotainsMatch = findRadioByMatch(radioGroups, PhraseMatch.CONTAINS);
		log.info("Best Radio found for {} is {}", radioName, cotainsMatch);
		
		return cotainsMatch;
	}
	
	private Optional<Radio> findRadioByMatch(Map<PhraseMatch, List<Radio>> radioGroup, PhraseMatch match) {
		
		if(radioGroup.containsKey(match) && !radioGroup.get(match).isEmpty()) {
			Optional<Radio> aMatch = radioGroup.get(match).stream().findAny();
			return aMatch;
		}
		
		return Optional.empty();
	}

}
