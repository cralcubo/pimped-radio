package bo.roman.radio.player;

import static bo.roman.radio.utilities.LoggerUtils.logDebug;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.caprica.vlcj.binding.LibVlc;
import uk.co.caprica.vlcj.component.AudioMediaPlayerComponent;
import uk.co.caprica.vlcj.discovery.NativeDiscovery;
import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerEventAdapter;

public class RadioPlayer implements RadioPlayerInterface {
	private static final Logger log = LoggerFactory.getLogger(RadioPlayer.class);

	private final MediaPlayer mediaPlayer;
	
	public RadioPlayer() {
		// Help vlcj to find LibVlc native libraries
		if(!new NativeDiscovery().discover()){
			throw new RuntimeException("LibVlc native libraries not found. RadioPlayer is closing.");
		}
		
		logDebug(log, () -> String.format("LibVlc found [%s]. Instantiating a new RadioPlayer.", LibVlc.INSTANCE.libvlc_get_version()));
		AudioMediaPlayerComponent playerComponent = new AudioMediaPlayerComponent();
		mediaPlayer = playerComponent.getMediaPlayer();
	}
	
	@Override
	public void addEventsListener(MediaPlayerEventAdapter eventsAdapter) {
		mediaPlayer.addMediaPlayerEventListener(eventsAdapter);
	}

	@Override
	public void play(String radioStationUrl) {
		log.info("Starting to play stream={}", radioStationUrl);
		mediaPlayer.playMedia(radioStationUrl);
		try {
			Thread.currentThread().join();
		} catch (InterruptedException e) {
			log.error("RadioPlayer was unexpectedly stopped.", e);
		}
	}

	@Override
	public void stop() {
		log.info("Stopping player..."); 
		mediaPlayer.release();
	}

}
