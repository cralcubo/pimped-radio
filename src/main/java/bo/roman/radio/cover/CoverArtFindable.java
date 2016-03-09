package bo.roman.radio.cover;

import java.io.IOException;
import java.util.Optional;

public interface CoverArtFindable {
	/**
	 * Find the link of the picture of the 
	 * front cover of an Album. 
	 * 
	 * @param mbid is the ID from MusicBrainz
	 * @return the HTTP link of the front cover picture
	 * @throws IOException
	 */
	Optional<String> findCoverUrl(String mbid) throws IOException;

}
