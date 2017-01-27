package bo.roman.radio.utilities;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SecretFileProperties {
	private final static Logger log = LoggerFactory.getLogger(SecretFileProperties.class);

	private final static String propFileName = "resources/pimped-radio.properties";

	private static final Properties properties = new Properties();

	static {
		try (FileInputStream fis = new FileInputStream(ResourceFinder.findFilePath(propFileName))) {
			properties.load(fis);
		} catch (IOException e) {
			log.error("The property file could not be loaded. Double check the Path.", e);
			throw new RuntimeException("There is no file to read secret tokens.");
		}
	}

	/**
	 * Returns the value of the token requested.
	 * 
	 * If the token does not exists, a runtime exception is thrown.
	 * 
	 * @param key
	 * @return
	 */
	public static String get(String key) {
		String val = properties.getProperty(key);
		if (!StringUtils.exists(val)) {
			throw new RuntimeException(String.format("Property [%s] does not exist.", key));
		}
		return val;
	}
}
