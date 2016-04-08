package bo.roman.radio.utilities;

import org.junit.Test;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.*;

public class SecretFilePropertiesTest {
	
	@Test
	public void testReadFacebookToken() {
		String key = "facebook.token";
		String val = SecretFileProperties.get(key);
		
		assertThat(val, not(isEmptyOrNullString()));
	}
	
	@Test
	public void testReadAmazonAwsAccessKeyId() {
		String key = "amazon.awsAccessKeyId";
		String val = SecretFileProperties.get(key);
		
		assertThat(val, not(isEmptyOrNullString()));
	}
	
	@Test
	public void testReadAmazonAssociateTag() {
		String key = "amazon.associateTag";
		String val = SecretFileProperties.get(key);
		
		assertThat(val, not(isEmptyOrNullString()));
	}
	
	@Test
	public void testReadAmazonAwsSecretKey() {
		String key = "amazon.awsSecretKey";
		String val = SecretFileProperties.get(key);
		
		assertThat(val, is(notNullValue()));
	}

}
