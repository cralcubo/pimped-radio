package bo.roman.radio.cover.album.last.fm;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

import bo.roman.radio.cover.model.Album;
import bo.roman.radio.cover.model.CoverArt;
import bo.roman.radio.cover.model.mapping.last.fm.AlbumInfoMapping;
import bo.roman.radio.cover.model.mapping.last.fm.LastFmImage;
import bo.roman.radio.cover.model.mapping.last.fm.SearchTrackMapping;
import bo.roman.radio.cover.model.mapping.last.fm.TrackInfoMapping;
import bo.roman.radio.utilities.LoggerUtils;


/**
 * Parse a LastFm json response to create a Pimped Radio Album. <br/>
 * The conditions to create an album are:
 * <ul>
 * <li>Album must have a name</li>
 * <li>Album must have a valid cover art</li>
 * </ul>
 * A valid cover art must have at least a large picture.
 * 
 * @author croman
 *
 */
public class LastFmParser {
	private static final Logger log = LoggerFactory.getLogger(LastFmParser.class);
	
	private static final Gson gsonParser = new Gson();
	private static final String LARGE_IMAGE = "extralarge";
	private static final String MEDIUM_IMAGE = "large";
	private static final String SMALL_IMAGE = "medium";
	

	public static List<Album> parseSearchTrack(String jsonResponse) {
		LoggerUtils.logDebug(log, () -> "Parsing track.search request.");
		LoggerUtils.logDebug(log, () -> jsonResponse);
		SearchTrackMapping mappingResults = gsonParser.fromJson(jsonResponse, SearchTrackMapping.class);
		List<SearchTrackMapping.Track> tracks = Optional.ofNullable(mappingResults.results)
														.map(r -> r.trackmatches)
														.map(tm -> tm.track)
														.orElseGet(() -> Collections.emptyList());
		
		return tracks.stream()
					 .map(LastFmParser::convertTrackToAlbum)
					 .collect(Collectors.toList());
	}

	public static Album parseTrackInfo(String jsonResponse) {
		LoggerUtils.logDebug(log, () -> "Parsing track.getInfo request.");
		LoggerUtils.logDebug(log, () -> jsonResponse);
		TrackInfoMapping mappingResults = gsonParser.fromJson(jsonResponse, TrackInfoMapping.class);
		
		return Optional.ofNullable(mappingResults)
					   .map(m -> m.track)
					   .map(LastFmParser::convertTrackToAlbum)
					   .orElse(null);
	}
	
	public static Album parseAlbumInfo(String jsonResponse) {
		LoggerUtils.logDebug(log, () -> "Parsing album.getInfo request.");
		LoggerUtils.logDebug(log, () -> jsonResponse);
		AlbumInfoMapping mappingResults = gsonParser.fromJson(jsonResponse, AlbumInfoMapping.class);
		
		return Optional.ofNullable(mappingResults)
					   .map(m -> m.album)
					   .map(LastFmParser::convertLastFmAlbumToAlbum)
					   .orElse(null);
	}
	
	/*
	 * Utilities
	 */
	
	private static Album convertLastFmAlbumToAlbum(AlbumInfoMapping.Album album) {
		if(album == null) {
			return null;
		}
		// No track info
		return new Album.Builder()
						.artistName(album.artist)
						.name(album.name)
						.coverArt(buildCoverArt(album.image))
						.build();
	}
	
	private static Album convertTrackToAlbum(TrackInfoMapping.Track track) {
		if(track == null) {
			return null;
		}
		
		LoggerUtils.logDebug(log, () -> "Converting TrackInfoMapping.Track to Album.");
		String albumName = Optional.ofNullable(track.album)
									.map(a -> a.title)
									.orElse(null);
		String songName = track.name;
		String artistName = Optional.ofNullable(track.artist)
									.map(a -> a.name)
									.orElse(null);
		
		Optional<CoverArt> oCover = buildCoverArt(Optional.ofNullable(track.album)
														  .map(a -> a.image)
														  .orElseGet(() -> Collections.emptyList()));
		
		return new Album.Builder()
						.artistName(artistName)
						.songName(songName)
						.name(albumName)
						.coverArt(oCover)
						.build();
	}
	
	private static Album convertTrackToAlbum(SearchTrackMapping.Track track) {
		if(track == null) {
			return null;
		}
		
		// No Album name, therefore we set the name of the track instead.
		return new Album.Builder()
						.artistName(track.artist)
						.songName(track.name)
						.name(track.name)
						.coverArt(buildCoverArt(track.image))
						.build();
	}
	
	private static Optional<CoverArt> buildCoverArt(List<LastFmImage> images) {
		String largeImage = null;
		String mediumImage = null;
		String smallImage = null;
		for (LastFmImage i : images) {
			if (i == null) {
				continue;
			}
			switch (i.size) {
			case LARGE_IMAGE:
				largeImage = imageCleaner(i.url);
				break;
			case MEDIUM_IMAGE:
				mediumImage = imageCleaner(i.url);
				break;
			case SMALL_IMAGE:
				smallImage = imageCleaner(i.url);
			}
		}
		
		return Optional.of(new CoverArt.Builder()//
										.largeUri(largeImage)//
										.mediumUri(mediumImage)//
										.smallUri(smallImage)//
										.build());
	}
	
	private static String imageCleaner(String imageUrl) {
		return imageUrl != null ? imageUrl.replaceAll("\\s+", "") : null;
	}

}
