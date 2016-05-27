package bo.roman.radio.utilities;

public class RegExUtil {
	private final static String BEGINSWITH_TEMPL = "^\\b%s\\b.*$";
	private final static String CONTAINSWORD_TEMPL = "^.*\\b%s\\b.*$";
	private static final String SPECIALCHARS_REGEX = "([\\\\\\.\\[\\{\\(\\*\\+\\?\\^\\$\\|])";
	
	private final String toMatch;
	
	private RegExUtil(String toMatch) {
		this.toMatch = escSpecChars(toMatch);
	}
	
	public static RegExUtil phrase(String word) {
		return new RegExUtil(word);
	}
	
	public boolean beginsWith(String word) {
		word = escSpecChars(word);
		return toMatch.matches(String.format(BEGINSWITH_TEMPL, word));
	}
	
	public boolean beginsWithIgnoreCase(String word) {
		word = escSpecChars(word);
		return toMatch.toLowerCase().matches(String.format(BEGINSWITH_TEMPL, word.toLowerCase()));
	}
	
	public boolean containsIgnoreCase(String word) {
		word = escSpecChars(word);
		return toMatch.toLowerCase().matches(String.format(CONTAINSWORD_TEMPL, word.toLowerCase()));
	}
	
	private String escSpecChars(String val){
		return val.replaceAll(SPECIALCHARS_REGEX, "\\\\$1");
	}
}
