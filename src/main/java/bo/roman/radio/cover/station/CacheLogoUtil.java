package bo.roman.radio.cover.station;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bo.roman.radio.utilities.LoggerUtils;
import bo.roman.radio.utilities.StringUtils;
import net.sf.jmimemagic.Magic;
import net.sf.jmimemagic.MagicException;
import net.sf.jmimemagic.MagicMatchNotFoundException;
import net.sf.jmimemagic.MagicParseException;

public class CacheLogoUtil {
	private static final Logger log = LoggerFactory.getLogger(CacheLogoUtil.class);
	
	private static final String CACHEFOLDER_PATH = System.getProperty("java.io.tmpdir");
	private static final String CACHELOGOPATH_TEMPL = "%s%s.jpg";

	private static final float MINIMAGE_SIZE = 1000;
	
	/**
	 * Download the picture requested to the internet
	 * and save it in the OS temp folder.
	 * 
	 * The file will have the name of the radioStation. 
	 * @param radioName
	 * @param logoUrl
	 */
	public static boolean cacheRadioLogo(String radioName, Optional<URI> optLogoUri) {
		if(!optLogoUri.isPresent()) {
			log.info("There is no file URI to download.");
			return false;
		}
		
		Path toCacheLogoPath = Paths.get(String.format(CACHELOGOPATH_TEMPL, CACHEFOLDER_PATH, radioName));
		URI logoUri = optLogoUri.get();
		if(isCached(radioName)) {
			log.info("The file [{}] is already cached.", toCacheLogoPath);
			return true;
		}
		
		log.info("Caching the logo of radio [{}] downloaded from [{}]", radioName, optLogoUri.get());
		try(InputStream in = logoUri.toURL().openStream()){
			// Copy the content of the InputStream in the file to cache
		    Files.copy(in, toCacheLogoPath);
		    File image = toCacheLogoPath.toFile();
		    
			if(!isValidImage(image)) {
				log.warn("The file [{}] is not a valid image. It won't be cached.", image);
				// The image is not valid, then delete it
				Files.delete(toCacheLogoPath);
				return false;
		    }
			
		    LoggerUtils.logDebug(log, () -> "Caching the file: " + toCacheLogoPath);
		    return true;
		} catch (IOException e) {
			log.error("There was an error trying to download the image {}", optLogoUri.get(), e);
			return false;
		}
	}
	
	private static boolean isValidImage(File image) {
		long size = image.length();
		String mimeType;
		try {
			mimeType = StringUtils.nullIsEmpty(Magic.getMagicMatch(image, false).getMimeType());
			LoggerUtils.logDebug(log, () ->  String.format("Analazing the image [%s] with size [%.2f]KB of mime-type [%s]", image, size/1024f, mimeType));
			return size >= MINIMAGE_SIZE && mimeType.startsWith("image/");
		} catch (MagicParseException | MagicMatchNotFoundException | MagicException e) {
			log.error("Error finding the mime type of the file [{}]", image, e);
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
