package bo.roman.radio.cover;

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
	
	private MusicBrainzAlbumFinder finder;

	@Override
	public String getCoverLink(String song, String artist) {
		
		
		
		return null;
	}
	
	

}
