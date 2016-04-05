package bo.roman.radio.cover;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bo.roman.radio.cover.album.AlbumFindable;
import bo.roman.radio.cover.album.CoverArtArchiveFinder;
import bo.roman.radio.cover.album.CoverArtFindable;
import bo.roman.radio.cover.album.MBAlbumFinder;
import bo.roman.radio.cover.model.Album;
import bo.roman.radio.cover.model.CoverArt;
import bo.roman.radio.cover.model.Radio;
import bo.roman.radio.cover.station.CacheLogoUtil;
import bo.roman.radio.cover.station.FacebookRadioStationFinder;
import bo.roman.radio.cover.station.RadioStationFindable;
import bo.roman.radio.utilities.ExecutorUtils;
import bo.roman.radio.utilities.LoggerUtils;
import bo.roman.radio.utilities.StringUtils;

public class CoverArtManager implements RadioCoverInterface{
	private final static Logger log = LoggerFactory.getLogger(CoverArtManager.class);
	
	private static final int MAXALBUMS_FETCHED = 10;
	
	private static final String DEFAULTLOGO_PATH = "src/main/resources/pimped-radio-flat.png";
	
	private final AlbumFindable albumFinder;
	private final CoverArtFindable coverFinder;
	private final RadioStationFindable radioFinder;
	
	public CoverArtManager() {
		this(new MBAlbumFinder(MAXALBUMS_FETCHED), new CoverArtArchiveFinder(), new FacebookRadioStationFinder());
	}

	CoverArtManager(AlbumFindable albumFinder, CoverArtFindable coverFinder, RadioStationFindable radioFinder) {
		this.albumFinder = albumFinder;
		this.coverFinder = coverFinder;
		this.radioFinder = radioFinder;
	}
	
	/**
	 * {@inheritDoc}}
	 */
	@Override
	public Optional<Album> getAlbumWithCoverAsync(String song, String artist) {
		if(!StringUtils.exists(song) || !StringUtils.exists(artist)) {
			log.info("There is no Song name and/or Artist to find an Album.");
			return Optional.empty();
		}
		
		// First find the albums that match the song and artist
		List<Album> albums = albumFinder.findAlbums(song, artist);
		
		// No albums to retrieve their cover art
		if(albums.isEmpty()) {
			return Optional.empty();
		}
		
		// Build an Executor with all the threads needed to find
		// the Album covers.
		final Executor executor = ExecutorUtils.fixedThreadPoolFactory(albums.size());
		
		// Find asynchronously all the covers available
		List<CompletableFuture<Optional<Album>>> asyncAlbums = albums.stream()
				.filter(a -> StringUtils.exists(a.getMbid()))
				.map(album -> CompletableFuture.supplyAsync(albumWithCoverSupplier(album, song, artist), executor))
				.collect(Collectors.toList());
		
		// Get the first Abum with cover
		Optional<Album> albumWithCover = asyncAlbums.stream()
				.map(CompletableFuture::join)
				.filter(Optional::isPresent)
				.map(Optional::get)
				.findFirst();
		
		if(albumWithCover.isPresent()) {
			return albumWithCover;
		}
		
		// No cover art album found, return the first cover-less album
		// retrieved from MusicBrains
		return albums.stream().findFirst();
	}
	
	private Supplier<Optional<Album>> albumWithCoverSupplier(Album album, String song, String artist) {
		log.info("Fetching CoverArt for [{}]", album);
		return () -> {
			try {
				Optional<CoverArt> oArt = coverFinder.findCoverUrl(album);
				// If it exists:
				if(oArt.isPresent()) {
					Album richAlbum = new Album.Builder()
							.artistName(artist)
							.songName(song)
							.name(album.getName())
							.coverArt(oArt)
							.mbid(album.getMbid().get())
							.status(album.getStatus())
							.build();
					log.info("Album with Cover found [{}]", richAlbum);
					return Optional.of(richAlbum);
				}
			} catch (Exception e) {
				log.info("Cover not found for [{}]. Error={}", album, e.getMessage());
				LoggerUtils.logDebug(log, () -> "Cover not found for " + album, e);
			}
			
			return Optional.empty();
		};
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
			return Optional.of(new Radio(radioName, cachedLogoPath.toUri()));
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
		return Optional.of(new Radio(radioName, Paths.get(DEFAULTLOGO_PATH).toUri()));
	}
}
