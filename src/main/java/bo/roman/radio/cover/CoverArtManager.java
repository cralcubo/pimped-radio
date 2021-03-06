package bo.roman.radio.cover;

import static bo.roman.radio.utilities.StringUtils.removeBracketsInfo;
import static bo.roman.radio.utilities.StringUtils.removeFeatureInfo;

import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bo.roman.radio.cover.album.AlbumComparator;
import bo.roman.radio.cover.album.AlbumFindable;
import bo.roman.radio.cover.album.last.fm.LastFmAlbumFinder;
import bo.roman.radio.cover.model.Album;
import bo.roman.radio.cover.model.CoverArt;
import bo.roman.radio.cover.model.Radio;
import bo.roman.radio.cover.station.CacheLogoUtil;
import bo.roman.radio.cover.station.FacebookRadioStationFinder;
import bo.roman.radio.cover.station.RadioStationFindable;
import bo.roman.radio.utilities.LoggerUtils;
import bo.roman.radio.utilities.StringUtils;

public class CoverArtManager implements ICoverArtManager {
	private final static Logger log = LoggerFactory.getLogger(CoverArtManager.class);
	
	private final AlbumFindable albumFinder;
	private final RadioStationFindable radioFinder;
	
	public CoverArtManager() {
		this(new LastFmAlbumFinder(), new FacebookRadioStationFinder());
	}
	
	CoverArtManager(AlbumFindable albumFinder, RadioStationFindable radioFinder) {
		this.albumFinder = albumFinder;
		this.radioFinder = radioFinder;
	}

	@Override
	public Optional<Album> getAlbumWithCover(String song, String artist) {
		// Clean song and artist from info in brackets or featuring info
		// to have a better Album search.
		LoggerUtils.logDebug(log, () -> String.format("Cleaning brackets and feat. info from: [%s] - [%s]", song, artist));
		String cSong = removeFeatureInfo(removeBracketsInfo(song));
		String cArtist = removeFeatureInfo(removeBracketsInfo(artist));
		LoggerUtils.logDebug(log, () -> String.format("Clean info: [%s] - [%s]", cSong, cArtist));
		
		log.info("Finding Album for [{} - {}]", cSong, cArtist);
		
		// Get all the albums found in Amazon and give priority to the albums name 
		// that have the same name as the song that was used to find it.
		final AlbumComparator albumComparator = new AlbumComparator(song, artist);
		List<Album> allAlbums = albumFinder.findAlbums(cSong, cArtist).stream()
											.sorted(albumComparator)
											.collect(Collectors.toList());
		log.info("{} albums found.", allAlbums.size());
		if(log.isDebugEnabled()) {
			allAlbums.forEach(a -> log.debug(a.toString()));
		}
		
		if(allAlbums.isEmpty()) {
			return Optional.empty();
		}
		
		Comparator<Album> proportionsComparator = new CoverArtProportionsComparator();
		Optional<Album> albumFound = allAlbums.stream().min(proportionsComparator);
		log.info("Match for Album found {}", albumFound);
		
		return albumFound;
	}

	@Override
	public Optional<Radio> getRadioWithLogo(String radioName) {
		if (!StringUtils.exists(radioName)) {
			log.info("There is no radioName to find a Radio.");
			return Optional.empty();
		}
		// Check if the radio log is already cached
		if (CacheLogoUtil.isCached(radioName)) {
			log.info("Returning Cached Radio Logo.");
			Path cachedLogoPath = CacheLogoUtil.getCachedLogoPath(radioName);
			return Optional.of(new Radio(radioName, Optional.of(cachedLogoPath.toUri())));
		}

		// Logo is not cached, send a query to retrieve it
		Optional<Radio> oRadio = radioFinder.findRadioStation(radioName);
		if (oRadio.isPresent()) {
			// If the logo of the radio was found cache it
			CacheLogoUtil.cacheRadioLogo(radioName, oRadio.flatMap(Radio::getLogoUri));
			return oRadio;
		}

		// Radio logo is not cached and was not found on internet
		// return default app logo.
		return Optional.of(new Radio(radioName, Optional.empty()));
	}
	
	private class CoverArtProportionsComparator implements Comparator<Album>{

		@Override
		public int compare(Album a1, Album a2) {
			Optional<CoverArt> oCA1 = a1.getCoverArt();
			 Optional<CoverArt> oCA2 = a2.getCoverArt();
			 
			 if(oCA1.isPresent() && oCA2.isPresent()) {
				 float r1 = Math.abs(oCA1.get().getMaxWidth() * 1.0f/oCA1.get().getMaxHeight() - 1);
				 float r2 = Math.abs(oCA2.get().getMaxWidth() * 1.0f/oCA2.get().getMaxHeight() - 1);
				 return Float.compare(r1, r2);
			 }
			 else if(oCA1.isPresent() && !oCA2.isPresent()) {
				 return 1;
			 }
			 else if(!oCA1.isPresent() && oCA2.isPresent()) {
				 return -1;
			 }
			 else {
				 return 0;
			 }
		}
		
	}
	

}
