package bo.roman.radio.cover.album;

import java.io.IOException;
import java.util.Optional;

import bo.roman.radio.cover.model.Album;
import bo.roman.radio.cover.model.CoverArt;

public interface CoverArtFindable {
	/**
	 * Find the link of the picture of the 
	 * front cover of an Album. 
	 * 
	 * @param album is the album object to find its cover
	 * @return the HTTP link of the front cover picture
	 * @throws IOException
	 */
	Optional<CoverArt> findCoverUrl(Album album) throws IOException;

}
