package bo.roman.radio.cover;

import static bo.roman.radio.utilities.StringUtils.exists;
import static bo.roman.radio.utilities.StringUtils.removeBracketsInfo;
import static bo.roman.radio.utilities.StringUtils.removeFeatureInfo;
import static java.util.Optional.empty;
import static java.util.stream.Collectors.toList;

import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bo.roman.radio.cover.album.AlbumComparator;
import bo.roman.radio.cover.album.AlbumFindable;
import bo.roman.radio.cover.album.last.fm.LastFmAlbumFinder;
import bo.roman.radio.cover.model.Album;
import bo.roman.radio.cover.model.CoverArt;
import bo.roman.radio.cover.model.Radio;
import bo.roman.radio.cover.station.CacheLogoUtil;
import bo.roman.radio.cover.station.RadioStationFindable;
import bo.roman.radio.cover.station.StationFinderRequestValidator;
import bo.roman.radio.cover.station.tunein.TuneInRadioFinder;
import bo.roman.radio.utilities.LoggerUtils;

class CoverArtManager implements ICoverArtManager {
	private final static Logger log = LoggerFactory.getLogger(CoverArtManager.class);

	private final AlbumFindable albumFinder;
	private final RadioStationFindable radioFinder;

	public CoverArtManager() {
		this(new LastFmAlbumFinder(), new TuneInRadioFinder());
	}

	CoverArtManager(AlbumFindable albumFinder, RadioStationFindable radioFinder) {
		this.albumFinder = albumFinder;
		this.radioFinder = radioFinder;
	}

	@Override
	public Optional<Album> getAlbumWithCover(String song, String artist) {
		if (!exists(song) || !exists(artist)) {
			// We need a song and artist to find an album
			return empty();
		}

		// Clean song and artist from info in brackets or featuring info
		// to have a better Album search.
		LoggerUtils.logDebug(log,
				() -> String.format("Cleaning brackets and feat. info from: [%s] - [%s]", song, artist));
		String cSong = removeFeatureInfo(removeBracketsInfo(song));
		String cArtist = removeFeatureInfo(removeBracketsInfo(artist));
		LoggerUtils.logDebug(log, () -> String.format("Clean info: [%s] - [%s]", cSong, cArtist));

		log.info("Finding Album for [{} - {}]", cSong, cArtist);

		// Get all the albums found in the provider and give priority to the albums name
		// that have the same name as the song that was used to find it.
		final AlbumComparator albumComparator = new AlbumComparator(song, artist);
		List<Album> allAlbums = albumFinder.findAlbums(cSong, cArtist).stream()//
				.sorted(albumComparator)//
				.collect(toList());

		log.info("{} albums found.", allAlbums.size());
		if (log.isDebugEnabled()) {
			allAlbums.forEach(a -> log.debug(a.toString()));
		}

		if (allAlbums.isEmpty()) {
			return Optional.empty();
		}

		Comparator<Album> proportionsComparator = new CoverArtProportionsComparator();
		Optional<Album> albumFound = allAlbums.stream().min(proportionsComparator);
		log.info("Match for Album found {}", albumFound);

		return albumFound;
	}

	@Override
	public Optional<Radio> getRadioWithLogo(String radioName) {
		if (!exists(radioName)) {
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
		Optional<Radio> oRadio = findRadio(radioName);
		if (oRadio.isPresent()) {
			// If the logo of the radio was found cache it
			CacheLogoUtil.cacheRadioLogo(radioName, oRadio.flatMap(Radio::getLogoUri));
			return oRadio;
		}

		// no radio found
		log.info("No info found for radio:" + radioName);
		return Optional.empty();
	}

	private Optional<Radio> findRadio(String radioName) {
		switch (StationFinderRequestValidator.validate(radioName)) {
		case VALID:
			return radioFinder.findRadioStation(radioName);
		case REPEATED:
			return radioFinder.getCachedRadio();
		default:
			// No valid request
			LoggerUtils.logDebug(log, () -> "Invalid radio name provided:" + radioName);
			return Optional.empty();
		}
	}

	private class CoverArtProportionsComparator implements Comparator<Album> {

		@Override
		public int compare(Album a1, Album a2) {
			Optional<CoverArt> oCA1 = a1.getCoverArt();
			Optional<CoverArt> oCA2 = a2.getCoverArt();

			if (oCA1.isPresent() && oCA2.isPresent()) {
				float r1 = Math.abs(oCA1.get().getMaxWidth() * 1.0f / oCA1.get().getMaxHeight() - 1);
				float r2 = Math.abs(oCA2.get().getMaxWidth() * 1.0f / oCA2.get().getMaxHeight() - 1);
				return Float.compare(r1, r2);
			} else if (oCA1.isPresent() && !oCA2.isPresent()) {
				return 1;
			} else if (!oCA1.isPresent() && oCA2.isPresent()) {
				return -1;
			} else {
				return 0;
			}
		}

	}

}
