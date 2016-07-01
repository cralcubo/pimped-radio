package bo.roman.radio.utilities;

import java.util.ArrayList;
import java.util.List;

public class PhraseCalculator {
	public enum PhraseMatch {EXACT, SIMILAR, SAME_BEGIN, CONTAINS, DIFFERENT}
	
	private static final String UNIONCHARS_REGEX = "(\\s*((and|with|or)|,|\\+|-|&{1,2}|\\|{1,2}|\\s|\\t)\\s*)";
	private static final String APOSTROPHES_REGEX = "'|`|’";
	private static final float MAXCHARACTERSDIF_PERCENT = 0.3f;
	
	private final String rootPhrase;
	
	
	private PhraseCalculator(final String rootPhrase) {
		this.rootPhrase = rootPhrase;
	}
	
	public static PhraseCalculator phrase(final String val) {
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
		/*
		 * Sometimes apostrophes come in different shapes: like ' ` or ’
		 * We remove from the comparator this characters.
		 */
		toPhrase_ = toPhrase_.replaceAll(APOSTROPHES_REGEX, "");
		rootPhrase_ = rootPhrase_.replaceAll(APOSTROPHES_REGEX, "");
		
		if(checkSimilarity(rootPhrase_, toPhrase_)) {
			return PhraseMatch.SIMILAR;
		}
		
		/*
		 * 03. Both phrases begin with the same words
		 */
		if((!rootPhrase_.isEmpty() && !toPhrase_.isEmpty()) && (rootPhrase_.startsWith(toPhrase_) || toPhrase_.startsWith(rootPhrase_))) {
			return PhraseMatch.SAME_BEGIN;
		}
		
		/*
		 * 04. Contains part of the phrase
		 */
		if((!rootPhrase_.isEmpty() && !toPhrase_.isEmpty()) && (rootPhrase_.contains(toPhrase_) || toPhrase_.contains(rootPhrase_))) {
			return PhraseMatch.CONTAINS;
		}
		
		return PhraseMatch.DIFFERENT;
	}
	
	public boolean isExactTo(String toPhrase) {
		PhraseMatch pm = calculateSimilarityTo(toPhrase);
		return pm == PhraseMatch.EXACT; 
	}
	
	public boolean isDifferentTo(String toPhrase) {
		PhraseMatch pm = calculateSimilarityTo(toPhrase);
		return pm == PhraseMatch.DIFFERENT; 
	}
	
	public boolean isSimilarTo(String toPhrase) {
		PhraseMatch pm = calculateSimilarityTo(toPhrase);
		return pm == PhraseMatch.EXACT || pm == PhraseMatch.SIMILAR; 
	}
	
	public boolean hasSameBeginAs(String toPhrase) {
		PhraseMatch pm = calculateSimilarityTo(toPhrase);
		return pm == PhraseMatch.EXACT || pm == PhraseMatch.SAME_BEGIN; 
	}
	
	public boolean atLeastContains(String toPhrase) {
		PhraseMatch pm = calculateSimilarityTo(toPhrase);
		return pm != PhraseMatch.DIFFERENT; 
	}

	private boolean checkSimilarity(String rootPhrase, String toCompare) {
		List<Character> c1 = toChars(rootPhrase);
		List<Character> c2 = toChars(toCompare);
		
		// Find differences
		List<Character> diff1 = removeChars(c1, c2);
		List<Character> diff2 = removeChars(c2, c1);
		
		// Add both differences
		diff1.addAll(diff2);
		
		float allowedDiffChars = ((rootPhrase.length() + toCompare.length())/2) * MAXCHARACTERSDIF_PERCENT;  
		
		return diff1.size() <= Math.ceil(allowedDiffChars);
	}
	
	private List<Character> toChars(String val) {
		List<Character> c = new ArrayList<>();
		for(int i = 0; i < val.length(); i++) {
			c.add(val.charAt(i));
		}
		
		return c;
	}
	
	private List<Character> removeChars(List<Character> from, List<Character> chars) {
		List<Character> tempFrom = new ArrayList<>(from);
		for(Character ch : chars) {
			tempFrom.remove(ch);
		}
		
		return tempFrom;
	}
	
}
