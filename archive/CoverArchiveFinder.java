package bo.roman.radio.cover.album;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bo.roman.radio.cover.model.Album;
import bo.roman.radio.cover.model.CoverArt;
import bo.roman.radio.utilities.ImageUtil;
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
public class CoverArchiveFinder implements CoverArtFindable {
	private final static Logger log = LoggerFactory.getLogger(CoverArchiveFinder.class);
	
	private static final String LARGECOVER_TMPL = "http://coverartarchive.org/release/%s/front";
	private static final String MEDIUMCOVER_TMPL = LARGECOVER_TMPL + "-500";
	private static final String SMALLCOVER_TMPL = LARGECOVER_TMPL + "-250";
	
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
	public Optional<CoverArt> findCoverArt(Album album) throws IOException {
		
		if(album == null || !album.getMbid().isPresent()) {
			LoggerUtils.logDebug(log, () -> String.format("There is no Album or Album MBID to find the cover art [%s]", album));
			return Optional.empty();
		}
		
		// First get the link to send the request
		String mbid = album.getMbid().get();
		String mediumCoverLink = String.format(MEDIUMCOVER_TMPL, mbid);
		LoggerUtils.logDebug(log, () -> "Fetching CoverArt from=" + mediumCoverLink);
		boolean isImageBig = false;
		try {
			 isImageBig = ImageUtil.isBigEnough(mediumCoverLink);
		} catch (URISyntaxException e) {
			log.error("Malformed link to retrieve CoverArt. This is unexpected because the URL is controlled by this class. Wrong URL is [{}]", mediumCoverLink, e);
			throw new RuntimeException(String.format("Malformed link to retrieve CoverArt. This is unexpected because the URL is controlled by this class. Wrong URL is [%s]", mediumCoverLink), e);
		}
		
		if(!isImageBig) {
			log.info("CoverArt for [{} - {}] is not big enough.", album.getName(), mbid);
			return Optional.empty();
		}
		
		String largeCoverLink = String.format(LARGECOVER_TMPL, mbid);
		String smallCoverLink = String.format(SMALLCOVER_TMPL, mbid);
		CoverArt coverArt = new CoverArt.Builder()
				.largeUri(largeCoverLink)
				.mediumUri(mediumCoverLink)
				.smallUri(smallCoverLink)
				.build();
		
		return Optional.of(coverArt);
	}

}
