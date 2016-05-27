package bo.roman.radio.utilities;

public class RegExUtil {
	private final static String BEGINSWITH_TEMPL = "^\\Q%s\\E.*$";
	private final static String CONTAINSWORD_TEMPL = "^.*\\Q%s\\E.*$";
	
	private final String toMatch;
	
	private RegExUtil(String toMatch) {
		this.toMatch = toMatch;
	}
	
	public static RegExUtil phrase(String word) {
		return new RegExUtil(word);
	}
	
	public boolean beginsWith(String word) {
		return toMatch.matches(String.format(BEGINSWITH_TEMPL, word));
	}
	
	public boolean beginsWithIgnoreCase(String word) {
		return toMatch.toLowerCase().matches(String.format(BEGINSWITH_TEMPL, word.toLowerCase()));
	}
	
	public boolean containsIgnoreCase(String word) {
		return toMatch.toLowerCase().matches(String.format(CONTAINSWORD_TEMPL, word.toLowerCase()));
	}

}
