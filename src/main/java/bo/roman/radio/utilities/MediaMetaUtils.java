package bo.roman.radio.utilities;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringEscapeUtils;

import bo.roman.radio.cover.model.Song;
import uk.co.caprica.vlcj.player.MediaMeta;

public class MediaMetaUtils {
	private static final String FROMSPACEDASH_REGEX = "(?<=-)\\s+.+$";
	private static final String TILSPACEDASH_REGEX = "^.+?\\s+(?=-)";
	
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
			songName = StringEscapeUtils.unescapeHtml4(songName).trim();
			artistName = StringEscapeUtils.unescapeHtml4(artistName).trim();
			return Optional.of(new Song(songName, artistName));
		}
		
		// Check NowPlaying
		String nowPlaying = meta.getNowPlaying();
		if(StringUtils.exists(nowPlaying)){
			nowPlaying = StringEscapeUtils.unescapeHtml4(nowPlaying);
			return parseNowPlaying(nowPlaying);
		}
		
		return Optional.empty();
	}

	private static Optional<Song> parseNowPlaying(String nowPlaying) {
		// Using regex to get the song and artist name
		Matcher songMatcher = Pattern.compile(TILSPACEDASH_REGEX).matcher(nowPlaying);
		Matcher artistMatcher = Pattern.compile(FROMSPACEDASH_REGEX).matcher(nowPlaying);
		if(songMatcher.find() && artistMatcher.find()) {
			String artistName = songMatcher.group().trim();
			String songName = artistMatcher.group().trim();
			return Optional.of(new Song(songName, artistName));
		}
		
		// There is no artist - song pair, just return the full nowPlaying String
		return Optional.of(new Song(nowPlaying.trim(), ""));
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
	public static Optional<String> findRadioName(MediaMeta meta) {
		String radioName = meta.getTitle();
		if(!StringUtils.exists(radioName)) {
			return Optional.empty();
		}
		
		// First ecape html encoding
		String parsedRadioName = StringEscapeUtils.unescapeHtml4(radioName.trim());
		
		// Separate the radio name if there is in the middle a '-'
		Matcher m = Pattern.compile(TILSPACEDASH_REGEX).matcher(parsedRadioName);
		if(m.find()) {
			parsedRadioName = m.group().trim();
		}
		
		return Optional.of(parsedRadioName);
	}
	
}
