package bo.roman.radio.cover;

public interface ICoverFinder {
	/**
	 * Find the art cover of a song.
	 * 
	 * @param song
	 * @param artist
	 * @return the link with the cover art.
	 */
	String getCoverLink(String song, String artist);

}
