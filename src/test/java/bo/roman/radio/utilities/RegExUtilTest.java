package bo.roman.radio.utilities;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static bo.roman.radio.utilities.RegExUtil.phrase;

public class RegExUtilTest {
	
	@Test
	public void testContainsWord() {
		assertThat("La vida es Bella", phrase("La vida es Bella").containsIgnoreCase("vida"), is(true));
		assertThat("TI Ft. Justin Timberlake", phrase("TI Ft. Justin Timberlake").containsIgnoreCase("justin timberlake"), is(true));
		assertThat("GUnit compilation", phrase("GUnit compilation").containsIgnoreCase("gunit compilation"), is(true));
		assertThat("Exitos 2010", phrase("Exitos 2010").containsIgnoreCase("2010"), is(true));
		
		assertThat("La vida es Bella", phrase("La vida es Bella").containsIgnoreCase("vidax"), is(false));
		assertThat("TI Ft. Justin Timberlake", phrase("TI Ft. Justin Timberlake").containsIgnoreCase("justin bieber"), is(false));
		assertThat("GUnit compilation", phrase("GUnit compilation").containsIgnoreCase("GUnit compiled"), is(false));
	}
	
	@Test
	public void testBeginsWithIC() {
		assertThat("Nirvana (Remastered)", phrase("Nirvana (Remastered)").beginsWithIgnoreCase("Nirvana"), is(true));
		assertThat("Nirvana", phrase("Nirvana ").beginsWithIgnoreCase("nirvana"), is(true));
		assertThat("Radio pasion", phrase("RADIO PASION ECUADOR").beginsWithIgnoreCase("Radio pasion"), is(true));
		assertThat("Radio pasión", phrase("RADIO PASIÓN ECUADOR").beginsWithIgnoreCase("Radio pasión ECUADOR"), is(true));
		
		assertThat("Last Nirvana Album", phrase("Last Nirvana Album").beginsWithIgnoreCase("Nirvana"), is(false));
	}

}
