package bo.roman.radio.cover.album;

import static bo.roman.radio.utilities.LoggerUtils.logDebug;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.musicbrainz.controller.Recording;
import org.musicbrainz.model.searchresult.RecordingResultWs2;
import org.musicbrainz.webservice.impl.HttpClientWebServiceWs2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bo.roman.radio.cover.model.Album;
import bo.roman.radio.utilities.SecretFileProperties;
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
	private static final String SAMENAMEREGEX_TMPL = "(?i)^\\b%s\\b($|\\s+\\(.+\\)$)";

	private static final int TIMEOUT_SECS = 3;
	private static final String OFFICIAL_RELEASE = "Official";
	private static final long HIGHPRIORITY = 1000_000L;

	private static final String APPLICATION_NAME = SecretFileProperties.get("app.name");
	private static final String APPLICATION_VERSION = SecretFileProperties.get("app.version");
	private static final String APPLICATION_CONTACT = SecretFileProperties.get("app.contact");
	
	private final int limit;
	
	public MBAlbumFinder(int limit) {
		this.limit = limit;
	}
	
	@Override
	public List<Album> findAlbums(String song, String artist) {
		if(!StringUtils.exists(song) || !StringUtils.exists(artist)) {
			log.info("There is no Song name and/or Artist to find an Album.");
			return Collections.emptyList();
		}
		
		CompletableFuture<Set<Album>> allFutureAlbums = CompletableFuture.supplyAsync(() -> findAllAlbums(song, artist));
		final Set<Album> allAlbums;
		try {
			allAlbums = allFutureAlbums.get(TIMEOUT_SECS, TimeUnit.SECONDS);
		} catch (InterruptedException | ExecutionException | TimeoutException e) {
			log.error("Error finding Albums in MusicBrains.", e);
			return Collections.emptyList();
		}
		
		// Find all Albums
		log.info("All albums found in MusicBrainz for {} - {} are in total={}", song, artist, allAlbums.size());
		logDebug(log, () -> allAlbums.toString());
		
		// Sort all the albums from the one that is repeated the most to the least
		Map<String, Long> albumsMap = getSortedAlbums(allAlbums, artist, song);
    	
		// Collect in a list all the Releases that are the most relevant
		List<Album> relevantAlbums = albumsMap.entrySet().stream()
				.flatMap(es -> allAlbums.stream()
										.filter(a -> isRelevantAlbum(a, artist))
										.filter(a -> a.getName().equals(es.getKey())))
				.limit(limit)
				.collect(Collectors.toList());
		log.info("All the relevant albums found in MusicBrainz for {} - {} are in total={}", song, artist, relevantAlbums.size());
		logDebug(log, () -> relevantAlbums.toString());
		return relevantAlbums;
	}
	
	private Set<Album> findAllAlbums(String songName, String artistName) {
		Recording recordingController = RecordingFactory.createRecording();
		logDebug(log, () -> String.format("Generating MB WSClient for [name=%s, version=%s, contact=%s]", APPLICATION_NAME, APPLICATION_VERSION, APPLICATION_CONTACT));
		recordingController.setQueryWs(new HttpClientWebServiceWs2(APPLICATION_NAME, APPLICATION_VERSION, APPLICATION_CONTACT));
		
		// First generate the query to find an album
		String query = String.format(QUERY_TEMPLATE, songName, artistName);
		logDebug(log, () -> "Query generated=" + query);

		// With the query send a request to MusicBrainz to get the albums
		recordingController.search(query);
		List<RecordingResultWs2> recordingResults = recordingController.getFullSearchResultList();
		
		if(recordingResults == null) {
			log.info("No RecordingResultWs2 found in MusicBrainz");
			return Collections.emptySet();
		}
		
		return recordingResults.stream()
					.map(RecordingResultWs2::getRecording)
					.flatMap(rec -> rec.getReleases().stream())
					.map(rel -> new Album.Builder()
							.name(rel.getTitle())
							.songName(songName)
							.artistName(StringUtils.exists(rel.getArtistCreditString()) ? rel.getArtistCreditString().trim() : artistName)
							.status(rel.getStatus())
							.mbid(rel.getId())
							.build())
					.collect(Collectors.toSet());
	}

	/**
	 * 
	 * - From the all the albums found, get all the releases available.
	 * - Filter the Releases that are not Official.
	 * - Filter the Releases that have a Credits with the name of the artist.
	 * - Group the Releases title by the number of times the same name is repeated.
	 * - Sort from the most repeated to the least one.
	 * - If there is found a name of an album equal to the name of the song that is playing, put it on
	 *   first priority. 
	 * 
	 * @param recordingResults
	 * @return
	 */
	private Map<String, Long> getSortedAlbums(Set<Album> allAlbums, String artist, String songName) {
		if(allAlbums == null || allAlbums.isEmpty()) {
			return Collections.emptyMap();
		}
		
		Map<String, Long> releasesMap = allAlbums.stream() 
		    	.collect(Collectors.groupingBy(Album::getName, Collectors.counting()))
		    	.entrySet().stream()
		    	.peek(es -> {
		    		Matcher m = Pattern.compile(String.format(SAMENAMEREGEX_TMPL, songName)).matcher(es.getKey());
		    		if(m.find()) {
		    			es.setValue(HIGHPRIORITY);
		    		}
		    	})
		    	.sorted((es1, es2) -> Long.compare(es2.getValue(), es1.getValue()))
		    	.collect(Collectors.toMap(Entry::getKey, Entry::getValue, (k,v) -> {throw new IllegalStateException("Duplicate key:" + k);}, LinkedHashMap::new));
		return releasesMap;
	}
	
	/**
	 * An album is considered relevant if:
	 * - The album is Official.
	 * - The artist is the same artist used to search for the album. This to filter out albums that have various artists in it.
	 * @param a
	 * @param artist
	 * @return
	 */
	private boolean isRelevantAlbum(Album a, String artist) {
		boolean isOfficial = OFFICIAL_RELEASE.equalsIgnoreCase(a.getStatus());
		
		boolean isExpectedArtist = true;
		String artisName = a.getArtistName();
		if(StringUtils.exists(artisName)) {
			isExpectedArtist = artisName.toLowerCase().contains(artist.toLowerCase());
		}
		
		return isOfficial && isExpectedArtist;
	}
	
	/**
	 * Helper factory that will create a new
	 * instance of Recording.
	 * This to mock the class Recording and 
	 * Unit test it.
	 * 
	 * @author christian
	 *
	 */
	public static class RecordingFactory {
		public static Recording createRecording() {
			return new Recording();
		}
	}
}
