package bo.roman.radio.player;

import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.musicbrainz.controller.Recording;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bo.roman.radio.cover.AlbumFindable;
import bo.roman.radio.cover.CoverArtArchiveFinder;
import bo.roman.radio.cover.CoverArtFindable;
import bo.roman.radio.cover.CoverArtManager;
import bo.roman.radio.cover.MBAlbumFinder;
import bo.roman.radio.cover.RadioCoverInterface;
import bo.roman.radio.cover.model.Album;
import bo.roman.radio.cover.model.Radio;
import bo.roman.radio.cover.model.Song;
import bo.roman.radio.utilities.LoggerUtils;
import bo.roman.radio.utilities.MediaMetaUtils;
import uk.co.caprica.vlcj.player.MediaMeta;
import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerEventAdapter;

public class RadioPlayerTest {
	private final static Logger log = LoggerFactory.getLogger(RadioPlayerTest.class);
	private static final String LOCAL_SONG = "/Users/christian/Desktop/TestMusic/test.mp3";
	private static final String RADIO_STREAM = "http://stream-tx3.radioparadise.com/aac-128";
	private RadioPlayer player;

	@Before
	public void setUp() {
		player = new RadioPlayer(new RadioPlayerEventListener());
	}

	@Test
	public void testPlayerInStream() {
		// Create a Thread to play the song
		Thread playerThread = new Thread(() -> player.play(RADIO_STREAM));
		// Start the RadioPlayer:
		playerThread.start();

		// Wait for 30 seconds and stop
		try {
			// Forever
			playerThread.join();
			
			log.info("Waiting 5 minutes before stopping it...");
			Thread.sleep(5 * 60 * 1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// Stop player
		player.stop();
	}

	private static class RadioPlayerEventListener extends MediaPlayerEventAdapter {
		private final static Logger log = LoggerFactory.getLogger(RadioPlayerEventListener.class);

		@Override
		public void mediaMetaChanged(MediaPlayer mediaPlayer, int metaType) {
			log.info("Media Meta Changed[metaType={}]", metaType);
			MediaMeta meta = mediaPlayer.getMediaMeta();
			System.out.println(".:.MediaMeta=" + meta);
			System.out.println(".:. Album=" + meta.getAlbum() + " - Art=" + meta.getArtworkUrl());
			
			Optional<Song> optSong = MediaMetaUtils.buildSong(meta);
			if(optSong.isPresent()) {
				Song song = optSong.get();
				LoggerUtils.logDebug(log, () -> String.format("Starting a new Thread to retrieve the CoverArt of %s", song));
				// Create Cover Thread
				AlbumFindable albumFinder = new MBAlbumFinder(10, new Recording());
				CoverArtFindable coverFinder = new CoverArtArchiveFinder();
				RadioCoverInterface coverManager = new CoverArtManager(albumFinder, coverFinder);
				Thread coverThread = new Thread(() -> {
					Optional<Album> oRichAlbum = coverManager.getAlbumWithCover(song.getName(), song.getArtist());
					
					if(oRichAlbum.isPresent()) {
						log.info("Album playing {} ", oRichAlbum.get());
					}
					else {
						Optional<Radio> radio = coverManager.getRadioWithCover(meta.getTitle());
						log.info("Radio playing {}", radio.get());
					}
					
				});
				coverThread.start();
			}
			
			meta.release();
		}
	}

}
