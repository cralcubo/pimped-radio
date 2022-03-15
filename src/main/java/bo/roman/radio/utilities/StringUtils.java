package bo.roman.radio.utilities;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Optional;

import org.apache.commons.codec.CharEncoding;
import org.apache.commons.lang3.StringEscapeUtils;

public interface StringUtils {
	final static String LINE_SEPARATOR = System.getProperty("line.separator");
	final static String NOCAMELCASE_REGEX = "(?<=[a-z])(?=[A-Z])|(?<=[A-Z])(?=[A-Z][a-z])";
	static final String PERC2LETTER_REGEX = "%(?![0-9a-fA-F]{2})";
	
	static final String BRACKETS_REGEX = "(?<=[\\w\\s])[\\(|\\[].*[\\)|\\]].*";
	static final String FEATURING_REGEX = "(?i)(?<=.)(\\s+(ft\\.|ft|feat\\.|feat|featuring|feature|f/|f\\.).*)";
	static final String AFTERTOKEN_REGEX = "[\\*+-\\/~\\|\\\\].*";

	/**
	 * @return True if {@code val} is non null and non empty
	 */
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
	
	static String removeBracketsInfo(String val) {
		return val.replaceAll(BRACKETS_REGEX, "").trim();
	}
	
	static String removeFeatureInfo(String val) {
		return val.replaceAll(FEATURING_REGEX, "").trim();
	}

	/**
	 * Removes extra information provided in the string, such as:
	 * usefulText * uselessText
	 * usefulText / uselessText
	 *
	 * Everything that is after the 'token' characters: *, +, -, /, \, |, ~
	 * will be deleted.
	 */
	static String removeExtraInfo(String val) {
		return val.replaceAll(AFTERTOKEN_REGEX, "").trim();
	}

	/**
	 * <ul>
	 * <li> Replace line.separator to space</li>
	 * <li> If val is URL encoded, decode it.</li>
	 * <li> If val is HTML encoded, decode it.</li>
	 * <li> If val has accents, remove them.</li>
	 * <li> Trim the val.
	 * </ul> 
	 * 
	 * @param val
	 * @return
	 */
	static String cleanIt(String val) {
		val = nullIsEmpty(val);
		val = val.replaceAll(LINE_SEPARATOR, " ");
		val = decodeUrl(val);
		val = StringEscapeUtils.unescapeHtml4(val);
		val = org.apache.commons.lang3.StringUtils.stripAccents(val);
		
		return val.trim();
	}
	
	/**
	 * Decode a URL encoded String, taking into account that the characters
	 * present in the String to decode got a replacement for the characters %
	 * and +. Those characters are replaced by: %2B (+) and %25 (%)
	 */
	static String decodeUrl(String data) {
		String utf8 = CharEncoding.UTF_8;
		try {
			data = data.replaceAll(PERC2LETTER_REGEX, "%25");
			data = data.replaceAll("\\+", "%2B");
			return URLDecoder.decode(data, utf8);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(String.format(
					"Completly unexpected situation. %s is supposed to be a supported Character Encoding.", utf8));
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
