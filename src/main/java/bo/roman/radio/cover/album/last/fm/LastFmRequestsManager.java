package bo.roman.radio.cover.album.last.fm;

import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bo.roman.radio.utilities.LoggerUtils;

public class LastFmRequestsManager {
	private static final Logger log = LoggerFactory.getLogger(LastFmRequestsManager.class);
	
	private static final int MAX_REQUESTS = 5;
	private static final long TIMEOUT_REQUESTS = 1000;
	
	private static AtomicInteger requestsCounter = new AtomicInteger(0);
	private static long startTimeRequests = System.currentTimeMillis();
	
	public static void prepare() {
		long currentTime = System.currentTimeMillis();
		if (currentTime - startTimeRequests > TIMEOUT_REQUESTS) {
			LoggerUtils.logDebug(log, () -> "Request manager timed out, cleaning it. Diff=" + (currentTime - startTimeRequests));
			// reset time and stored albums
			startTimeRequests = currentTime;
			requestsCounter.set(0);
		}
	}
	
	public static void count() {
		int c = requestsCounter.incrementAndGet();
		LoggerUtils.logDebug(log, () -> "Increasing counter:" + c);
	}
	
	/**
	 * Requests are enabled if the number of counted
	 * requests are less than MAX_REQUESTS
	 * @return
	 */
	public static boolean enableRequest() {
		boolean enabled = requestsCounter.get() < MAX_REQUESTS;
		if(log.isDebugEnabled() && !enabled) {
			log.debug("Request disabled because number of requests done:" + requestsCounter.get());
		}
			
		return enabled;
	}

}
