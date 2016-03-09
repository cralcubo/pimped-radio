package bo.roman.radio.cover;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bo.roman.radio.cover.model.Album;
import bo.roman.radio.utilities.LoggerUtils;

public class CoverArtManager implements RadioCoverInterface {
	private static final Logger LOG = LoggerFactory.getLogger(CoverArtManager.class);
	
	private static final String RADIOICON_PATH = "src/main/resources/radio-player.jpg";
	
	private final AlbumFindable albumFinder;
	private final CoverArtFindable coverFinder;
	
	public CoverArtManager(AlbumFindable albumFinder, CoverArtFindable coverFinder) {
		this.albumFinder = albumFinder;
		this.coverFinder = coverFinder;
	}
	
	
	public Optional<String> getCoverUrl(String song, String artist) {
		// First find the albums that match the song and artist
		List<Album> albums = albumFinder.findAlbums(song, artist);
		List<String> mbids = albums.stream()
							.map(Album::getMbid)
							.filter(oId -> oId.isPresent())
							.filter(oId -> !oId.get().isEmpty())
							.map(Optional::get)
							.collect(Collectors.toList());
		
		// With all the MBIDs now fetch the cover art and return the first found
		for (String mbid : mbids) {
			try {
				// Send request to get the cover link
				Optional<String> linkOpt = coverFinder.findCoverUrl(mbid);
				if (!linkOpt.orElse("").isEmpty()) {
					return linkOpt;
				}
			} catch (IOException e) {
				LoggerUtils.logDebug(LOG,
						() -> String.format("Cover not found for the mbid=%s [%s - %s]", mbid, song, artist), e);
			}
		}
		
		// No cover art found
		return Optional.empty();
	}
	
	
	public Optional<String> getRadioPlayerPath() {
		return Optional.of(RADIOICON_PATH);
	}

}
