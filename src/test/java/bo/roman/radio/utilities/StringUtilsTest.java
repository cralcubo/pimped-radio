package bo.roman.radio.utilities;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

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
	
	
	
	

}
