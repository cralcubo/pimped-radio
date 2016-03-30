package bo.roman.radio.cover.station;

import static bo.roman.radio.utilities.LoggerUtils.logDebug;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

import bo.roman.radio.cover.model.Radio;

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
		
		log.info("Best radio page found in Facebook: {}", oRadio);
		
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
		if(radios == null || radios.isEmpty()) {
			return Optional.empty();
		}
		
		// First check if there is a radio that matches
		// exactly the name of the radio expected.
		// In case there is more than one, return any
		Optional<Radio> oRadio = radios.stream()
				.filter(r -> r.getName().equalsIgnoreCase(radioName))
				.findAny();
		
		if(oRadio.isPresent()) {
			log.info("Exact match found for {}", radioName);
			return oRadio;
		}
		
		// There is no exact match with the name of the radio
		// expected, then find the radio which name is closely
		// related to the radio expected.
		Optional<Radio> oMatchRadio = radios.stream()
				.filter(r -> r.getName().toLowerCase()
						     .matches(String.format("^%s\\s+.*$", radioName.toLowerCase())))
				.findAny();
		log.info("Closely radio found for {} is: {}", radioName, oMatchRadio);
		
		return oMatchRadio;
	}
	
	

}
