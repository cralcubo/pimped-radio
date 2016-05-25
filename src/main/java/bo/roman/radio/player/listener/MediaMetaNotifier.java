package bo.roman.radio.player.listener;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bo.roman.radio.cover.ICoverArtManager;
import bo.roman.radio.cover.CoverArtManager;
import bo.roman.radio.cover.model.Album;
import bo.roman.radio.cover.model.Radio;
import bo.roman.radio.cover.model.Song;
import bo.roman.radio.player.model.RadioPlayerEntity;
import bo.roman.radio.utilities.ExecutorUtils;
import bo.roman.radio.utilities.LoggerUtils;
import bo.roman.radio.utilities.MediaMetaUtils;
import uk.co.caprica.vlcj.player.MediaMeta;

public class MediaMetaNotifier implements MediaMetaSubject {
	
	private static final Logger log = LoggerFactory.getLogger(MediaMetaNotifier.class);
	
	private final List<MediaMetaObserver> observers;
	
	private final ICoverArtManager radioCover; 
	
	public MediaMetaNotifier() {
		this(new CoverArtManager());
	}
	
	protected MediaMetaNotifier(ICoverArtManager radioCover) {
		this.radioCover = radioCover;
		observers = new ArrayList<>();
	}
	
	/**
	 * Meta has changed, call the RadioCoverInterface
	 * to find the Radio Logo and the Album that is 
	 * playing Cover Art.
	 * 
	 * Radio Logo and Cover Art are retrieved Asynchronously.
	 * 
	 * @param changedMeta
	 */
	public RadioPlayerEntity metaChanged(MediaMeta changedMeta) {
		LoggerUtils.logDebug(log, () -> "MediaMeta changed=" + changedMeta);
		
		final Optional<String> oRadioName = MediaMetaUtils.findRadioName(changedMeta);
		final Optional<Song> oSong = MediaMetaUtils.buildSong(changedMeta);
		log.info("Changed MediaMeta. Radio[{}] and Song[{}]", oRadioName, oSong);
		
		Executor executor = ExecutorUtils.fixedThreadPoolFactory(2);
		
		/* Do an async call to find information about the Radio and the Song */
		// Find the radio
		String radioName = oRadioName.orElse("");
		final CompletableFuture<Optional<Radio>> futureRadio = CompletableFuture.supplyAsync(() -> radioCover.getRadioWithLogo(radioName), executor);
		
		// Find the Album of the Song
		Song song = oSong.orElse(new Song.Builder().build());
		final CompletableFuture<Optional<Album>> futureAlbum = CompletableFuture.supplyAsync(() -> radioCover.getAlbumWithCover(song.getName(), song.getArtist()), executor);
		
		// Get the info found
		final Optional<Radio> oRadio = futureRadio.join();
		final Optional<Album> oAlbum = futureAlbum.join();
		
		return new RadioPlayerEntity(oRadio, oSong, oAlbum);
	}
	
	/**
	 * When the MediaMeta has changed, trigger
	 * this method to notify all the registered
	 * observers to update the information they
	 * are concerned about.
	 * 
	 */
	@Override
	public void notifyObservers(final MediaMeta meta) {
		RadioPlayerEntity entity = metaChanged(meta);
		log.info("Notifying observers with RadioPlayerEntity[{}]", entity);
		observers.forEach(o -> o.update(entity));
	}

	@Override
	public void registerObserver(MediaMetaObserver o) {
		observers.add(o);
	}

	

}
