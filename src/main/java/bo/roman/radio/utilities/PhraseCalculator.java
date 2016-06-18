package bo.roman.radio.utilities;

import java.util.ArrayList;
import java.util.List;

public class PhraseCalculator {
	public enum PhraseMatch {EXACT, SIMILAR, SAME_BEGIN, CONTAINS, DIFFERENT}
	
	private static final String UNIONCHARS_REGEX = "(\\s*((and|with|or)|,|\\+|-|&{1,2}|\\|{1,2}|\\s|\\t)\\s*)";
	private static final String BEGINSWITH_TEMPL = "^\\Q%s\\E.*$";
	private static final int MAXCHARACTERS_DIF = 2;
	
	private final String rootPhrase;
	
	
	private PhraseCalculator(final String rootPhrase) {
		this.rootPhrase = rootPhrase;
	}
	
	public static PhraseCalculator withPhrase(final String val) {
		return new PhraseCalculator(val);
	}
	
	public PhraseMatch calculateSimilarityTo(final String toPhrase) {
		String toPhrase_ = toPhrase.toLowerCase();
		String rootPhrase_ = rootPhrase.toLowerCase();
		/*
		 * 01. Exact Match (Ignore Case)
		 */
		if(rootPhrase_.equals(toPhrase_)) {
			return PhraseMatch.EXACT;
		}
		/*
		 * 02. Similar Match (Ignore Case)
		 * 
		 * Now that we will try to find a close
		 * match to the rootPhrase, we will proceed
		 * to:
		 * - split camel cases
		 * - remove accents
		 * - make them lower case and trim it
		 */
		
		/* Split Camel Case*/
		toPhrase_ = StringUtils.splitCamelCase(toPhrase);
		rootPhrase_ = StringUtils.splitCamelCase(rootPhrase);
		
		/* Remove accents*/
		toPhrase_ = StringUtils.cleanIt(toPhrase_);
		rootPhrase_ = StringUtils.cleanIt(rootPhrase_);
		
		/* make them lower case */
		toPhrase_ = toPhrase_.toLowerCase().trim();
		rootPhrase_ = rootPhrase_.toLowerCase().trim();
		/* 
		 * Ignore Union/Separation characters
		 * 
		 * Characters: , + - & | && ||
		 * Words: and or with
		 * Spaces and Tabs
		 */
		toPhrase_ = toPhrase_.replaceAll(UNIONCHARS_REGEX, " ");
		rootPhrase_ = rootPhrase_.replaceAll(UNIONCHARS_REGEX, " ");
		
		if(checkSimilarity(rootPhrase_, toPhrase_)) {
			return PhraseMatch.SIMILAR;
		}
		
		/*
		 * 03. Both phrases begin with the same words
		 */
		if(rootPhrase_.matches(String.format(BEGINSWITH_TEMPL, toPhrase_)) || toPhrase_.matches(String.format(BEGINSWITH_TEMPL, rootPhrase_))) {
			return PhraseMatch.SAME_BEGIN;
		}
		
		/*
		 * 04. Contains part of the phrase
		 */
		if(rootPhrase_.contains(toPhrase_) || toPhrase_.contains(rootPhrase_)) {
			return PhraseMatch.CONTAINS;
		}
		
		return PhraseMatch.DIFFERENT;
	}
	
	private boolean checkSimilarity(String val1, String val2) {
		List<Character> c1 = new ArrayList<>();
		for(int i = 0; i < val1.length(); i++) {
			c1.add(val1.charAt(i));
		}
		List<Character> c2 = new ArrayList<>();
		for(int i = 0; i < val2.length(); i++) {
			c2.add(val2.charAt(i));
		}
		List<Character> union = new ArrayList<>(c1);
		union.addAll(c2);
		
		List<Character> intersection = new ArrayList<>(c1);
		intersection.retainAll(c2);
		
		union.removeAll(intersection);
		
		return union.size() <= MAXCHARACTERS_DIF; 
	}

}
