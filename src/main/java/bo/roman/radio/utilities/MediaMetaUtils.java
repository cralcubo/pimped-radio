package bo.roman.radio.utilities;

import java.util.Optional;

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
		
		return Optional.empty();
	}
	
	private static Song buildSong(String songName, String artistName) {
		return new Song.Builder()
				.name(songName.trim())
				.artist(artistName.trim())
				.build();
	}
	
}
