package bo.roman.radio.cover;

import java.util.Optional;

import bo.roman.radio.cover.model.Album;
import bo.roman.radio.cover.model.Radio;

public interface ICoverArtManager {
	
	static ICoverArtManager getInstance = new CoverArtManager();

	/**
	 * Get the Album for the song and artist in an Async way. Depending on the
	 * number of Albums found for the pair song - artist, an equal number of Threads
	 * will be created to search for the Album Cover URL.
	 * 
	 * @param song
	 * @param artist
	 * @return
	 */
	Optional<Album> getAlbumWithCover(String song, String artist);

	/**
	 * Get the Radio that is playing with the logo of it, if it exists.
	 * 
	 * @param radioName
	 * @return
	 */
	Optional<Radio> getRadioWithLogo(String radioName);

}
