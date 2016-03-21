package bo.roman.radio.cover.station;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bo.roman.radio.utilities.LoggerUtils;

public class CacheLogoUtil {
	private static final Logger log = LoggerFactory.getLogger(CacheLogoUtil.class);
	
	private static final String CACHEFOLDER_PATH = System.getProperty("java.io.tmpdir");
	private static final String CACHELOGOPATH_TEMPL = "%s%s.jpg";
	
	/**
	 * Download the picture requested to the internet
	 * and save it in the OS temp folder.
	 * 
	 * The file will have the name of the radioStation. 
	 * @param radioName
	 * @param logoUrl
	 */
	public static boolean cacheRadioLogo(String radioName, String logoUrl) {
		Path cachedLogoPath = Paths.get(String.format(CACHELOGOPATH_TEMPL, CACHEFOLDER_PATH, radioName));
		
		if(isCached(radioName)) {
			log.info("The file [{}] is already cached.", cachedLogoPath);
			return true;
		}
		
		log.info("Caching the logo of radio ", radioName);
		try(InputStream in = new URL(logoUrl).openStream()){
			LoggerUtils.logDebug(log, () -> "Saving the file=" + cachedLogoPath);
		    Files.copy(in, cachedLogoPath);
		    return true;
		} catch (IOException e) {
			log.error("There was an error trying to save the file {}", cachedLogoPath, e);
			return false;
		}
	}
	
	/**
	 * Check if the logo of the 
	 * Radio Station to cache is 
	 * already saved in the temporary
	 * OS folder.
	 *  
	 * @param radioName
	 * @return
	 */
	public static boolean isCached(String radioName) {
		return Files.exists(getCachedLogoPath(radioName));
	}
	
	/**
	 * Gets the PATH of the file where the 
	 * cached logo is mean to be located..
	 * 
	 * @param radioName
	 * @return
	 */
	public static Path getCachedLogoPath(String radioName) {
		return Paths.get(String.format(CACHELOGOPATH_TEMPL, CACHEFOLDER_PATH, radioName));
	}

}
