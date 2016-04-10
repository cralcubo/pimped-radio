package bo.roman.radio.utilities;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SecretFileProperties {
	private final static Logger log = LoggerFactory.getLogger(SecretFileProperties.class);
	
	private static final String secretFilePath = "secure/token.secret";
	
	private static final Properties properties = new Properties();
	static {
		try {
			LoggerUtils.logDebug(log, () -> "Reading properties from file=" + secretFilePath);
			properties.load(new FileInputStream(secretFilePath));
		} catch (IOException e) {
			log.error("The property file in {} could not be loaded. Double check the Path.", secretFilePath, e);
			throw new RuntimeException("There is no file to read secret tokens.");
		}
	}
	
	/** 
	 * Returns the value of the token 
	 * requested.
	 * 
	 * If the token does not exists, a 
	 * runtime exception is thrown.
	 * 
	 * @param key
	 * @return
	 */
	public static String get(String key) {
		String val = properties.getProperty(key);
		if(!StringUtils.exists(val)) {
			throw new RuntimeException(String.format("To fetch Amazon information %s is needed.", key));
		}
		return val;
	}
}
