package bo.roman.radio.cover;

import static bo.roman.radio.utilities.LoggerUtils.logDebug;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.musicbrainz.controller.Recording;
import org.musicbrainz.model.searchresult.RecordingResultWs2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bo.roman.radio.cover.model.Album;

/**
 * Find the cover, sending a request to MusicBrainz based on the song played and
 * the name of the artist. With this information it will be retrieved first the
 * Album ID and with this ID another request will be send to coverartachive to
 * get the link of the cover art picture.
 * 
 * @author christian
 *
 */
public class MusicBrainzFinder implements AlbumFindable {
	private final static Logger log = LoggerFactory.getLogger(MusicBrainzFinder.class);

	private static final String QUERY_TEMPLATE = "\"%s\" AND artist:\"%s\""; // songName, artistName
	
	private Recording recordingController;
	private final int limit;
	private List<Album> allAlbums;

	public MusicBrainzFinder(int limit, Recording recording) {
		recordingController = recording;
		this.limit = limit;
	}
	
	@Override
	public List<Album> findAlbums(String song, String artist) {
		// First generate the query to find an album
		String query = String.format(QUERY_TEMPLATE, song, artist);
		logDebug(log, () -> "Query generated=" + query);

		// With the query send a request to MusicBrainz to get the albums
		recordingController.search(query);
		List<RecordingResultWs2> recordingResults = recordingController.getFullSearchResultList();
		
		// Sort all the albums from the one that is repeated the most to the least
		Map<String, Long> albumsMap = getSortedRecordings(recordingResults, artist);
    	
    	// Get all Albums
		List<Album> allAlbums = getAllAlbums(recordingResults);
		logDebug(log, () -> "All albums found=" + allAlbums.size());
    	
		// Collect in a list all the Releases that are the most relevant
		List<Album> relevantAlbums = albumsMap.entrySet().stream()
				.flatMap(es -> allAlbums.stream().filter(a -> a.getTitle().equals(es.getKey())))
				.limit(limit)
				.collect(Collectors.toList());
		logDebug(log, () -> "Relevant albums returned=" + relevantAlbums);
		return relevantAlbums;
	}
	
	private List<Album> getAllAlbums(List<RecordingResultWs2> recordingResults) {
		if(recordingResults == null) {
			allAlbums = Collections.emptyList();
		}
		
		if (allAlbums == null) {
			allAlbums = recordingResults.stream()
					.map(RecordingResultWs2::getRecording)
					.flatMap(rec -> rec.getReleases().stream())
					.map(rel -> new Album.Builder()
							.title(rel.getTitle())
							.credits(rel.getArtistCreditString())
							.status(rel.getStatus())
							.mbid(rel.getId())
							.build())
					.collect(Collectors.toList());
		}
		
		return allAlbums;
	}

	/**
	 * - From the RecordingResultWs2 List, get all the releases available.
	 * - Filter the Releases that are not Official.
	 * - Filter the Releases that have a Credits with the name of the artist.
	 * - Group the Releases title by name and repetitions.
	 * - Sort from the most repeated to the least one.
	 * 
	 * @param recordingResults
	 * @return
	 */
	private Map<String, Long> getSortedRecordings(List<RecordingResultWs2> recordingResults, String artist) {
		if(recordingResults == null) {
			return Collections.emptyMap();
		}
		
		Map<String, Long> releasesMap = getAllAlbums(recordingResults).stream() 
		    	.filter(a -> "Official".equalsIgnoreCase(a.getStatus()))
		    	.filter(a -> {
		    		String artistCredit = a.getCredits();
		    		if(artistCredit != null && !artistCredit.isEmpty())
		    		{
		    			return artistCredit.toLowerCase().contains(artist.toLowerCase());
		    		}
		    		return true;
		    	})
		    	.collect(Collectors.groupingBy(Album::getTitle, Collectors.counting()))
		    	.entrySet().stream()
		    	.sorted((es1, es2) -> Long.compare(es2.getValue(), es1.getValue()))
		    	.collect(Collectors.toMap(Entry::getKey, Entry::getValue, (k,v) -> {throw new IllegalStateException("Duplicate key:" + k);}, LinkedHashMap::new));
		return releasesMap;
	}

	
}
