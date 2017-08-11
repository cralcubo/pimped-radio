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
	
	private static final String SEARCHTRACK_QUERY = "http://ws.audioscrobbler.com/2.0/"//
													+ "?method=track.search"//
													+ "&limit=10"
													+ "&track=%s"//
													+ "&artist=%s"//
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
			if(songAlbum != null) {
				return Arrays.asList(songAlbum);
			}
			
			/*
			 *  Find by track.getInfo
			 */
			Album trackAlbum = getValidAlbumByTrackInfo(cleanTrack, cleanArtist);
			if(trackAlbum != null) {
				return Arrays.asList(trackAlbum);
			}
			
			/*
			 * Last resort:
			 * Find by track.search
			 */
			return getValidAlbumsByTrackSearch(cleanTrack, cleanArtist);
			
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

		if (!hasAlbumName(album) || !hasValidCoverArt(album)) {
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
		if (hasAlbumName(trackAlbum) && hasValidCoverArt(trackAlbum)) {
			LoggerUtils.logDebug(log, () -> "Album found throug track.getInfo call.");
			return trackAlbum;
		}
		
		// Does the album has a name
		if (hasAlbumName(trackAlbum)) {
			/*
			 * Find by album.getInfo
			 */
			return getValidAlbumByAlbumInfo(trackAlbum.getAlbumName(), trackAlbum.getArtistName(), Optional.of(trackAlbum.getSongName()));
		}

		return null;
	}
	
	private List<Album> getValidAlbumsByTrackSearch(String track, String artist) throws IOException {
		String requestQuery = String.format(SEARCHTRACK_QUERY, track, artist);
		List<Album> allAlbums = LastFmParser.parseSearchTrack(doHttpRequest(requestQuery));
		
		// Filter out all invalid albums
		List<Album> albums = allAlbums.stream()
				.filter(this::hasAlbumName)//
				.filter(this::hasValidCoverArt)//
				.collect(Collectors.toList());//
		
		if (log.isDebugEnabled() && !albums.isEmpty()) {
			log.debug("Albums found throug track.search call.");
		}
		
		return albums;
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
	
	private void setCachedAlbums(List<Album> foundAlbums) {
		LoggerUtils.logDebug(log, () -> "Caching albums:" + foundAlbums);
		cachedAlbums = foundAlbums;
	}

}
