package bo.roman.radio.utilities;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.core.classloader.annotations.SuppressStaticInitializationFor;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest(SecretFileProperties.class)
@SuppressStaticInitializationFor("bo.roman.radio.utilities.SecretFileProperties")
public class HttpUtilsTest {
	
	@Before
	public void setUp() {
		PowerMockito.mockStatic(SecretFileProperties.class);
		PowerMockito.when(SecretFileProperties.get("app.name")).thenReturn("aName");
		PowerMockito.when(SecretFileProperties.get("app.version")).thenReturn("aVersion");
		PowerMockito.when(SecretFileProperties.get("app.contact")).thenReturn("aContact");
	}
	
	@Test
	public void testParametersEncoding() {
		String url = "http://www.website.com/search"
				+ "?name='peace & love'"
				+ "&apos='I don't know what's the prob'"
				+ "&t='#type hashtag'"
				+ "&val=123|ABC"
				+ "&key=123=ABC"
				+ "&q='casa=grande'";
		
		String encodedUrl = HttpUtils.encodeParameters(url);
		String expectedUrl = "http://www.website.com/search"
				+ "?name=%27peace%20%26%20love%27"
				+ "&apos=%27I%20don%27t%20know%20what%27s%20the%20prob%27"
				+ "&t=%27%23type%20hashtag%27"
				+ "&val=123%7CABC"
				+ "&key=123%3DABC"
			    + "&q=%27casa%3Dgrande%27";
		
		assertThat(encodedUrl, is(equalTo(expectedUrl)));
	}

}
