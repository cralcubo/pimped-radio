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
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;;


public class HttpUtils {

	private static final Logger LOGGER = LoggerFactory.getLogger(HttpUtils.class);
	
	public static final String UTF_8 = StandardCharsets.UTF_8.name();
	
	private static final String USER_AGENT = SecretFileProperties.get("app.name") + "/"// 
											+ SecretFileProperties.get("app.version");
	
	private static final String PARAMETERS_REGEX = "(?<==)('.+?'|[^&]+)(?=&)";

	public static String doGet(String url) throws IOException {
		final String encodedUrl = encodeParameters(url);

		logDebug(LOGGER, () -> "Creating custom HttpClient with User-Agent:" + USER_AGENT);
		CloseableHttpClient httpClient = HttpClients.custom().setUserAgent(USER_AGENT).build();
		try {
			logDebug(LOGGER, () -> "Sending request to=" + encodedUrl);

			HttpGet get = new HttpGet(encodedUrl);
			logDebug(LOGGER, () -> "Excecuting request" + get.getRequestLine());

			String response = httpClient.execute(get, new MyResponseHandler());
			logDebug(LOGGER, () -> "Response:" + response);

			return response;
		} finally {
			httpClient.close();
		}
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
		Matcher m = Pattern.compile(PARAMETERS_REGEX).matcher(url + '&');
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