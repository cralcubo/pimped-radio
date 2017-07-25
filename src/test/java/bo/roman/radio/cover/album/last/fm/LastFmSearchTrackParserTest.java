package bo.roman.radio.cover.album.last.fm;

import java.io.IOException;
import java.net.URI;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import bo.roman.radio.cover.model.Album;
import bo.roman.radio.cover.model.CoverArt;

public class LastFmSearchTrackParserTest {
	private final static String ST_INBLOOM = LastFmTestsUtils.RESOURCES_PATH + "searchTrack_InBloom.json";

	private Album oneTrackOnly;

	@Before
	public void setUp() throws IOException {
		oneTrackOnly = LastFmParser.parseSearchTrack(LastFmTestsUtils.getJsonResponse(ST_INBLOOM)).get(0);
	}

	@Test
	public void testParseArtist() {
		Assert.assertEquals("Nirvana", oneTrackOnly.getArtistName());
	}

	@Test
	public void testParseSong() {
		Assert.assertEquals("In Bloom", oneTrackOnly.getSongName());
	}

	@Test
	public void testParseAlbum() {
		Assert.assertEquals("In Bloom", oneTrackOnly.getAlbumName());
	}

	@Test
	public void testParseImageLarge() {
		Assert.assertEquals("https://lastfm-img2.akamaized.net/i/u/300x300/495cd263c78c46efafecea01685a4734.png",
				oneTrackOnly.getCoverArt().flatMap(CoverArt::getLargeUri).map(URI::toString).get());
	}
	
	@Test
	public void testParseImageMedium() {
		Assert.assertEquals("https://lastfm-img2.akamaized.net/i/u/174s/495cd263c78c46efafecea01685a4734.png",
				oneTrackOnly.getCoverArt().flatMap(CoverArt::getMediumUri).map(URI::toString).get());
	}
	
	@Test
	public void testParseImageSmall() {
		Assert.assertEquals("https://lastfm-img2.akamaized.net/i/u/64s/495cd263c78c46efafecea01685a4734.png",
				oneTrackOnly.getCoverArt().flatMap(CoverArt::getSmallUri).map(URI::toString).get());
	}

	@Test
	public void testParseSearchTrack() throws IOException {
		String jsonResponse = LastFmTestsUtils.getJsonResponse(ST_INBLOOM);
		List<Album> albums = LastFmParser.parseSearchTrack(jsonResponse);

		// assertions
		// number of expected albums
		Assert.assertEquals("The number of expected albums is incorrect.", 10, albums.size());
	}

}
