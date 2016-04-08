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
		}
	}
	
	public static String get(String key) {
		return properties.getProperty(key);
	}

}
