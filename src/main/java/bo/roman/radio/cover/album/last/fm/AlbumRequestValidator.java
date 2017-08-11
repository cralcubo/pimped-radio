package bo.roman.radio.cover.album.last.fm;

import bo.roman.radio.utilities.RequestValidator;
import bo.roman.radio.utilities.RequestValidator.RequestStatus;
import bo.roman.radio.utilities.StringUtils;

public class AlbumRequestValidator {
	
	private final static RequestValidator REQUEST_VALIDATOR = new RequestValidator();

	public static RequestStatus getRequestStatus(String track, String artist) {
		if (!StringUtils.exists(track) || !StringUtils.exists(artist)) {
			return RequestStatus.INVALID;
		}
		
		return REQUEST_VALIDATOR.validate(String.format("[%s-%s]", track, artist));
	}

}
