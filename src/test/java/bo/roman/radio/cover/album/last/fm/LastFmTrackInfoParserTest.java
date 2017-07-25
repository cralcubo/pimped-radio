package bo.roman.radio.cover.album.last.fm;

import java.io.IOException;
import java.net.URI;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import bo.roman.radio.cover.model.Album;
import bo.roman.radio.cover.model.CoverArt;

public class LastFmTrackInfoParserTest {
	private final static String TI_INBLOOM = LastFmTestsUtils.RESOURCES_PATH + "trackInfo_InBloom.json";
	
	private Album oneTrackOnly;

	@Before
	public void setUp() throws IOException {
		oneTrackOnly = LastFmParser.parseTrackInfo(LastFmTestsUtils.getJsonResponse(TI_INBLOOM));
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
		Assert.assertEquals("Nevermind", oneTrackOnly.getAlbumName());
	}

	@Test
	public void testParseImageLarge() {
		Assert.assertEquals("https://lastfm-img2.akamaized.net/i/u/300x300/fb27b5245fb7444ac4fd8cad6f0374e4.png",
				oneTrackOnly.getCoverArt().flatMap(CoverArt::getLargeUri).map(URI::toString).get());
	}
	
	@Test
	public void testParseImageMedium() {
		Assert.assertEquals("https://lastfm-img2.akamaized.net/i/u/174s/fb27b5245fb7444ac4fd8cad6f0374e4.png",
				oneTrackOnly.getCoverArt().flatMap(CoverArt::getMediumUri).map(URI::toString).get());
	}
	
	@Test
	public void testParseImageSmall() {
		Assert.assertEquals("https://lastfm-img2.akamaized.net/i/u/64s/fb27b5245fb7444ac4fd8cad6f0374e4.png",
				oneTrackOnly.getCoverArt().flatMap(CoverArt::getSmallUri).map(URI::toString).get());
	}
}
