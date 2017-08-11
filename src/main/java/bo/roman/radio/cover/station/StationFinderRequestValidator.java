package bo.roman.radio.cover.station;

import bo.roman.radio.utilities.RequestValidator.RequestStatus;
import bo.roman.radio.utilities.RequestValidator;
import bo.roman.radio.utilities.StringUtils;

public class StationFinderRequestValidator {
	
	private final static RequestValidator REQUEST_VALIDATOR = new RequestValidator();
	
	public static RequestStatus validate(String radioName) {
		radioName = StringUtils.nullIsEmpty(radioName);
		if(radioName.startsWith("http://") || radioName.startsWith("https://")) {
			// This is not a valid radio name
			return RequestStatus.INVALID;
		}
		
		return REQUEST_VALIDATOR.validate(radioName);
	}

}
