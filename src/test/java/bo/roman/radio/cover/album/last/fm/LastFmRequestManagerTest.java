package bo.roman.radio.cover.album.last.fm;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bo.roman.radio.utilities.ReflectionUtils;

public class LastFmRequestManagerTest {
	private static final Logger log = LoggerFactory.getLogger(LastFmRequestManagerTest.class);
	private Long timeOut;
	
	public LastFmRequestManagerTest() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		timeOut = (Long) ReflectionUtils.getPrivateConstant(new LastFmRequestsManager(), "TIMEOUT_REQUESTS");
	}
	
	@After
	public void tearDown() throws Exception {
		// Wait one second to reset the Request Counter
		Thread.sleep(timeOut + 1000);
		LastFmRequestsManager.prepare();
	}
	
	@Test
	public void testSixRequests() {
		log.info("testSixRequests");
		// Assert
		Assert.assertFalse("Requests should be disabled after 6 requests", isRequestEnabled(6));
	}
	
	@Test
	public void testTwoRequests() {
		log.info("testTwoRequests");
		// Assert
		Assert.assertTrue("Requests should be enabled after 2 requests", isRequestEnabled(2));
	}
	
	private boolean isRequestEnabled(int numReq) {
		LastFmRequestsManager.prepare();
		for(int i = 0 ; i < numReq; i++) {
			LastFmRequestsManager.count();
		}
		
		return LastFmRequestsManager.enableRequest(); 
	}

}
