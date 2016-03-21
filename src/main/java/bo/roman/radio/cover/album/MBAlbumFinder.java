package bo.roman.radio.cover.album;

import static bo.roman.radio.utilities.LoggerUtils.logDebug;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import org.musicbrainz.controller.Recording;
import org.musicbrainz.model.searchresult.RecordingResultWs2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bo.roman.radio.cover.model.Album;
import bo.roman.radio.utilities.StringUtils;

/**
 * Find the cover, sending a request to MusicBrainz based on the song played and
 * the name of the artist. With this information it will be retrieved first the
 * Album ID and with this ID another request will be send to coverartachive to
 * get the link of the cover art picture.
 * 
 * @author christian
 *
 */
public class MBAlbumFinder implements AlbumFindable {
	private final static Logger log = LoggerFactory.getLogger(MBAlbumFinder.class);

	private static final String QUERY_TEMPLATE = "\"%s\" AND artist:\"%s\""; // songName, artistName
	
	private Recording recordingController;
	private final int limit;
	private Set<Album> allAlbums;

	public MBAlbumFinder(int limit, Recording recording) {
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
		Set<Album> allAlbums = getAllAlbums(recordingResults);
		log.info("All albums found for {} - {} are in total={}", song, artist, allAlbums.size());
		logDebug(log, () -> allAlbums.toString());
    	
		// Collect in a list all the Releases that are the most relevant
		List<Album> relevantAlbums = albumsMap.entrySet().stream()
				.flatMap(es -> allAlbums.stream().filter(a -> a.getName().equals(es.getKey())))
				.limit(limit)
				.collect(Collectors.toList());
		log.info("All the relevant albums found for {} - {} are in total={}", song, artist, relevantAlbums.size());
		logDebug(log, () -> relevantAlbums.toString());
		return relevantAlbums;
	}
	
	private Set<Album> getAllAlbums(List<RecordingResultWs2> recordingResults) {
		if(recordingResults == null) {
			allAlbums = Collections.emptySet();
		}
		
		if (allAlbums == null) {
			allAlbums = recordingResults.stream()
					.map(RecordingResultWs2::getRecording)
					.flatMap(rec -> rec.getReleases().stream())
					.map(rel -> new Album.Builder()
							.name(rel.getTitle())
							.artistName(rel.getArtistCreditString())
							.status(rel.getStatus())
							.mbid(rel.getId())
							.build())
					.collect(Collectors.toSet());
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
		    		String artisName = a.getArtistName();
		    		if(StringUtils.exists(artisName)) {
		    			return artisName.toLowerCase().contains(artist.toLowerCase());
		    		}
		    		
		    		return true;
		    	})
		    	.collect(Collectors.groupingBy(Album::getName, Collectors.counting()))
		    	.entrySet().stream()
		    	.sorted((es1, es2) -> Long.compare(es2.getValue(), es1.getValue()))
		    	.collect(Collectors.toMap(Entry::getKey, Entry::getValue, (k,v) -> {throw new IllegalStateException("Duplicate key:" + k);}, LinkedHashMap::new));
		return releasesMap;
	}

	
}
