package bo.roman.radio.utilities;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import bo.roman.radio.cover.model.Song;
import uk.co.caprica.vlcj.player.MediaMeta;

public class MediaMetaUtils {
	private static final String FROMSPACEDASH_REGEX = "(?<=-)\\s+.+$";
	private static final String TILSPACEDASH_REGEX = "^.+?\\s+(?=-)";
	private static final String SONGINFO_REGEX = "(?<=.)\\(.*\\).*";
	
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
	 * When finding the songName, if there are braces next to it '()', this is extra information that will be removed.
	 * The reason is that generally this extra information make the finding of the Album and therefore the CoverArt
	 * much complicated, so to help on this search this information is removed.
	 * 
	 * @param meta
	 * @return
	 */
	public static Optional<Song> buildSong(MediaMeta meta) {
		// Get the song artist
		String songName = meta.getTitle();
		String artistName = meta.getArtist();
		if(StringUtils.exists(songName) && StringUtils.exists(artistName)) {
			songName = StringUtils.cleanIt(songName);
			artistName = StringUtils.cleanIt(artistName);
			return buildCleanSong(songName, artistName);
		}
		
		// Check NowPlaying
		String nowPlaying = meta.getNowPlaying();
		if(StringUtils.exists(nowPlaying)){
			nowPlaying = StringUtils.cleanIt(nowPlaying);
			return parseNowPlaying(nowPlaying);
		}
		
		return Optional.empty();
	}

	private static Optional<Song> parseNowPlaying(String nowPlaying) {
		// Using regex to get the song and artist name
		Matcher songMatcher = Pattern.compile(TILSPACEDASH_REGEX).matcher(nowPlaying);
		Matcher artistMatcher = Pattern.compile(FROMSPACEDASH_REGEX).matcher(nowPlaying);
		if(songMatcher.find() && artistMatcher.find()) {
			String artistName = songMatcher.group();
			String songName = artistMatcher.group();
			return buildCleanSong(songName, artistName);
		}
		
		// There is no artist - song pair, just return the full nowPlaying String
		return buildCleanSong(nowPlaying);
	}
	
	private static Optional<Song> buildCleanSong(String nowPlaying) {
		return buildCleanSong(nowPlaying, null);
	}
	
	/**
	 * - Trim the songName and artist name.
	 * - Remove the extra info attached to a song.
	 * 
	 * @param songName
	 * @param artist
	 * @return
	 */
	private static Optional<Song> buildCleanSong(String songName, String artist) {
		if(!StringUtils.exists(songName)) {
			return Optional.empty();
		}
		
		songName = songName.replaceAll(SONGINFO_REGEX, "").trim();
		if(StringUtils.exists(songName) && !StringUtils.exists(artist)) {
			return Optional.of(new Song(songName, ""));
		}
		
		artist = artist.trim();
		return Optional.of(new Song(songName, artist));
	}
	
	/**
	 * Sometimes the title of a Radio Station
	 * has more information than only its name.
	 * For this reason we will assume that when
	 * there is a '-' all the info after it is 
	 * extra information that is not needed to find
	 * the logo of the Radio Station.
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
		String parsedRadioName = StringUtils.cleanIt(radioName.trim());
		
		// Separate the radio name if there is in the middle a '-'
		Matcher m = Pattern.compile(TILSPACEDASH_REGEX).matcher(parsedRadioName);
		if(m.find()) {
			parsedRadioName = m.group().trim();
		}
		
		return Optional.of(parsedRadioName);
	}
	
}
