package bo.roman.radio.player.listener;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bo.roman.radio.cover.CoverArtManager;
import bo.roman.radio.cover.model.Album;
import bo.roman.radio.cover.model.Radio;
import bo.roman.radio.cover.model.Song;
import bo.roman.radio.player.model.RadioPlayerEntity;

public class RadioInformationFinder {
	private final static Logger log = LoggerFactory.getLogger(RadioInformationFinder.class);
	
	private final CoverArtManager coverManager;
	
	public RadioInformationFinder() {
		this.coverManager = new CoverArtManager();
	}
	
	protected RadioInformationFinder(CoverArtManager coverManager) {
		this.coverManager = coverManager;
	}
	
	public RadioPlayerEntity find(Optional<String> oRadioName, Optional<Song> oSong) {
		log.info("Changed MediaMeta. Radio[{}] and Song[{}]", oRadioName, oSong);
		
		/*
		 * Do an async call to find information about the Radio and the Song
		 */
		// Find the radio
		String radioName = oRadioName.orElse("");
		final CompletableFuture<Optional<Radio>> futureRadio = CompletableFuture.supplyAsync(() -> coverManager.getRadioWithLogo(radioName));

		// Find the Album of the Song
		Song song = oSong.orElseGet(() -> new Song.Builder().build());
		final CompletableFuture<Optional<Album>> futureAlbum = CompletableFuture.supplyAsync(() -> coverManager.getAlbumWithCover(song.getName(), song.getArtist()));

		// Get the info found
		final Optional<Radio> oRadio = futureRadio.join();
		final Optional<Album> oAlbum = futureAlbum.join();

		return new RadioPlayerEntity(oRadio, oSong, oAlbum);
	}
}
