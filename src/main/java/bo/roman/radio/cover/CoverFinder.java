package bo.roman.radio.cover;

import java.net.URL;
import java.util.List;

import bo.roman.radio.cover.model.Album;

/**
 * Find the cover, sending a request to MusicBrainz based on the song played and
 * the name of the artist. With this information it will be retrieved first the
 * Album ID and with this ID another request will be send to coverartachive to
 * get the link of the cover art picture.
 * 
 * @author christian
 *
 */
public class CoverFinder implements ICoverFinder {
	
	private final MusicBrainzAlbumFinder finder;
	private final int limit;
	
	public CoverFinder(int limit, MusicBrainzAlbumFinder finder) {
		this.limit = limit;
		this.finder = finder;
	}

	@Override
	public List<URL> getCoverLinks(String song, String artist) {
		List<Album> albums = finder.getAlbums(song, artist);
		
		return null;
	}
	
	

}
