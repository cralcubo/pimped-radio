package bo.roman.radio.cover.album.last.fm;

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bo.roman.radio.cover.album.AlbumFindable;
import bo.roman.radio.cover.model.Album;
import bo.roman.radio.cover.model.CoverArt;
import bo.roman.radio.utilities.HttpUtils;
import bo.roman.radio.utilities.LoggerUtils;
import bo.roman.radio.utilities.PhraseCalculator;
import bo.roman.radio.utilities.PhraseCalculator.PhraseMatch;
import bo.roman.radio.utilities.SecretFileProperties;
import bo.roman.radio.utilities.StringUtils;

public class LastFmAlbumFinder implements AlbumFindable {
	private static final Logger log = LoggerFactory.getLogger(LastFmAlbumFinder.class);
	private List<Album> cachedAlbums = Collections.emptyList();
	
	private static final String LASTFM_KEY = SecretFileProperties.get("lastfm.apiKey");
	private static final String TRACKINFO_QUERY = "http://ws.audioscrobbler.com/2.0/"//
													+ "?method=track.getInfo"//
													+ "&track=%s"//
													+ "&artist=%s"//
													+ "&autocorrect=1"//
													+ "&format=json"
													+ "&api_key=" + LASTFM_KEY;
	
	private static final String ALBUMINFO_QUERY = "http://ws.audioscrobbler.com/2.0/"//
													+ "?method=album.getInfo"//
													+ "&album=%s"//
													+ "&artist=%s"//
													+ "&autocorrect=1"//
													+ "&format=json"
													+ "&api_key=" + LASTFM_KEY;
	
	private static final String SEARCHALBUM_QUERY = "http://ws.audioscrobbler.com/2.0/"//
													+ "?method=album.search"//
													+ "&album=%s"//
													+ "&format=json"
													+ "&api_key=" + LASTFM_KEY;
	
	@Override
	public List<Album> findAlbums(final String track, final String artist) {
		// Prepare Requests manager
		LastFmRequestsManager.prepare();

		// Do the search of albums
		List<Album> foundAlbums;
		switch (AlbumRequestValidator.getRequestStatus(track, artist)) {
		case VALID:
			foundAlbums = doSearchAlbums(track, artist);
			break;
		case REPEATED:
			LoggerUtils.logDebug(log, () -> "Request repeated, returning cached albums:" + cachedAlbums);
			foundAlbums = cachedAlbums;
			break;
		default:
			// Invalid request, return empty
			return Collections.emptyList();
		}

		// Cache Albums found
		setCachedAlbums(foundAlbums);
		LoggerUtils.logDebug(log, () -> foundAlbums.size() + " albums found.");
		LoggerUtils.logDebug(log, () -> foundAlbums.toString());

		return foundAlbums;
	}

	private List<Album> doSearchAlbums(String track, String artist) {
		LoggerUtils.logDebug(log, () -> "Searching album for song:" + track + " | artist:" + artist);
			
		/*
		 * First try to find the full album info, removing the extra information
		 * that comes with the artist/song name like featuring, this because lastFm
		 * don't like it like that.
		 */
		String cleanArtist = StringUtils.removeFeatureInfo(artist);
		String cleanTrack = StringUtils.removeFeatureInfo(track);
		try {
			/*
			 * Find Album by song - artist
			 * call: album.getInfo 
			 */
			Album songAlbum = getValidAlbumBySongName(cleanTrack, cleanArtist);
			if(isValidAlbum(songAlbum)) {
				return Arrays.asList(songAlbum);
			}
			
			/*
			 *  Find by track.getInfo
			 */
			Album trackAlbum = getValidAlbumByTrackInfo(cleanTrack, cleanArtist);
			if(isValidAlbum(trackAlbum)) {
				return Arrays.asList(trackAlbum);
			}
			
			/*
			 * Last resort:
			 * Find by album.search
			 */
			return getValidAlbumByAlbumSearch(trackAlbum, cleanArtist, cleanTrack);
			
		} catch (IOException | IllegalStateException e) {
			log.error("There was an error querying last.fm", e);
			return Collections.emptyList();
		}
	}
	
	private Album getValidAlbumBySongName(String song, String artist) throws IOException {
		return getValidAlbumByAlbumInfo(song, artist, Optional.empty());
	}
	
	private Album getValidAlbumByAlbumInfo(String albumName, String artist, Optional<String> songName)
			throws IOException {
		String requestQuery = String.format(ALBUMINFO_QUERY, albumName, artist);
		Album album = LastFmParser.parseAlbumInfo(doHttpRequest(requestQuery));

		if (!isValidAlbum(album)) {
			return null;
		}

		LoggerUtils.logDebug(log, () -> "Album found throug album.getInfo call.");
		return new Album.Builder()//
				.artistName(album.getArtistName())//
				.songName(songName.orElse(album.getAlbumName()))//
				.name(album.getAlbumName())//
				.coverArt(album.getCoverArt())//
				.build();
	}
	
	private Album getValidAlbumByTrackInfo(String track, String artist) throws IOException {
		String requestQuery = String.format(TRACKINFO_QUERY, track, artist);
		Album trackAlbum = LastFmParser.parseTrackInfo(doHttpRequest(requestQuery));

		// Does the album has a name and a cover art?
		if (isValidAlbum(trackAlbum)) {
			LoggerUtils.logDebug(log, () -> "Album found throug track.getInfo call.");
			return trackAlbum;
		}
		
		// Does the album has a name
		if (hasAlbumName(trackAlbum)) {
			/*
			 * Find by album.getInfo
			 */
			Album a = getValidAlbumByAlbumInfo(trackAlbum.getAlbumName(), trackAlbum.getArtistName(), Optional.of(trackAlbum.getSongName()));
			if(isValidAlbum(a)) {
				return a;
			}
			
			// Empty album with the name found
			return new Album.Builder().name(trackAlbum.getAlbumName()).build();
		}
		
		return null;
	}
	
	private List<Album> getValidAlbumByAlbumSearch(Album album, String expectedArtist, String song) throws IOException {
		String requestQuery = String.format(SEARCHALBUM_QUERY, hasAlbumName(album) ? album.getAlbumName() : song + " " + expectedArtist);
		List<Album> allAlbums = LastFmParser.parseSearchAlbum(doHttpRequest(requestQuery));
		
		// A valid album will have the expected artist
		List<Album> validAlbums = allAlbums.stream()
								.filter(a -> PhraseCalculator.phrase(a.getArtistName()).calculateSimilarityTo(expectedArtist) != PhraseMatch.DIFFERENT)
								.filter(this::isValidAlbum)
								  .map(a -> new Album.Builder()
										   .artistName(a.getArtistName())
										   .songName(song)
										   .name(a.getAlbumName())
										   .coverArt(a.getCoverArt())
										   .build())
								 .collect(Collectors.toList());
		
		if(validAlbums.isEmpty()) {
			// Maybe we got swapped information song <-> artist from the music stream
			validAlbums = allAlbums.stream()
						  .filter(a -> PhraseCalculator.phrase(a.getArtistName()).calculateSimilarityTo(song) != PhraseMatch.DIFFERENT)
						  .filter(this::isValidAlbum)
						  .map(a -> new Album.Builder()
										   .artistName(a.getArtistName())
										   .songName(expectedArtist)
										   .name(a.getAlbumName())
										   .coverArt(a.getCoverArt())
										   .build())
						  .collect(Collectors.toList());
		}
		
		
		if (log.isDebugEnabled() && !validAlbums.isEmpty()) {
			log.debug("Albums found throug album.search call.");
		}
		
		return validAlbums;
	}
	
	private String doHttpRequest(String request) throws IOException {
		if (!LastFmRequestsManager.enableRequest()) {
			throw new IllegalStateException("The limit of requests per second was reached.");
		}
		
		// We can do the request, then count it
		String response = HttpUtils.doGet(request); 
		LastFmRequestsManager.count();
		
		return response;
	}
	
	/*
	 * Conditions 
	 */
	
	private boolean hasAlbumName(Album a) {
		return a != null && StringUtils.exists(a.getAlbumName());
	}
	
	private boolean hasValidCoverArt(Album a) {
		return a!= null &&  a.getCoverArt().isPresent() 
				&& StringUtils.exists(a.getCoverArt().flatMap(CoverArt::getLargeUri).map(URI::toString));
	}
	
	private boolean isValidAlbum(Album a) {
		return a != null && hasAlbumName(a) && hasValidCoverArt(a);
	}
	
	private void setCachedAlbums(List<Album> foundAlbums) {
		LoggerUtils.logDebug(log, () -> "Caching albums:" + foundAlbums);
		cachedAlbums = foundAlbums;
	}

}
