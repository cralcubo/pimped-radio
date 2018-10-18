package bo.roman.radio.utilities;

import java.util.ArrayList;
import java.util.List;

public class PhraseCalculator {
	/**
	 * Enum that represents the similarity between two strings.
	 * <p/>
	 * In order of similarity:
	 * <ul>
	 * <li>EXACT: Exact Match (Ignore Case)</li>
	 * <li>SIMILAR: Similar Match (Ignore Case) <br/>
	 * Close match to the rootPhrase, will proceed to: a) split camel cases b)
	 * remove accents c) make them lower case and trim it</li>
	 * <li>SAME_BEGIN: Both phrases begin with the same words</li>
	 * <li>CONTAINS: Contains part of the phrase</li>
	 * <li>DIFFERENT</li>
	 * </ul>
	 * 
	 * @author christian
	 *
	 */
	public enum PhraseMatch {
		EXACT, SIMILAR, SAME_BEGIN, CONTAINS, DIFFERENT
	}

	private static final String UNIONCHARS_REGEX = "(\\s*((and|with|or)|,|\\+|-|&{1,2}|\\|{1,2}|\\s|\\t)\\s*)";
	private static final String APOSTROPHES_REGEX = "'|`|’";
	private static final float MAXCHARACTERSDIF_PERCENT = 0.3f;

	private final String rootPhrase;

	private PhraseCalculator(final String rootPhrase) {
		this.rootPhrase = rootPhrase;
	}

	/**
	 * Sets the main phrase to test similarity against to.
	 * 
	 * @param val
	 *            is the main phrase to test similarity against to.
	 * @return an instance of the PhraseCalculator
	 */
	public static PhraseCalculator phrase(final String val) {
		return new PhraseCalculator(val);
	}

	/**
	 * 
	 * @param toPhrase
	 *            This is the phrase that will be used to see how similar is to the
	 *            phrase set in {@link #phrase(String)}
	 * @return a value from the enum {@link PhraseMatch}
	 */
	public PhraseMatch calculateSimilarityTo(final String toPhrase) {
		String toPhrase_ = toPhrase.toLowerCase();
		String rootPhrase_ = rootPhrase.toLowerCase();
		/*
		 * 01. Exact Match (Ignore Case)
		 */
		if (rootPhrase_.equals(toPhrase_)) {
			return PhraseMatch.EXACT;
		}
		/*
		 * 02. Similar Match (Ignore Case)
		 * 
		 * Now that we will try to find a close match to the rootPhrase, we will proceed
		 * to: - split camel cases - remove accents - make them lower case and trim it
		 */

		/* Split Camel Case */
		toPhrase_ = StringUtils.splitCamelCase(toPhrase);
		rootPhrase_ = StringUtils.splitCamelCase(rootPhrase);

		/* Remove accents */
		toPhrase_ = StringUtils.cleanIt(toPhrase_);
		rootPhrase_ = StringUtils.cleanIt(rootPhrase_);

		/* make them lower case */
		toPhrase_ = toPhrase_.toLowerCase().trim();
		rootPhrase_ = rootPhrase_.toLowerCase().trim();
		/*
		 * Ignore Union/Separation characters
		 * 
		 * Characters: , + - & | && || Words: and or with Spaces and Tabs
		 */
		toPhrase_ = toPhrase_.replaceAll(UNIONCHARS_REGEX, " ");
		rootPhrase_ = rootPhrase_.replaceAll(UNIONCHARS_REGEX, " ");
		/*
		 * Sometimes apostrophes come in different shapes: like ' ` or ’ We remove from
		 * the comparator this characters.
		 */
		toPhrase_ = toPhrase_.replaceAll(APOSTROPHES_REGEX, "");
		rootPhrase_ = rootPhrase_.replaceAll(APOSTROPHES_REGEX, "");

		if (checkSimilarity(rootPhrase_, toPhrase_)) {
			return PhraseMatch.SIMILAR;
		}

		/*
		 * 03. Both phrases begin with the same words
		 */
		if ((!rootPhrase_.isEmpty() && !toPhrase_.isEmpty())
				&& (rootPhrase_.startsWith(toPhrase_) || toPhrase_.startsWith(rootPhrase_))) {
			return PhraseMatch.SAME_BEGIN;
		}

		/*
		 * 04. Contains part of the phrase
		 */
		if ((!rootPhrase_.isEmpty() && !toPhrase_.isEmpty())
				&& (rootPhrase_.contains(toPhrase_) || toPhrase_.contains(rootPhrase_))) {
			return PhraseMatch.CONTAINS;
		}

		return PhraseMatch.DIFFERENT;
	}

	public boolean isExactTo(String toPhrase) {
		PhraseMatch pm = calculateSimilarityTo(toPhrase);
		return pm == PhraseMatch.EXACT;
	}

	/**
	 * Return true if the phrase to compare with is either: - An exact match - A
	 * close match (few letters might be different) - Has the same beginning
	 *
	 */
	public boolean isCloseTo(String toPhrase) {
		PhraseMatch pm = calculateSimilarityTo(toPhrase);
		return pm == PhraseMatch.EXACT || pm == PhraseMatch.SIMILAR || pm == PhraseMatch.SAME_BEGIN;
	}

	public boolean isDifferentTo(String toPhrase) {
		PhraseMatch pm = calculateSimilarityTo(toPhrase);
		return pm == PhraseMatch.DIFFERENT;
	}

	public boolean atLeastContains(String toPhrase) {
		PhraseMatch pm = calculateSimilarityTo(toPhrase);
		return pm != PhraseMatch.DIFFERENT;
	}

	private boolean checkSimilarity(String rootPhrase, String toCompare) {
		float allowedDiffChars = ((rootPhrase.length() + toCompare.length()) / 2) * MAXCHARACTERSDIF_PERCENT;

		return calculateCharsDifference(rootPhrase, toCompare) <= Math.ceil(allowedDiffChars);
	}

	public int calculateCharsDifference(String toCompare) {
		return calculateCharsDifference(rootPhrase, toCompare);
	}

	private int calculateCharsDifference(String rootPhrase, String toCompare) {
		List<Character> c1 = toChars(rootPhrase);
		List<Character> c2 = toChars(toCompare);

		// Find differences
		List<Character> diff1 = removeChars(c1, c2);
		List<Character> diff2 = removeChars(c2, c1);

		// Add both differences
		diff1.addAll(diff2);
		return diff1.size();
	}

	private List<Character> toChars(String val) {
		List<Character> c = new ArrayList<>();
		for (int i = 0; i < val.length(); i++) {
			c.add(val.charAt(i));
		}

		return c;
	}

	private List<Character> removeChars(List<Character> from, List<Character> chars) {
		List<Character> tempFrom = new ArrayList<>(from);
		for (Character ch : chars) {
			tempFrom.remove(ch);
		}

		return tempFrom;
	}

}
