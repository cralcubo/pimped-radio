package bo.roman.radio.cover;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import org.musicbrainz.controller.Recording;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bo.roman.radio.cover.album.AlbumFindable;
import bo.roman.radio.cover.album.CoverArtArchiveFinder;
import bo.roman.radio.cover.album.CoverArtFindable;
import bo.roman.radio.cover.album.MBAlbumFinder;
import bo.roman.radio.cover.model.Album;
import bo.roman.radio.cover.model.Radio;
import bo.roman.radio.cover.station.CacheLogoUtil;
import bo.roman.radio.cover.station.FacebookRadioStationFinder;
import bo.roman.radio.cover.station.RadioStationFindable;
import bo.roman.radio.utilities.LoggerUtils;
import bo.roman.radio.utilities.StringUtils;

public class CoverArtManager implements RadioCoverInterface{
	private final static Logger log = LoggerFactory.getLogger(CoverArtManager.class);
	
	private static final int MAXALBUMS_FETCHED = 10;
	
	private static final String DEFAULTLOGO_URL = "src/main/resources/radio-player.jpg";
	
	private final AlbumFindable albumFinder;
	private final CoverArtFindable coverFinder;
	private final RadioStationFindable radioFinder;
	
	public CoverArtManager() {
		this.albumFinder = new MBAlbumFinder(MAXALBUMS_FETCHED, new Recording());
		this.coverFinder = new CoverArtArchiveFinder();
		this.radioFinder = new FacebookRadioStationFinder();
	}

	CoverArtManager(AlbumFindable albumFinder, CoverArtFindable coverFinder, RadioStationFindable radioFinder) {
		this.albumFinder = albumFinder;
		this.coverFinder = coverFinder;
		this.radioFinder = radioFinder;
	}

	@Override
	public Optional<Album> getAlbumWithCover(String song, String artist) {
		if(!StringUtils.exists(song) || !StringUtils.exists(artist)) {
			return Optional.empty();
		}
		
		// First find the albums that match the song and artist
		List<Album> albums = albumFinder.findAlbums(song, artist);
		
		for(int i = 0; i < albums.size(); i++) {
			// Get the MBID first
			Album album = albums.get(i);
			log.debug("[{}] Fetching CoverArt for {}", i, album);
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
		
		// No cover art album found, return the first album
		// retrieved from MusicBrains
		return albums.stream().findFirst();
	}

	@Override
	public Optional<Radio> getRadioWithLogo(String radioName) {
		if (!StringUtils.exists(radioName)) {
			return Optional.empty();
		}
		
		// Check if the radio log is already cached
		if(CacheLogoUtil.isCached(radioName)) {
			Path cachedLogoPath = CacheLogoUtil.getCachedLogoPath(radioName);
			return Optional.of(new Radio(radioName, cachedLogoPath.toFile().getAbsolutePath()));
		}
		
		// Logo is not cached, send a query to retrieve it
		Optional<Radio> oRadio = radioFinder.findRadioStation(radioName);
		if(oRadio.isPresent()) {
			// If the logo of the radio was found cache it
			CacheLogoUtil.cacheRadioLogo(radioName, oRadio.get().getLogoUrl());
			return oRadio;
		}
		
		// Radio logo is not cached and was not found on internet
		// return default app logo.
		return Optional.of(new Radio(radioName, DEFAULTLOGO_URL));
	}
	
}
