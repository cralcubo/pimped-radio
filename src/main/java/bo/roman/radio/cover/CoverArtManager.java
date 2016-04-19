package bo.roman.radio.cover;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bo.roman.radio.cover.album.AlbumFindable;
import bo.roman.radio.cover.album.AmazonCoverFinder;
import bo.roman.radio.cover.album.CoverArtArchiveFinder;
import bo.roman.radio.cover.album.CoverArtFindable;
import bo.roman.radio.cover.album.MBAlbumFinder;
import bo.roman.radio.cover.model.Album;
import bo.roman.radio.cover.model.CoverArt;
import bo.roman.radio.cover.model.Radio;
import bo.roman.radio.cover.station.CacheLogoUtil;
import bo.roman.radio.cover.station.FacebookRadioStationFinder;
import bo.roman.radio.cover.station.RadioStationFindable;
import bo.roman.radio.utilities.FiltersUtil;
import bo.roman.radio.utilities.ExecutorUtils;
import bo.roman.radio.utilities.LoggerUtils;
import bo.roman.radio.utilities.StringUtils;

public class CoverArtManager implements RadioCoverInterface{
	private final static Logger log = LoggerFactory.getLogger(CoverArtManager.class);
	
	private static final int MAXALBUMS_FETCHED = 5;
	
	private static final String DEFAULTLOGO_PATH = "src/main/resources/pimped-radio-flat.png";
	
	private final AlbumFindable albumFinder;
	private final CoverArtFindable coverArchiveFinder;
	private final CoverArtFindable amazonCoverFinder;
	private final RadioStationFindable radioFinder;
	
	public CoverArtManager() {
		this(new MBAlbumFinder(MAXALBUMS_FETCHED), new CoverArtArchiveFinder(), new AmazonCoverFinder(), new FacebookRadioStationFinder());
	}

	CoverArtManager(AlbumFindable albumFinder, CoverArtFindable coverArchiveFinder, CoverArtFindable amazonFinder, RadioStationFindable radioFinder) {
		this.albumFinder = albumFinder;
		this.coverArchiveFinder = coverArchiveFinder;
		this.amazonCoverFinder = amazonFinder;
		this.radioFinder = radioFinder;
	}
	
	@Override
	public Optional<Album> getAlbumWithCoverAsync(String song, String artist) {
		if(!StringUtils.exists(song) || !StringUtils.exists(artist)) {
			log.info("There is no Song name and/or Artist to find an Album.");
			return Optional.empty();
		}
		
		// First find the albums that match the song and artist
		List<Album> albums = albumFinder.findAlbums(song, artist);
		
		// No albums found for song/artist
		// Try to find an album from Amazon
		if(albums.isEmpty()) {
			log.info("No album MBID found. Trying to find album in Amazon.");
			Album album = new Album.Builder()
					.artistName(artist)
					.songName(song)
					.name("")
					.build();
			Optional<Album> richAlbum = findAmazonAlbum(Arrays.asList(album));
			LoggerUtils.logDebug(log, () -> String.format("RichAlbum built after searching in Amazon by [%s - %s] =%s", artist, song, richAlbum));
			return richAlbum;
		}
		
		// Find the covers first in CoverArtArchive
		Optional<Album> coverArchiveAlbum = findCoverArchiveAlbum(albums);
		if(coverArchiveAlbum.isPresent()) {
			log.info("RichAlbum build after CoverArtArchive search=" + coverArchiveAlbum);
			return coverArchiveAlbum;
		}
		
		// No Album with Cover found in CoverArtArchive, try Amazon
		Optional<Album> amazonAlbum = findAmazonAlbum(albums);
		if(amazonAlbum.isPresent()) {
			log.info("RichAlbum build after Amazon search=" + amazonAlbum);
			return amazonAlbum;
		}
		
		// No cover art album found, return the first cover-less album
		// retrieved from MusicBrains
		return albums.stream().findFirst();
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
	
	private Optional<Album> findAmazonAlbum(List<Album> albums) {
		// Amazon just needs the name of an album and the name of an artist
		// to find a Cover Art.
		List<Album> diffNameAlbums = albums.stream()
				.filter(FiltersUtil.distinctByKey(Album::getName))
				.collect(Collectors.toList());
		
		log.info("Fetching CoverArt from Amazon for [{}] Albums [{}]", diffNameAlbums.size(), diffNameAlbums);
		return findCoverArt(diffNameAlbums, amazonCoverFinder);
	}
	
	private Optional<Album> findCoverArchiveAlbum(List<Album> albums) {
		log.info("Fetching CoverArt from CoverArtArchive for [{}] Albums [{}]", albums.size(), albums);
		return findCoverArt(albums, coverArchiveFinder);
	}
	
	/**
	 * Generic method that will find the cover art of a list of albums.
	 * 
	 * @param albums 
	 * @param coverFinder
	 * @return
	 */
	private Optional<Album> findCoverArt(List<Album> albums, CoverArtFindable coverFinder) {
		if(albums.isEmpty()) {
			log.info("No Albums found to retrieve a CoverArt");
			return Optional.empty();
		}
		// Build an Executor with all the threads needed to find
		// the Album covers.
		final Executor executor = ExecutorUtils.fixedThreadPoolFactory(albums.size());
		
		// Find asynchronously all the covers available
		List<CompletableFuture<Optional<Album>>> asyncAlbums = albums.stream()
				.map(album -> CompletableFuture.supplyAsync(albumWithCoverSupplier(album, coverFinder), executor))
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
		
		return Optional.empty();
	}

	private Supplier<Optional<Album>> albumWithCoverSupplier(Album album, CoverArtFindable coverFinder) {
		log.info("Fetching CoverArt for [{}]", album);
		return () -> {
			try {
				Optional<CoverArt> oArt = coverFinder.findCoverArt(album);
				// If it exists:
				if(oArt.isPresent()) {
					Album richAlbum = new Album.Builder()
							.artistName(album.getArtistName())
							.songName(album.getSongName())
							.name(album.getName())
							.coverArt(oArt)
							.mbid(album.getMbid().get())
							.status(album.getStatus())
							.build();
					log.info("Album with Cover found [{}]", richAlbum);
					return Optional.of(richAlbum);
				}
			} catch (IOException e) {
				log.info("Cover not found for [{}]. Error={}", album, e.getMessage());
				LoggerUtils.logDebug(log, () -> "Cover not found for " + album, e);
			}
			
			return Optional.empty();
		};
	}
}
