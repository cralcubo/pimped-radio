package bo.roman.radio.utilities;

import static bo.roman.radio.utilities.LoggerUtility.logDebug;

import java.io.IOException;

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

	public static String doGet(String url) throws IOException {
		CloseableHttpClient httpClient = HttpClients.createDefault();
		try {
			logDebug(LOGGER, () -> "Sending request to=" + url);
			
			HttpGet get = new HttpGet(url);
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
				return entity != null ? EntityUtils.toString(entity) : "";
			}
			else {
				throw new ClientProtocolException("Unexpected status=" + status);
			}
		}
	}

}