package bo.roman.radio.cover.album.last.fm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bo.roman.radio.utilities.LoggerUtils;
import bo.roman.radio.utilities.StringUtils;

public class AlbumRequestValidator {
	enum RequestStatus {REPEATED, INVALID, VALID}
	private static final Logger log = LoggerFactory.getLogger(AlbumRequestValidator.class);
	private static String cachedSongArtist = "";
	
	public static RequestStatus getRequestStatus(String track, String artist) {
		
		if(!StringUtils.exists(track) || !StringUtils.exists(artist)) {
			LoggerUtils.logDebug(log, () -> "Requests disabled because no valid track and/or artist were provided.");
			return RequestStatus.INVALID;
		}
		
		String currentSongArtist = track.concat(artist);
		if (cachedSongArtist.equals(currentSongArtist)) {
			LoggerUtils.logDebug(log, () -> "Requests disabled because track:" + track + " and artist:" + artist + " were repeated.");
			return RequestStatus.REPEATED;
		}
		
		cachedSongArtist = currentSongArtist;
		return RequestStatus.VALID;
	}

}
