package bo.roman.radio.utilities;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Optional;

import org.apache.commons.codec.CharEncoding;
import org.apache.commons.lang3.StringEscapeUtils;

public interface StringUtils {
	final static String LINE_SEPARATOR = System.getProperty("line.separator");
	final static String NOCAMELCASE_REGEX = "(?<=[a-z])(?=[A-Z])|(?<=[A-Z])(?=[A-Z][a-z])";
	
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

	/**
	 * - Replace line.separator to space
	 * - If val is URL encoded, decode it.
	 * - If val is HTML encoded, decode it.
	 * - If val has accents, remove them.
	 * - Trim the val. 
	 * 
	 * @param val
	 * @return
	 */
	static String cleanIt(String val) {
		String utf8 = CharEncoding.UTF_8; 
		try {
			val = nullIsEmpty(val);
			val = val.replaceAll(LINE_SEPARATOR, " ");
			val = URLDecoder.decode(val, utf8);
			val = StringEscapeUtils.unescapeHtml4(val);
			val = org.apache.commons.lang3.StringUtils.stripAccents(val);
			
			return val.trim();
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(String.format("Completly unexpected situation. %s is supposed to be a supported Character Encoding.", utf8)); 
		}
	}
	
	static String splitCamelCase(String name) {
		// Split the name if is camelCase
		StringBuilder noCamelName = new StringBuilder();
		for (String v : name.split(NOCAMELCASE_REGEX)) {
			noCamelName.append(v).append(" ");
		}
		return noCamelName.toString().trim();
	}

}
