package bo.roman.radio.cover.album;

import java.io.IOException;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

import bo.roman.radio.cover.model.Album;
import bo.roman.radio.cover.model.CoverArt;
import bo.roman.radio.cover.model.mapping.CoverArtArchiveImages;
import bo.roman.radio.cover.model.mapping.CoverArtArchiveImages.Image;
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
	public Optional<CoverArt> findCoverUrl(Album album) throws IOException {
		if(album == null || !album.getMbid().isPresent()) {
			LoggerUtils.logDebug(LOG, () -> String.format("There is no Album or Album MBID to find the cover art [%s]", album));
			return Optional.empty();
		}
		// First get the link to send the request
		String requestLink = String.format(RELEASEREQUEST_TEMPLATE, album.getMbid().get());
		LoggerUtils.logDebug(LOG, () -> "Fetching album from=" + requestLink);
		
		// With the link, send a GET request to coverartarchive
		String jsonObject = HttpUtils.doGet(requestLink);

		// Parse the object and find the link of the front cover art
		CoverArtArchiveImages images = gsonParser.fromJson(jsonObject, CoverArtArchiveImages.class);
		
		if(images == null) {
			return Optional.empty();
		}
		
		Optional<CoverArt> coverArt = images.getImages().stream()
				.filter(Image::isFront)
				.map(i -> new CoverArt.Builder()
						.largeUri(i.getImage())
						.mediumUri(i.getThumbnails().get("large"))
						.smallUri(i.getThumbnails().get("small"))
						.build())
				.findFirst();
		
		return coverArt;
	}
	
	

}
