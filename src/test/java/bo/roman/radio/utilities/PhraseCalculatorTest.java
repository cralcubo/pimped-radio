package bo.roman.radio.utilities;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.junit.Test;

import bo.roman.radio.utilities.PhraseCalculator.PhraseMatch;

public class PhraseCalculatorTest {
	
	@Test
	public void testExactMatch() {
		String phrase = "Peace and Love";
		String toTest = "Peace and Love";
		
		assertThat(PhraseCalculator.withPhrase(phrase).calculateSimilarityTo(toTest), is(PhraseMatch.EXACT));
	}
	
	@Test
	public void testExactMatch2() {
		String phrase = "Peace and Love";
		String toTest = "peace AND LovE";
		
		assertThat(PhraseCalculator.withPhrase(phrase).calculateSimilarityTo(toTest), is(PhraseMatch.EXACT));
	}
	
	@Test
	public void testSimilarMatch_cleaningUnionSeparationCharacters() {
		String phrase = "Peace and Love";
		String toTest = "Peace Love ";
		
		assertThat(PhraseCalculator.withPhrase(phrase).calculateSimilarityTo(toTest), is(PhraseMatch.SIMILAR));
	}
	
	@Test
	public void testSimilarMatch_cleaningUnionSeparationCharacters2() {
		String phrase = "Peace and Love";
		String toTest = "Peace, Love";
		
		assertThat(PhraseCalculator.withPhrase(phrase).calculateSimilarityTo(toTest), is(PhraseMatch.SIMILAR));
	}
	
	@Test
	public void testSimilarMatch_cleaningUnionSeparationCharacters3() {
		String phrase = "Peace and Love";
		String toTest = "Peace or Love";
		
		assertThat(PhraseCalculator.withPhrase(phrase).calculateSimilarityTo(toTest), is(PhraseMatch.SIMILAR));
	}
	
	@Test
	public void testSimilarMatch_cleaningUnionSeparationCharacters4() {
		String phrase = "Peace and Love";
		String toTest = "Peace + Love";
		
		assertThat(PhraseCalculator.withPhrase(phrase).calculateSimilarityTo(toTest), is(PhraseMatch.SIMILAR));
	}
	
	@Test
	public void testSimilarMatch_cleaningUnionSeparationCharacters5() {
		String phrase = "Peace, Love and More";
		String toTest = "Peace Love & more";
		
		assertThat(PhraseCalculator.withPhrase(phrase).calculateSimilarityTo(toTest), is(PhraseMatch.SIMILAR));
	}
	
	@Test
	public void testSimilarMatch_cleaningUnionSeparationCharacters6() {
		String phrase = "ClassicRock";
		String toTest = "Classic Rock";
		
		assertThat(PhraseCalculator.withPhrase(phrase).calculateSimilarityTo(toTest), is(PhraseMatch.SIMILAR));
	}
	
	@Test
	public void testSimilarMatch_cleaningUnionSeparationCharacters7() {
		String phrase = "Classic Rock";
		String toTest = "ClassicRock";
		
		assertThat(PhraseCalculator.withPhrase(phrase).calculateSimilarityTo(toTest), is(PhraseMatch.SIMILAR));
	}
	
	@Test
	public void testSimilarMatch_cleaningUnionSeparationCharacters8() {
		String phrase = "Kaskade with Deadmau5";
		String toTest = "Kaskade & Deadmau5";
		
		assertThat(PhraseCalculator.withPhrase(phrase).calculateSimilarityTo(toTest), is(PhraseMatch.SIMILAR));
	}
	
	@Test
	public void testSimilarMatch_cleaningUnionSeparationCharacters9() {
		String phrase = "Kaskade with Deadmau5";
		String toTest = "Kaskade   with   Deadmau5";
		
		assertThat(PhraseCalculator.withPhrase(phrase).calculateSimilarityTo(toTest), is(PhraseMatch.SIMILAR));
	}
	
	@Test
	public void testSimilarMatch_cleaningAposthrofes() {
		String phrase = "Playin' around";
		String toTest = "Playing Around";
		
		assertThat(PhraseCalculator.withPhrase(phrase).calculateSimilarityTo(toTest), is(PhraseMatch.SIMILAR));
	}
	
	@Test
	public void testSimilarMatch_cleaningAposthrofes2() {
		String phrase = "Playin` around";
		String toTest = "Playing around";
		
		assertThat(PhraseCalculator.withPhrase(phrase).calculateSimilarityTo(toTest), is(PhraseMatch.SIMILAR));
	}
	
	@Test
	public void testSimilarMatch_cleaningAposthrofes3() {
		String phrase = "Playin' around";
		String toTest = "PlayingAround";
		
		assertThat(PhraseCalculator.withPhrase(phrase).calculateSimilarityTo(toTest), is(PhraseMatch.SIMILAR));
	}
	
	@Test
	public void testSimilarMatch_reverted() {
		String phrase = "Christian, Roman";
		String toTest = "Roman Christian";
		
		assertThat(PhraseCalculator.withPhrase(phrase).calculateSimilarityTo(toTest), is(PhraseMatch.SIMILAR));
	}
	
	@Test
	public void testSimilarMatch_extraChars() {
		String phrase = "I've played";
		String toTest = "I ve played";
		
		assertThat(PhraseCalculator.withPhrase(phrase).calculateSimilarityTo(toTest), is(PhraseMatch.SIMILAR));
	}
	
	@Test
	public void testSameBegin() {
		String phrase = "Escape me";
		String toTest = "Escape me (Remix 2005)";
		
		assertThat(PhraseCalculator.withPhrase(phrase).calculateSimilarityTo(toTest), is(PhraseMatch.SAME_BEGIN));
	}
	
	@Test
	public void testSameBegin2() {
		String phrase = "Escape me - Tiesto";
		String toTest = "Escape me";
		
		assertThat(PhraseCalculator.withPhrase(phrase).calculateSimilarityTo(toTest), is(PhraseMatch.SAME_BEGIN));
	}
	
	@Test
	public void testSameBegin3() {
		String phrase = "Escape me - Tiesto";
		String toTest = "ESCAPE ME";
		
		assertThat(PhraseCalculator.withPhrase(phrase).calculateSimilarityTo(toTest), is(PhraseMatch.SAME_BEGIN));
	}
	
	@Test
	public void testSameBegin4() {
		String phrase = "Escaping me - Tiesto";
		String toTest = "Escapin' me ";
		
		assertThat(PhraseCalculator.withPhrase(phrase).calculateSimilarityTo(toTest), is(PhraseMatch.SAME_BEGIN));
	}
	
	@Test
	public void testSameBegin5() {
		String phrase = "RapRadio";
		String toTest = "Rap Radio California";
		
		assertThat(PhraseCalculator.withPhrase(phrase).calculateSimilarityTo(toTest), is(PhraseMatch.SAME_BEGIN));
	}
	
	@Test
	public void testSameBegin6() {
		String phrase = "Sound & Vision: Tribute to David Bowie";
		String toTest = "Sound and Vision";
		
		assertThat(PhraseCalculator.withPhrase(phrase).calculateSimilarityTo(toTest), is(PhraseMatch.SAME_BEGIN));
	}
	
	@Test
	public void testContains() {
		String phrase = "Rhianna ft. Drake";
		String toTest = " DRAKE ";
		
		assertThat(PhraseCalculator.withPhrase(phrase).calculateSimilarityTo(toTest), is(PhraseMatch.CONTAINS));
	}
	
	@Test
	public void testContains2() {
		String phrase = "Nevermind";
		String toTest = "Nirvana  -  Nevermind";
		
		assertThat(PhraseCalculator.withPhrase(phrase).calculateSimilarityTo(toTest), is(PhraseMatch.CONTAINS));
	}
	
	@Test
	public void testContains3() {
		String phrase = "In bloom";
		String toTest = "Nirvana - In Bloom - Nevermind";
		
		assertThat(PhraseCalculator.withPhrase(phrase).calculateSimilarityTo(toTest), is(PhraseMatch.CONTAINS));
	}
	
	@Test
	public void testDifferent() {
		String phrase = "Casa grande";
		String toTest = "Big House";
		
		assertThat(PhraseCalculator.withPhrase(phrase).calculateSimilarityTo(toTest), is(PhraseMatch.DIFFERENT));
	}

}
