package bo.roman.radio.cover.station;

import org.junit.Assert;
import org.junit.Test;

import bo.roman.radio.utilities.RequestValidator.RequestStatus;

public class StationFinderRequestValidatorTest {
	
	@Test
	public void testHttpName() {
		String name = "http://167.114.64.181:9411/stream";
		Assert.assertEquals(RequestStatus.INVALID, StationFinderRequestValidator.validate(name));
	}
	
	@Test
	public void testEmptyName() {
		String name = null;
		Assert.assertEquals(RequestStatus.INVALID, StationFinderRequestValidator.validate(name));
	}
	
	@Test
	public void testRadioName() {
		String name = "Radio Paradise";
		Assert.assertEquals(RequestStatus.VALID, StationFinderRequestValidator.validate(name));
	}
	
	@Test
	public void testRepeatedName() {
		String name = "Radio Stereo 97";
		Assert.assertEquals(RequestStatus.VALID, StationFinderRequestValidator.validate(name));
		// Another request with the same station name
		Assert.assertEquals(RequestStatus.REPEATED, StationFinderRequestValidator.validate(name));
		
	}

}
