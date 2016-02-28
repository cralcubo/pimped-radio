package bo.roman.radio.cover;

import java.util.List;

import org.musicbrainz.controller.Recording;
import org.musicbrainz.model.searchresult.RecordingResultWs2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static bo.roman.radio.utilities.LoggerUtility.logDebug;

public class MusicBrainzAlbumFinder {
	private final static Logger log = LoggerFactory.getLogger(MusicBrainzAlbumFinder.class);
	
	private static final String QUERY_TEMPLATE = "\"%s\" AND artist:\"%s\""; // songName, artistName
	private Recording recordingController;
	
	public MusicBrainzAlbumFinder() {
		recordingController = new Recording();
	}
	

	public List<String> getAlbumIds(String song, String artist){
		// First generate the query to find an album
		String query = String.format(QUERY_TEMPLATE, song, artist);
		logDebug(log, () -> "Query generated=" + query);
		
		// With the query send a request to MusicBrainz to get the albums
		recordingController.search(query);
		List<RecordingResultWs2> results = recordingController.getFullSearchResultList();
		
		
		return null;
	}


}
