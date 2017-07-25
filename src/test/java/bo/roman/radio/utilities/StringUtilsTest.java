package bo.roman.radio.utilities;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import org.junit.Test;

public class StringUtilsTest {
	private final static String NL = StringUtils.LINE_SEPARATOR;
	
	@Test
	public void testPercentage() {
		assertThat(StringUtils.cleanIt("La X Estereo - 100% Pura Salsa"), is("La X Estereo - 100% Pura Salsa"));
	}
	
	@Test
	public void testReplaceAccents() {
		assertThat(StringUtils.cleanIt("Et ça sera sa moitié."), is("Et ca sera sa moitie."));
	}
	
	@Test
	public void testReplaceNewLines() {
		String toTest = "Mi casa" + NL + "es tu casa";
		assertThat(StringUtils.cleanIt(toTest), is("Mi casa es tu casa"));
	}
	
	@Test
	public void testReplaceHtmlEncoded() {
		assertThat(StringUtils.cleanIt("&lt;Fran&ccedil;ais&gt;"), is("<Francais>"));
	}
	
	@Test
	public void testReplaceUrlEncoded() {
		assertThat(StringUtils.cleanIt("You%20%26%20Me"), is("You & Me"));
	}
	
	@Test
	public void testReplaceCombinedCases() {
		String toTest = "Et ça sera sa moitié." + NL + "by Ces%C3%A1ria%20%C3%89vora" + NL + "&#35;song by Tiësto";
		String expected = "Et ca sera sa moitie. by Cesaria Evora #song by Tiesto";
		assertThat(StringUtils.cleanIt(toTest), is(expected));
	}
	
	@Test
	public void testRemoveFeaturing1() {
		String song = "aSong ft. anArtist";
		assertThat(StringUtils.removeFeatureInfo(song), is(equalTo("aSong")));
		
	}
	
	@Test
	public void testRemoveFeaturing2() {
		String artist = "anArtist feat. anArtis";
		assertThat(StringUtils.removeFeatureInfo(artist), is(equalTo("anArtist")));
	}
	
	@Test
	public void testRemoveFeaturing3() {
		String song = "feature FEAT. anArtist";
		assertThat(StringUtils.removeFeatureInfo(song), is(equalTo("feature")));
	}
	
	@Test
	public void testRemoveFeaturing4() {
		String artist = "ft. FEATURING anArtis";
		assertThat(StringUtils.removeFeatureInfo(artist), is(equalTo("ft.")));
	}
	
	@Test
	public void testRemoveFeaturing5() {
		String song = "aSong f. anotherArtist";
		assertThat(StringUtils.removeFeatureInfo(song), is(equalTo("aSong")));
	}
	
	@Test
	public void testRemoveFeaturing6() {
		String artist = "anArtist f/anotherArtis";
		assertThat(StringUtils.removeFeatureInfo(artist), is(equalTo("anArtist")));
	}
	
	@Test
	public void testRemoveExtraInfo() {
		String song = "aSong (extra info)";
		assertThat(StringUtils.removeBracketsInfo(song), is(equalTo("aSong")));
	}
	
	@Test
	public void testRemoveExtraInfo1() {
		String song = "sSong [test (xxx) edition] (just a test)";
		assertThat(StringUtils.removeBracketsInfo(song), is(equalTo("sSong")));
	}
	
	@Test
	public void testRemoveExtraInfo2() {
		String song = "aSong [extra info]";
		assertThat(StringUtils.removeBracketsInfo(song), is(equalTo("aSong")));
	}
	
	@Test
	public void testRemoveExtraInfo3() {
		String song = "sSong (test edition) ft. test";
		assertThat(StringUtils.removeBracketsInfo(song), is(equalTo("sSong")));
	}
	
	@Test
	public void testRemoveExtraInfo4() {
		String song = "sSong (((test edition))) ft. test";
		assertThat(StringUtils.removeBracketsInfo(song), is(equalTo("sSong")));
	}
	
	@Test
	public void testRemoveExtraInfo5() {
		String song = "sSong (((test edition) ft. test";
		assertThat(StringUtils.removeBracketsInfo(song), is(equalTo("sSong")));
	}
	
	@Test
	public void testRemoveExtraInfo6() {
		String song = "stereo 97(((test edition) ft. test";
		assertThat(StringUtils.removeBracketsInfo(song), is(equalTo("stereo 97")));
	}
	
	@Test
	public void testRemoveExtraInfoMultipleBraces() {
		String name = "((( WEFUNK Radio ))) . raw uncut funk . classic & underground hip-hop";
		assertThat(StringUtils.removeBracketsInfo(name), is("((( WEFUNK Radio ))) . raw uncut funk . classic & underground hip-hop"));
	}
	
	//( ( HITFORMULE ) ) 80s 90s 00s HOT TOP40 POP HITS nonstop 24/7

}
