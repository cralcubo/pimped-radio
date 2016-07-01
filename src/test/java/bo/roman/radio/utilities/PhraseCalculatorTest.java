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
		
		assertThat(PhraseCalculator.phrase(phrase).calculateSimilarityTo(toTest), is(PhraseMatch.EXACT));
	}
	
	@Test
	public void testExactMatch2() {
		String phrase = "Peace and Love";
		String toTest = "peace AND LovE";
		
		assertThat(PhraseCalculator.phrase(phrase).calculateSimilarityTo(toTest), is(PhraseMatch.EXACT));
	}
	
	@Test
	public void testSimilarMatch_cleaningUnionSeparationCharacters() {
		String phrase = "Peace and Love";
		String toTest = "Peace Love ";
		
		assertThat(PhraseCalculator.phrase(phrase).calculateSimilarityTo(toTest), is(PhraseMatch.SIMILAR));
	}
	
	@Test
	public void testSimilarMatch_cleaningUnionSeparationCharacters2() {
		String phrase = "Peace and Love";
		String toTest = "Peace, Love";
		
		assertThat(PhraseCalculator.phrase(phrase).calculateSimilarityTo(toTest), is(PhraseMatch.SIMILAR));
	}
	
	@Test
	public void testSimilarMatch_cleaningUnionSeparationCharacters3() {
		String phrase = "Peace and Love";
		String toTest = "Peace or Love";
		
		assertThat(PhraseCalculator.phrase(phrase).calculateSimilarityTo(toTest), is(PhraseMatch.SIMILAR));
	}
	
	@Test
	public void testSimilarMatch_cleaningUnionSeparationCharacters4() {
		String phrase = "Peace and Love";
		String toTest = "Peace + Love";
		
		assertThat(PhraseCalculator.phrase(phrase).calculateSimilarityTo(toTest), is(PhraseMatch.SIMILAR));
	}
	
	@Test
	public void testSimilarMatch_cleaningUnionSeparationCharacters5() {
		String phrase = "Peace, Love and More";
		String toTest = "Peace Love & more";
		
		assertThat(PhraseCalculator.phrase(phrase).calculateSimilarityTo(toTest), is(PhraseMatch.SIMILAR));
	}
	
	@Test
	public void testSimilarMatch_cleaningUnionSeparationCharacters6() {
		String phrase = "ClassicRock";
		String toTest = "Classic Rock";
		
		assertThat(PhraseCalculator.phrase(phrase).calculateSimilarityTo(toTest), is(PhraseMatch.SIMILAR));
	}
	
	@Test
	public void testSimilarMatch_cleaningUnionSeparationCharacters7() {
		String phrase = "Classic Rock";
		String toTest = "ClassicRock";
		
		assertThat(PhraseCalculator.phrase(phrase).calculateSimilarityTo(toTest), is(PhraseMatch.SIMILAR));
	}
	
	@Test
	public void testSimilarMatch_cleaningUnionSeparationCharacters8() {
		String phrase = "Kaskade with Deadmau5";
		String toTest = "Kaskade & Deadmau5";
		
		assertThat(PhraseCalculator.phrase(phrase).calculateSimilarityTo(toTest), is(PhraseMatch.SIMILAR));
	}
	
	@Test
	public void testSimilarMatch_cleaningUnionSeparationCharacters9() {
		String phrase = "Kaskade with Deadmau5";
		String toTest = "Kaskade   with   Deadmau5";
		
		assertThat(PhraseCalculator.phrase(phrase).calculateSimilarityTo(toTest), is(PhraseMatch.SIMILAR));
	}
	
	@Test
	public void testSimilarMatch_cleaningUnionSeparationCharacters10() {
		String phrase = "TOKYO (VAMPIRES&WOLVES)";
		String toTest = "Tokyo (Vampires & Wolves)";
		
		assertThat(PhraseCalculator.phrase(phrase).calculateSimilarityTo(toTest), is(PhraseMatch.SIMILAR));
	}
	
	@Test
	public void testSimilarMatch_cleaningAposthrofes() {
		String phrase = "Playin' around";
		String toTest = "Playing Around";
		
		assertThat(PhraseCalculator.phrase(phrase).calculateSimilarityTo(toTest), is(PhraseMatch.SIMILAR));
	}
	
	@Test
	public void testSimilarMatch_cleaningAposthrofes2() {
		String phrase = "Playin` around";
		String toTest = "Playing around";
		
		assertThat(PhraseCalculator.phrase(phrase).calculateSimilarityTo(toTest), is(PhraseMatch.SIMILAR));
	}
	
	@Test
	public void testSimilarMatch_cleaningAposthrofes3() {
		String phrase = "Playin' around";
		String toTest = "PlayingAround";
		
		assertThat(PhraseCalculator.phrase(phrase).calculateSimilarityTo(toTest), is(PhraseMatch.SIMILAR));
	}
	
	@Test
	public void testSimilarMatch_reverted() {
		String phrase = "Christian, Roman";
		String toTest = "Roman Christian";
		
		assertThat(PhraseCalculator.phrase(phrase).calculateSimilarityTo(toTest), is(PhraseMatch.SIMILAR));
	}
	
	@Test
	public void testSimilarMatch_extraChars() {
		String phrase = "I've played";
		String toTest = "I ve played";
		
		assertThat(PhraseCalculator.phrase(phrase).calculateSimilarityTo(toTest), is(PhraseMatch.SIMILAR));
	}
	
	@Test
	public void testSimilarMatch_extraChars2() {
		String phrase = "Shy Carter Aleon Craft";
		String toTest = "Shy Carter feat. Aleon Craft";
		
		assertThat(PhraseCalculator.phrase(phrase).calculateSimilarityTo(toTest), is(PhraseMatch.SIMILAR));
	}
	
	@Test
	public void testSimilarMatch_extraChars3() {
		String phrase = "Pink";
		String toTest = "P!nk";
		
		assertThat(PhraseCalculator.phrase(phrase).calculateSimilarityTo(toTest), is(PhraseMatch.SIMILAR));
	}
	
	@Test
	public void testSameBegin() {
		String phrase = "Escape me";
		String toTest = "Escape me (Remix 2005)";
		
		assertThat(PhraseCalculator.phrase(phrase).calculateSimilarityTo(toTest), is(PhraseMatch.SAME_BEGIN));
	}
	
	@Test
	public void testSameBegin2() {
		String phrase = "Escape me - Tiesto";
		String toTest = "Escape me";
		
		assertThat(PhraseCalculator.phrase(phrase).calculateSimilarityTo(toTest), is(PhraseMatch.SAME_BEGIN));
	}
	
	@Test
	public void testSameBegin3() {
		String phrase = "Escape me - Tiesto";
		String toTest = "ESCAPE ME";
		
		assertThat(PhraseCalculator.phrase(phrase).calculateSimilarityTo(toTest), is(PhraseMatch.SAME_BEGIN));
	}
	
	@Test
	public void testSameBegin5() {
		String phrase = "RapRadio";
		String toTest = "Rap Radio California";
		
		assertThat(PhraseCalculator.phrase(phrase).calculateSimilarityTo(toTest), is(PhraseMatch.SAME_BEGIN));
	}
	
	@Test
	public void testSameBegin6() {
		String phrase = "Sound & Vision: Tribute to David Bowie";
		String toTest = "Sound and Vision";
		
		assertThat(PhraseCalculator.phrase(phrase).calculateSimilarityTo(toTest), is(PhraseMatch.SAME_BEGIN));
	}
	
	@Test
	public void testSameBegin7() {
		String phrase = "LAY IT ALL ON ME";
		String toTest = "Lay It All On Me (feat. Ed Sheeran) [Taurus Riley Remix]";
		
		assertThat(PhraseCalculator.phrase(phrase).calculateSimilarityTo(toTest), is(PhraseMatch.SAME_BEGIN));
	}
	
	@Test
	public void testSameBegin8() {
		String phrase = "Radio Pasion";
		String toTest = "Radio Pasion latina";
		
		assertThat(PhraseCalculator.phrase(phrase).calculateSimilarityTo(toTest), is(PhraseMatch.SAME_BEGIN));
	}
	
	//Isn't This a Lovely Day (To Be Caught in the Rain) -2] does not match Song=Isn’t This A Lovely Day
	@Test
	public void testSameBegin9() {
		String phrase = "Isn't This a Lovely Day (To Be Caught in the Rain) -2";
		String toTest = "Isn’t This A Lovely Day";
		
		assertThat(PhraseCalculator.phrase(phrase).calculateSimilarityTo(toTest), is(PhraseMatch.SAME_BEGIN));
	}
	
	@Test
	public void testContains() {
		String phrase = "Rhianna ft. Drake";
		String toTest = " DRAKE ";
		
		assertThat(PhraseCalculator.phrase(phrase).calculateSimilarityTo(toTest), is(PhraseMatch.CONTAINS));
	}
	
	@Test
	public void testContains2() {
		String phrase = "Nevermind";
		String toTest = "Nirvana  -  Nevermind";
		
		assertThat(PhraseCalculator.phrase(phrase).calculateSimilarityTo(toTest), is(PhraseMatch.CONTAINS));
	}
	
	@Test
	public void testContains3() {
		String phrase = "In bloom";
		String toTest = "Nirvana - In Bloom - Nevermind";
		
		assertThat(PhraseCalculator.phrase(phrase).calculateSimilarityTo(toTest), is(PhraseMatch.CONTAINS));
	}
	
	@Test
	public void testDifferent() {
		String phrase = "Casa grande";
		String toTest = "Big House";
		
		assertThat(PhraseCalculator.phrase(phrase).calculateSimilarityTo(toTest), is(PhraseMatch.DIFFERENT));
	}
	
	@Test
	public void testDifferentMatch_empty() {
		String phrase = " ";
		String toTest = "I've played";
		
		assertThat(PhraseCalculator.phrase(phrase).calculateSimilarityTo(toTest), is(PhraseMatch.DIFFERENT));
	}
	
	@Test
	public void testDifferentMatch_extraChars() {
		String phrase = "Let It Echo (Live)";
		String toTest = "Echoes";
		
		assertThat(PhraseCalculator.phrase(phrase).calculateSimilarityTo(toTest), is(PhraseMatch.DIFFERENT));
	}
	
	@Test
	public void testIsExact() {
		String phrase = "Shy Carter";
		String toTest = "Shy Carter";
		assertThat(PhraseCalculator.phrase(phrase).isExactTo(toTest), is(true));
	}
	
	@Test
	public void testhasSameBegin() {
		String phrase = "Shy Carter";
		String toTest = "Shy Carter and Aleon Craft";
		assertThat(PhraseCalculator.phrase(phrase).hasSameBeginAs(toTest), is(true));
	}
	
	@Test
	public void testhasSameBegin_2() {
		String phrase = "Shy Carter";
		String toTest = "Shy Carter";
		assertThat(PhraseCalculator.phrase(phrase).hasSameBeginAs(toTest), is(true));
	}
	
	@Test
	public void testIsSimilarTo() {
		String phrase = "Shy Carter";
		String toTest = "Shy Corter";
		assertThat(PhraseCalculator.phrase(phrase).isSimilarTo(toTest), is(true));
	}
	
	@Test
	public void testIsSimilarTo_2() {
		String phrase = "Shy Carter";
		String toTest = "Shy Carter";
		assertThat(PhraseCalculator.phrase(phrase).isSimilarTo(toTest), is(true));
	}
	
	@Test
	public void testAtLeastContains() {
		String phrase = "Aleon Craft Shy Carter ft. Will Smith";
		String toTest = "Shy Carter";
		assertThat(PhraseCalculator.phrase(phrase).atLeastContains(toTest), is(true));
	}
	
	@Test
	public void testAtLeastContains_1() {
		String phrase = "Shy Carter ft. Will Smith";
		String toTest = "Shy Carter";
		assertThat(PhraseCalculator.phrase(phrase).atLeastContains(toTest), is(true));
	}
	
	@Test
	public void testAtLeastContains_2() {
		String phrase = "Shy Carter";
		String toTest = "Shy Carter";
		assertThat(PhraseCalculator.phrase(phrase).atLeastContains(toTest), is(true));
	}
	
	@Test
	public void testIsDifferent() {
		String phrase = "Jay Z";
		String toTest = "Shy Carter";
		assertThat(PhraseCalculator.phrase(phrase).isDifferentTo(toTest), is(true));
	}
	
	

}
