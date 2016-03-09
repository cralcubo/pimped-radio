package bo.roman.radio.cover;

import java.util.Optional;

public interface RadioCoverInterface {
	
	/**
	 * Get the HTML link of the cover of the album
	 * based on a song and an artist.
	 * 
	 * @param song
	 * @param artist
	 * @return
	 */
	Optional<String> getCoverUrl(String song, String artist);
	
	/**
	 * Get the path of the icon of the 
	 * radio player.
	 * 
	 * @return
	 */
	Optional<String> getRadioPlayerPath();

}
