package bo.roman.radio.cover;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static bo.roman.radio.utilities.LoggerUtility.logDebug;

public class MusicBrainzAlbumFinder {
	private final static Logger log = LoggerFactory.getLogger(MusicBrainzAlbumFinder.class);
	
	private static final String QUERY_TEMPLATE = "\"%s\" AND artist:\"%s\""; // songName, artistName

	public List<String> getAlbumIds(String song, String artist){
		// First generate the query to find an album
		String query = String.format(QUERY_TEMPLATE, song, artist);
		logDebug(log, () -> String.format("Query generated >>%s<<", query));
		return null;
	}


}
