package bo.roman.radio.cover;

import java.net.URL;
import java.util.List;

public interface ICoverFinder {
	/**
	 * Find the art cover of a song.
	 * 
	 * @param song
	 * @param artist
	 * @return the links with the cover art.
	 */
	List<URL> getCoverLinks(String song, String artist);

}
