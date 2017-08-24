package bo.roman.radio.utilities;

import static bo.roman.radio.utilities.LoggerUtils.logDebug;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;;


public class HttpUtils {

	private static final Logger LOGGER = LoggerFactory.getLogger(HttpUtils.class);
	
	public static final String UTF_8 = StandardCharsets.UTF_8.name();
	
	private static final String USER_AGENT = SecretFileProperties.get("app.name") + "/"// 
											+ SecretFileProperties.get("app.version");
	
	private static final int CONNECTION_TIMEOUT_MS = 5 * 1000;
	
	private static final Pattern PARAMETERS_PATTERN = Pattern.compile("(?<==).+?(?=&\\w+=)");
	
	
	public static String doGet(String url) throws IOException {
		final String encodedUrl = encodeParameters(url);

		logDebug(LOGGER, () -> "Creating custom HttpClient with User-Agent:" + USER_AGENT);
		HttpClient httpClient = HttpClientBuilder.create().setUserAgent(USER_AGENT).build();
		// Timeout configurations
		RequestConfig requestConfig = RequestConfig.custom()
												   .setConnectionRequestTimeout(CONNECTION_TIMEOUT_MS) //
												   .setConnectTimeout(CONNECTION_TIMEOUT_MS) //
												   .setSocketTimeout(CONNECTION_TIMEOUT_MS) //
												   .build();
		HttpGet get = new HttpGet(encodedUrl);
		get.setConfig(requestConfig);
		logDebug(LOGGER, () -> String.format("Executing request: %s", get.getRequestLine()));

		String response = httpClient.execute(get, new MyResponseHandler());
		logDebug(LOGGER, () -> "Response:" + response);

		return response;
	}

	private static class MyResponseHandler implements ResponseHandler<String> {
		private static final Logger LOGGER = LoggerFactory.getLogger(MyResponseHandler.class);
		@Override
		public String handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
			int status = response.getStatusLine().getStatusCode();
			logDebug(LOGGER, () -> "Status from server="  + status);
			
			if(status >= 200 && status < 300) {
				HttpEntity entity = response.getEntity();
				return entity != null ? EntityUtils.toString(entity, StandardCharsets.UTF_8) : "";
			}
			else {
				throw new ClientProtocolException("Unexpected status=" + status);
			}
		}
	}
	
	public static String encodeParameters(String url) {
		Matcher m = PARAMETERS_PATTERN.matcher(url + "&end=");
		// UTF-8 encoding chartset
		while(m.find()) {
			try {
				String param = m.group();
				/* Percent-encode values according the RFC 3986. The built-in Java
			     * URLEncoder does not encode according to the RFC, so we make the
			     * extra replacements. */
				String encParam = URLEncoder.encode(param, UTF_8)
						.replace("+", "%20")
		                .replace("*", "%2A")
		                .replace("%7E", "~");
				LoggerUtils.logDebug(LOGGER, () -> String.format("Encoding param: [%s] to [%s]", param, encParam));
				url = url.replace(param, encParam);
			} catch (UnsupportedEncodingException e) {
				throw new RuntimeException(String.format("Totally unexpected, %s is supposed to be an accepted character encoding.", UTF_8), e);
			}
		}
		
		return url;
	}

}