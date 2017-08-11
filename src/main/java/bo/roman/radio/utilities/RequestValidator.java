package bo.roman.radio.utilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestValidator {
	private static final Logger log = LoggerFactory.getLogger(RequestValidator.class);
	public enum RequestStatus {REPEATED, INVALID, VALID}
	
	private String lastRequest = "";
	
	public RequestStatus validate(String request) {
		if(request == null || request.isEmpty()) {
			LoggerUtils.logDebug(log, () -> "Requests disabled because an empty request was provided.");
			return RequestStatus.INVALID;
		}
		
		if (request.equals(lastRequest)) {
			LoggerUtils.logDebug(log, () -> "Requests disabled because request:" + request + " was repeated.");
			return RequestStatus.REPEATED;
		}
		
		lastRequest = request;
		return RequestStatus.VALID;
	}

}
