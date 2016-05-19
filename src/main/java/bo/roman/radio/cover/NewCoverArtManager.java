package bo.roman.radio.cover;

import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bo.roman.radio.cover.album.AlbumFindable;
import bo.roman.radio.cover.album.AmazonAlbumFinder;
import bo.roman.radio.cover.model.Album;
import bo.roman.radio.cover.model.CoverArt;
import bo.roman.radio.cover.model.Radio;
import bo.roman.radio.cover.station.CacheLogoUtil;
import bo.roman.radio.cover.station.FacebookRadioStationFinder;
import bo.roman.radio.cover.station.RadioStationFindable;
import bo.roman.radio.utilities.RegExUtil;
import bo.roman.radio.utilities.StringUtils;

public class NewCoverArtManager implements CoverArtManagerInterface {
	private final static Logger log = LoggerFactory.getLogger(NewCoverArtManager.class);
	
	private final AlbumFindable albumFinder;
	private final RadioStationFindable radioFinder;
	
	public NewCoverArtManager() {
		this(new AmazonAlbumFinder(), new FacebookRadioStationFinder());
	}
	
	NewCoverArtManager(AlbumFindable albumFinder, RadioStationFindable radioFinder) {
		this.albumFinder = albumFinder;
		this.radioFinder = radioFinder;
	}

	@Override
	public Optional<Album> getAlbumWithCoverAsync(String song, String artist) {
		
		log.info("Finding Album for [{} - {}]", song, artist);
		List<Album> allAlbums = albumFinder.findAlbums(song, artist);
		
		if(allAlbums.isEmpty()) {
			log.info("No Albums found.");
			return Optional.empty();
		}
		
		Comparator<Album> proportionsComparator = new CoverArtProportionsComparator();
		
		// Find the best Album, this is the one that exactly matches song and artist
		// if there is more than one, return the one that is more square: w/h closer to 1
		Optional<Album> bestAlbum = allAlbums.stream()
											 .filter(a -> a.getSongName().equalsIgnoreCase(song))
											 .filter(a -> a.getArtistName().equalsIgnoreCase(artist))
											 .min(proportionsComparator);
		if(bestAlbum.isPresent()) {
			log.info("Best Match Album found {}", bestAlbum.get());
			return bestAlbum;
		}
		
		// No Exact Match, return a close Match
		Optional<Album> closeAlbum = allAlbums.stream()
											.filter(a -> RegExUtil.phrase(a.getSongName()).beginsWithIgnoreCase(song))
											.filter(a -> RegExUtil.phrase(a.getArtistName()).containsIgnoreCase(artist))
											.min(proportionsComparator);
		
		if(closeAlbum.isPresent()) {
			log.info("Close Match Album found {}", closeAlbum.get());
			return closeAlbum;
		}
		
		// No exact/close match found, then return the album with the best CoverArt: w/h closer to 1
		Optional<Album> albumFound = allAlbums.stream().min(proportionsComparator);
		log.info("Returnin {}", albumFound);
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