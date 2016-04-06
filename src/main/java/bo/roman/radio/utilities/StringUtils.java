package bo.roman.radio.utilities;

import java.util.Optional;

public interface StringUtils {
	
	static boolean exists(String val) {
		return val != null && !val.trim().isEmpty();
	}
	
	static boolean exists(Optional<String> val) {
		return val != null && val.isPresent() && exists(val.get());
	}
	
	static String nullIsEmpty(String val) {
		if (val == null) {
			val = "";
		}
		return val;
	}
	
	static String cleanIt(String val) {
		return nullIsEmpty(val).trim();
	}

}
