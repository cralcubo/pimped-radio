package bo.roman.radio.cover;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bo.roman.radio.cover.model.Album;
import bo.roman.radio.cover.model.Radio;
import bo.roman.radio.utilities.LoggerUtils;
import bo.roman.radio.utilities.StringUtils;

public class CoverArtManager implements RadioCoverInterface{
	private final static Logger log = LoggerFactory.getLogger(CoverArtManager.class);
	
	private static final String RADIOICON_PATH = "src/main/resources/radio-player.jpg";
	
	private final AlbumFindable albumFinder;
	private final CoverArtFindable coverFinder;

	public CoverArtManager(AlbumFindable albumFinder, CoverArtFindable coverFinder) {
		this.albumFinder = albumFinder;
		this.coverFinder = coverFinder;
	}

	@Override
	public Optional<Album> getAlbumWithCover(String song, String artist) {
		if(!StringUtils.exists(song) || !StringUtils.exists(artist)) {
			return Optional.empty();
		}
		
		// First find the albums that match the song and artist
		List<Album> albums = albumFinder.findAlbums(song, artist);
		
		for(Album album : albums) {
			// Get the MBID first
			Optional<String> oMbid = album.getMbid();
			if(StringUtils.exists(oMbid)) {
				try {
					// Find the CoverArt URL
					Optional<String> oUrl = coverFinder.findCoverUrl(oMbid.get());
					// If it exists:
					if(StringUtils.exists(oUrl)) {
						Album richAlbum = new Album.Builder()
								.artistName(artist)
								.songName(song)
								.name(album.getName())
								.coverUrl(oUrl.get())
								.mbid(oMbid.get())
								.build();
						return Optional.of(richAlbum);
					}
				} catch (IOException e) {
					LoggerUtils.logDebug(log, () -> "Cover not found for " + album, e);
				}
			}
		}
		
		return Optional.empty();
	}

	@Override
	public Optional<Radio> getRadioWithCover(String radioName) {
		if (StringUtils.exists(radioName)) {
			return Optional.of(new Radio(radioName, RADIOICON_PATH));
		}

		return Optional.empty();
	}
}
