package bo.roman.radio.cover;

import java.util.Optional;

import bo.roman.radio.cover.model.Album;
import bo.roman.radio.cover.model.Radio;

public interface RadioCoverInterface {
		
	/**
	 * Get the Album for the song and artist
	 * 
	 * @param song
	 * @param artist
	 * @return
	 */
	Optional<Album> getAlbumWithCover(String song, String artist);
	
	/**
	 * Get the Radio that is playing with 
	 * the logo of it, if it exists. 
	 * 
	 * @param radioName
	 * @return
	 */
	Optional<Radio> getRadioWithLogo(String radioName);

}
