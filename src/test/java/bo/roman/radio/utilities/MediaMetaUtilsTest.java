package bo.roman.radio.utilities;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import bo.roman.radio.cover.model.Song;
import uk.co.caprica.vlcj.player.MediaMeta;

@RunWith(MockitoJUnitRunner.class)
public class MediaMetaUtilsTest {
	
	@Mock
	private MediaMeta mediaMeta;

	@Test
	public void testNowPlaying() {
		String song = "Escape me";
		String artist = "Tiësto";
		String nowPlaying = String.format("%s - %s", artist, song);
		doTestNowPlaying(nowPlaying, song, artist);
	}
	
	@Test
	public void testNowPlaying_spaces() {
		String song = "aSong";
		String artist = "anArtist";
		String nowPlaying = String.format("  %s   -   %s   ", artist, song);
		doTestNowPlaying(nowPlaying, song, artist);
	}
	
	@Test
	public void testNowPlaying_strangeCharacters() {
		String song = "sSong-with-strange #characters";
		String artist = "anArtist";
		String nowPlaying = String.format("%s - %s", artist, song);
		doTestNowPlaying(nowPlaying, song, artist);
	}
	
	@Test
	public void testSongArtist() {
		String song = "aSong";
		String artist = "anArtist";
		doTestSongArtist(song, artist);
	}
	
	@Test
	public void testSongArtist_spaces() {
		String song = " aSong   ";
		String artist = " anArtist ";
		doTestSongArtist(song, artist);
	}
	
	/* *** Utilities *** */
	
	private void doTestSongArtist(String song, String artist) {
		when(mediaMeta.getTitle()).thenReturn(song);
		when(mediaMeta.getArtist()).thenReturn(artist);
		
		Optional<Song> optSong = MediaMetaUtils.buildSong(mediaMeta);
		
		assertThat(optSong.get().getName(), is(equalTo(song.trim())));
		assertThat(optSong.get().getArtist(), is(equalTo(artist.trim())));
	}
	
	private void doTestNowPlaying(String nowPlaying, String expectedSong, String expectedArtist) {
		when(mediaMeta.getTitle()).thenReturn(null);
		when(mediaMeta.getArtist()).thenReturn(null);
		
		when(mediaMeta.getNowPlaying()).thenReturn(nowPlaying);
		
		Optional<Song> optSong = MediaMetaUtils.buildSong(mediaMeta);
		
		assertThat(optSong.get().getName(), is(equalTo(expectedSong.trim())));
		assertThat(optSong.get().getArtist(), is(equalTo(expectedArtist.trim())));
	}

}
