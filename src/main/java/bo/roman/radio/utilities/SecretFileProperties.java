package bo.roman.radio.utilities;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SecretFileProperties {
	private final static Logger log = LoggerFactory.getLogger(SecretFileProperties.class);

	private final static String propFileName = "/pimped-radio.properties";

	private static final Properties properties = new Properties();

	static {
		try {
			Path secretFilePath = Paths.get(SecretFileProperties.class.getResource(propFileName).toURI());
			LoggerUtils.logDebug(log, () -> "Reading properties from file=" + secretFilePath);
			try(FileInputStream fis = new FileInputStream(secretFilePath.toFile())) {
				properties.load(fis);
			}
		} catch (IOException | URISyntaxException e) {
			log.error("The property file could not be loaded. Double check the Path.", e);
			throw new RuntimeException("There is no file to read secret tokens.");
		} catch (Exception e) {
			log.error(
					"There was an error finding the property files [{}]. Check in the pom.xml file to find where this file is supposed to be.",
					propFileName, e);
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
