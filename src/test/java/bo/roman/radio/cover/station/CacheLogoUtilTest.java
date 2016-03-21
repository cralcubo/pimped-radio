package bo.roman.radio.cover.station;

import org.junit.Test;

public class CacheLogoUtilTest {
	
	private static final String RADIOPARADISE_JPG = "src/test/resources/radio-paradise.jpg";

	@Test
	public void testRadioLogo() {
		String radioName = "aTestRadio";
		
		
		String logoUrl = "file://";
		CacheLogoUtil.cacheRadioLogo(radioName, logoUrl);
	}

}
