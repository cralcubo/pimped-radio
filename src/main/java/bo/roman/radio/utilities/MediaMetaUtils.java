package bo.roman.radio.utilities;

import java.util.Optional;

import org.apache.commons.lang3.StringEscapeUtils;

import bo.roman.radio.cover.model.Song;
import uk.co.caprica.vlcj.player.MediaMeta;

public class MediaMetaUtils {
	
	/**
	 * Get the information from the MetaData
	 * to build an Album object that will contain:
	 * - a Song
	 * - an Artist
	 * First will be checked if the Song and Artist 
	 * is present in the meta data.
	 * If not, check the nowPlaying and parse to 
	 * find the Song and Artist.
	 * 
	 * @param meta
	 * @return
	 */
	public static Optional<Song> buildSong(MediaMeta meta) {
		// Get the song artist
		String songName = meta.getTitle();
		String artistName = meta.getArtist();
		if(StringUtils.exists(songName) && StringUtils.exists(artistName)) {
			return Optional.of(buildSong(songName, artistName));
		}
		
		// Check NowPlaying
		String nowPlaying = meta.getNowPlaying();
		if(StringUtils.exists(nowPlaying)){
			return parseNowPlaying(nowPlaying);
		}
		
		return Optional.empty();
	}

	private static Optional<Song> parseNowPlaying(String nowPlaying) {
		int index = nowPlaying.indexOf('-');
		// We expect artist - song
		if(index > 0) {
			String artistName = nowPlaying.substring(0, index).trim();
			String songName = nowPlaying.substring(index + 1).trim();
			return Optional.of(buildSong(songName, artistName));
		}
		// There is no artist - song pair, just return the full nowPlaying String
		return Optional.of(buildSong(nowPlaying));
	}
	
	private static Song buildSong(String songName) {
		return buildSong(songName, "");
	}
	
	private static Song buildSong(String songName, String artistName) {
		songName = StringEscapeUtils.unescapeHtml4(songName.trim());
		artistName = StringEscapeUtils.unescapeHtml4(artistName.trim());
		return new Song.Builder()
				.name(songName)
				.artist(artistName)
				.build();
	}
	
	/**
	 * Sometimes the title of a Radio Station
	 * has more information than only its name.
	 * For this reason we will assume that when
	 * there is a '-' all the info after it is 
	 * extra information that is not needed to find
	 * the logo of the Radio Station.
	 * 
	 * Because '&' is used as a delimiter when sending
	 * a GET request, we will URL encode the 
	 * radio name to avoid errors.
	 * 
	 * @param meta
	 * @return
	 */
	public static String parseRadioName(String radioName) {
		// First ecape html encoding
		String parsedRadioName = StringEscapeUtils.unescapeHtml4(radioName.trim());
		
		// Check if there is a '-' and get everything that is before it
		int index = radioName.indexOf('-');
		if(index > 0) {
			parsedRadioName = radioName.substring(0, index).trim();
		}
		
		return parsedRadioName;
	}
	
}
