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
		doTestNowPlaying(nowPlaying, song, "Tiesto");
	}
	
	@Test
	public void testNoArtist() {
		String song = "a big song title without aritst";
		String nowPlaying = song;
		doTestNowPlaying(nowPlaying, song, "");
	}
	
	@Test
	public void testNowPlaying_spaces() {
		String song = "aSong";
		String artist = "anArtist";
		String nowPlaying = String.format("  %s   -   %s   ", artist, song);
		doTestNowPlaying(nowPlaying, song, artist);
	}
	
	@Test
	public void testNowPlaying_dashInNames() {
		String song = "aSong-with-dash";
		String artist = "anArtist-name";
		String nowPlaying = String.format("%s - %s", artist, song);
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
	public void testNowPlaying_removeExtraInfo() {
		String song = "sSong (test edition) ft. test";
		String artist = "anArtist";
		String nowPlaying = String.format("%s - %s", artist, song);
		doTestNowPlaying(nowPlaying, "sSong", artist);
	}
	
	@Test
	public void testNowPlaying_removeExtraInfo1() {
		String song = "sSong [test (xxx) edition] (just a test)";
		String artist = "anArtist";
		String nowPlaying = String.format("%s - %s", artist, song);
		doTestNowPlaying(nowPlaying, "sSong", artist);
	}
	
	@Test
	public void testSongArtist() {
		String song = "aSong";
		String artist = "anArtist";
		doTestSongArtist(song, artist);
	}
	
	@Test
	public void testSongArtist_removeExtraInfo() {
		String song = "aSong (extra info)";
		String artist = "anArtist";
		when(mediaMeta.getTitle()).thenReturn(song);
		when(mediaMeta.getArtist()).thenReturn(artist);
		
		Optional<Song> optSong = MediaMetaUtils.buildSong(mediaMeta);
		
		assertThat(optSong.get().getName(), is(equalTo("aSong")));
		assertThat(optSong.get().getArtist(), is(equalTo(artist)));
	}
	
	@Test
	public void testSongArtist_removeExtraInfo1() {
		String song = "aSong [extra info]";
		String artist = "anArtist";
		when(mediaMeta.getTitle()).thenReturn(song);
		when(mediaMeta.getArtist()).thenReturn(artist);
		
		Optional<Song> optSong = MediaMetaUtils.buildSong(mediaMeta);
		
		assertThat(optSong.get().getName(), is(equalTo("aSong")));
		assertThat(optSong.get().getArtist(), is(equalTo(artist)));
	}
	
	@Test
	public void testSongArtist_removeFeaturing() {
		String song = "aSong ft. anArtist";
		String artist = "anArtist feat. anArtis";
		
		when(mediaMeta.getTitle()).thenReturn(song);
		when(mediaMeta.getArtist()).thenReturn(artist);
		
		Optional<Song> optSong = MediaMetaUtils.buildSong(mediaMeta);
		
		assertThat(optSong.get().getName(), is(equalTo("aSong")));
		assertThat(optSong.get().getArtist(), is(equalTo("anArtist")));
	}
	
	@Test
	public void testSongArtist_removeFeaturing2() {
		String song = "feature FEAT. anArtist";
		String artist = "ft. FEATURING anArtis";
		
		when(mediaMeta.getTitle()).thenReturn(song);
		when(mediaMeta.getArtist()).thenReturn(artist);
		
		Optional<Song> optSong = MediaMetaUtils.buildSong(mediaMeta);
		
		assertThat(optSong.get().getName(), is(equalTo("feature")));
		assertThat(optSong.get().getArtist(), is(equalTo("ft.")));
	}
	@Test
	public void testSongArtist_removeFeaturing3() {
		String song = "aSong f. anotherArtist";
		String artist = "anArtist f/anotherArtis";
		
		when(mediaMeta.getTitle()).thenReturn(song);
		when(mediaMeta.getArtist()).thenReturn(artist);
		
		Optional<Song> optSong = MediaMetaUtils.buildSong(mediaMeta);
		
		assertThat(optSong.get().getName(), is(equalTo("aSong")));
		assertThat(optSong.get().getArtist(), is(equalTo("anArtist")));
	}
	
	@Test
	public void testSongArtist_spaces() {
		String song = " aSong   ";
		String artist = " anArtist ";
		doTestSongArtist(song, artist);
	}
	
	@Test public void testSongArtist_htmlEncoded() {
		String song = "&#35;song";
		String artist = "&lt;Fran&ccedil;ais&gt;";
		when(mediaMeta.getTitle()).thenReturn(song);
		when(mediaMeta.getArtist()).thenReturn(artist);
		Optional<Song> optSong = MediaMetaUtils.buildSong(mediaMeta);
		
		assertThat(optSong.isPresent(), is(true));
		Song songObj = optSong.get();
		String songExpected = "#song";
		String artistExpected = "<Francais>";
		assertThat(songObj.getArtist(), is(equalTo(artistExpected)));
		assertThat(songObj.getName(), is(equalTo(songExpected)));
	}
	
	@Test
	public void testParseRadioName_dash() {
		String radioName = "Radio Paradise - DJ-mixed modern & classic rock, world, electronica & more - info: radioparadise.com";
		doTestParseRadioName(radioName, Optional.of("Radio Paradise"));
	}
	
	@Test
	public void testParseRadioName_dashPartOfName() {
		String radioName = "ai-radio.org   -   video game music and many other more...flac 44.KB airadio";
		doTestParseRadioName(radioName, Optional.of("ai-radio.org"));
	}
	
	@Test
	public void testParseRadioName_WithAmpersands() {
		String radioName = "Radio Peace & Love";
		doTestParseRadioName(radioName, Optional.of("Radio Peace & Love"));
	}
	
	@Test
	public void testParseRadioName_escapeHtmlEncode() {
		String radioName = "Best Radio &#35;radio";
		doTestParseRadioName(radioName, Optional.of("Best Radio #radio"));
	}
	
	@Test
	public void testParseRadioName_noMetaTitle() {
		doTestParseRadioName("", Optional.empty());
	}
	
	@Test 
	public void testParseRadioName_nullMetaTitle() {
		doTestParseRadioName(null, Optional.empty());
	}
	
	/* *** Utilities *** */
	
	private void doTestParseRadioName(String radioName, Optional<String> radioNameExpected) {
		// Prepare Mock
		when(mediaMeta.getTitle()).thenReturn(radioName);
		// Run method to test
		Optional<String> nameParsed = MediaMetaUtils.findRadioName(mediaMeta);
		// Asserts
		assertThat(nameParsed, is(equalTo(radioNameExpected)));
	}
	
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
