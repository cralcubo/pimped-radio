package bo.roman.radio.utilities;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import org.junit.Test;

public class HttpUtilsTest {
	
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
				+ "?name=%27peace+%26+love%27"
				+ "&apos=%27I+don%27t+know+what%27s+the+prob%27"
				+ "&t=%27%23type+hashtag%27"
				+ "&val=123%7CABC"
				+ "&key=123%3DABC"
			    + "&q=%27casa%3Dgrande%27";
		
		assertThat(encodedUrl, is(equalTo(expectedUrl)));
	}

}
