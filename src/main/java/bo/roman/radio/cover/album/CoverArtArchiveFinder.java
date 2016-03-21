package bo.roman.radio.cover.album;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

import bo.roman.radio.cover.model.Images;
import bo.roman.radio.cover.model.Images.Image;
import bo.roman.radio.utilities.HttpUtils;
import bo.roman.radio.utilities.LoggerUtils;

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
public class CoverArtArchiveFinder implements CoverArtFindable {
	private final static Logger LOG = LoggerFactory.getLogger(CoverArtArchiveFinder.class);
	
	private static final String RELEASEREQUEST_TEMPLATE = "http://coverartarchive.org/release/%s";
	
	private final Gson gsonParser;
	
	public CoverArtArchiveFinder() {
		gsonParser = new Gson();
	}
	
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
	public Optional<String> findCoverUrl(String mbid) throws IOException {
		// First get the link to send the request
		String requestLink = String.format(RELEASEREQUEST_TEMPLATE, mbid);
		LoggerUtils.logDebug(LOG, () -> "Fetching album from=" + requestLink);
		
		// With the link, send a GET request to coverartarchive
		String jsonObject = HttpUtils.doGet(requestLink);

		// Parse the object and find the link of the front cover art
		Images images = gsonParser.fromJson(jsonObject, Images.class);
		
		if(images == null) {
			return Optional.empty();
		}
		
		List<String> links = images.getImages().stream()
				.filter(Image::isFront)
				.map(Image::getImage)
				.collect(Collectors.toList());
		
		if(links.isEmpty()) {
			return Optional.empty();
		} 
		
		String link = links.get(0);
		LOG.info("Linked to cover art found [{}]", link);
		return Optional.of(link);
	}
	
	

}
