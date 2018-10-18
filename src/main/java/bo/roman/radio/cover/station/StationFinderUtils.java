package bo.roman.radio.cover.station;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bo.roman.radio.cover.model.Radio;
import bo.roman.radio.utilities.LoggerUtils;

public class StationFinderUtils {
	private static final Logger log = LoggerFactory.getLogger(StationFinderUtils.class);

	public static Optional<Radio> requestController(String radioName, RadioStationFindable finder) {

		switch (StationFinderRequestValidator.validate(radioName)) {
		case VALID:
			return finder.findRadioStation(radioName);
		case REPEATED:
			return finder.getCachedRadio();
		default:
			// No valid request
			LoggerUtils.logDebug(log, () -> "Invalid radio name provided:" + radioName);
			return Optional.empty();
		}
	}

}
