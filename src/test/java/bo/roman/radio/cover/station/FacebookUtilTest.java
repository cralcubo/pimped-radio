package bo.roman.radio.cover.station;

import java.io.IOException;
import java.util.Optional;

import org.apache.http.client.ClientProtocolException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.core.classloader.annotations.SuppressStaticInitializationFor;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import bo.roman.radio.cover.station.facebook.FacebookUtil;
import bo.roman.radio.utilities.HttpUtils;
import bo.roman.radio.utilities.ReflectionUtils;
import bo.roman.radio.utilities.SecretFileProperties;

@RunWith(PowerMockRunner.class)
@PrepareForTest({HttpUtils.class, SecretFileProperties.class})
@SuppressStaticInitializationFor("bo.roman.radio.utilities.SecretFileProperties")
public class FacebookUtilTest {
	private final String GETQUERY_TEMPLATE;
	private static final String ACCESS_TOKEN = "aToken";
	
	public FacebookUtilTest() throws Exception {
		PowerMockito.mockStatic(SecretFileProperties.class);
		PowerMockito.when(SecretFileProperties.get("facebook.token")).thenReturn(ACCESS_TOKEN);
		GETQUERY_TEMPLATE = (String) ReflectionUtils.getPrivateConstant(new FacebookUtil(), "GETQUERY_TEMPLATE");
	}

	@Before
	public void setUp() {
		PowerMockito.mockStatic(HttpUtils.class);
	}

	@Test
	public void testDoGet() throws IOException {
		String queryType = "search?q=radio&type=page";
		String requestLink = String.format(GETQUERY_TEMPLATE, queryType, ACCESS_TOKEN);
		String jsonObject = "{\"key\":\"value\"}";
		// Prepare
		PowerMockito.when(HttpUtils.doGet(requestLink)).thenReturn(jsonObject);

		// Run test
		Optional<String> response = FacebookUtil.doGet(queryType);

		// Assert
		assertThat(response.isPresent(), is(true));
		assertThat(response.get(), is(equalTo(jsonObject)));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testDoGet_Exception() throws IOException {
		String queryType = "search?q=radio&type=page";
		String requestLink = String.format(GETQUERY_TEMPLATE, queryType, ACCESS_TOKEN);
		// Prepare
		PowerMockito.when(HttpUtils.doGet(requestLink)).thenThrow(ClientProtocolException.class);

		// Run test
		Optional<String> response = FacebookUtil.doGet(queryType);

		// Assert
		assertThat(response.isPresent(), is(false));
	}
	
	@Test
	public void doSearch() throws IOException {
		String searchQuery = "q='radio passion'&type=page&fields=id";
		String requestLink = String.format("https://graph.facebook.com/search?%s&access_token=%s", searchQuery, ACCESS_TOKEN);
		String jsonObject = "{\"key\":\"value\"}";
		// Prepare
		PowerMockito.when(HttpUtils.doGet(requestLink)).thenReturn(jsonObject);

		// Run test
		Optional<String> response = FacebookUtil.doSearch(searchQuery );
		
		// Assert
		assertThat(response.isPresent(), is(true));
		assertThat(response.get(), is(equalTo(jsonObject)));
	}

}
