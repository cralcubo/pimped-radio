package bo.roman.radio.cover;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

import bo.roman.radio.cover.model.Images;
import bo.roman.radio.cover.model.Images.Image;
import bo.roman.radio.utilities.HttpUtils;
import bo.roman.radio.utilities.LoggerUtility;

/**
 * This class will do a request to:
 * 
 * <code>covertartarchive.com<code>
 * 
 * To request an album art from this website
 * a REST request with the mbid of an album 
 * needs to be sent.
 *  
 * @author christian
 *
 */
public class CoverArtFinder {
	private final static Logger LOG = LoggerFactory.getLogger(CoverArtFinder.class);
	
	private static final String RELEASEREQUEST_TEMPLATE = "http://coverartarchive.org/release/%s";
	
	/**
	 * Send the following request to coverartarchive:
	 * 
	 * http://coverartarchive.org/release/<MBID>
	 * 
	 * where MBID is the MusicBrains ID of the album
	 * to be requested.
	 * @param album
	 * @return
	 * @throws IOException 
	 */
	public String fetchAlbumLink(String mbid) throws IOException {
		// First get the link to send the request
		String requestLink = String.format(RELEASEREQUEST_TEMPLATE, mbid);
		LoggerUtility.logDebug(LOG, () -> "Fetching album from=" + requestLink);
		
		// With the link, send a GET request to coverartarchive
		String jsonObject = HttpUtils.doGet(requestLink);

		// Parse the object and find the link of the front cover art
		Gson gson = new Gson();
		Images images = gson.fromJson(jsonObject, Images.class);
		String link = images.getImages().stream()
				.filter(Image::isFront)
				.map(Image::getImage)
				.reduce("", (i1, i2) -> i1.concat(i2));
		
		LOG.info("Linked to cover art found [{}]", link);
		
		return link;
	}
	
	

}
