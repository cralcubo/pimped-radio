package bo.roman.radio.cover;

import java.util.List;

import bo.roman.radio.cover.model.Album;

public interface AlbumFindable {
	/**
	 * Find the albums by song and artist.
	 * 
	 * @param song
	 * @param artist
	 * @return the albums found.
	 */
	List<Album> findAlbums(String song, String artist);

}
