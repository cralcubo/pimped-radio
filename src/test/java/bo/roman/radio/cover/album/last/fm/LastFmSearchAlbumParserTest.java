package bo.roman.radio.cover.album.last.fm;

import java.io.IOException;
import java.net.URI;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import bo.roman.radio.cover.model.Album;
import bo.roman.radio.cover.model.CoverArt;

public class LastFmSearchAlbumParserTest {
	private final static String SA_INBLOOM = LastFmTestsUtils.RESOURCES_PATH + "searchAlbum_InBloom.json";
	
	private Album oneAlbumOnly;
	
	@Before
	public void setUp() throws IOException {
		oneAlbumOnly = LastFmParser.parseSearchAlbum(LastFmTestsUtils.getJsonResponse(SA_INBLOOM)).get(0);
	}
	
	@Test
	public void testParseArtist() {
		Assert.assertEquals("Nirvana", oneAlbumOnly.getArtistName());
	}

	@Test
	public void testParseAlbum() {
		Assert.assertEquals("In Bloom [Single]", oneAlbumOnly.getAlbumName());
	}

	@Test
	public void testParseImageLarge() {
		Assert.assertEquals("https://lastfm-img2.akamaized.net/i/u/300x300/b1828995754f4d4394ab166ab2d49cca.png",
				oneAlbumOnly.getCoverArt().flatMap(CoverArt::getLargeUri).map(URI::toString).get());
	}
	
	@Test
	public void testParseImageMedium() {
		Assert.assertEquals("https://lastfm-img2.akamaized.net/i/u/174s/b1828995754f4d4394ab166ab2d49cca.png",
				oneAlbumOnly.getCoverArt().flatMap(CoverArt::getMediumUri).map(URI::toString).get());
	}
	
	@Test
	public void testParseImageSmall() {
		Assert.assertEquals("https://lastfm-img2.akamaized.net/i/u/64s/b1828995754f4d4394ab166ab2d49cca.png",
				oneAlbumOnly.getCoverArt().flatMap(CoverArt::getSmallUri).map(URI::toString).get());
	}

	@Test
	public void testParseSearchAlbum() throws IOException {
		String jsonResponse = LastFmTestsUtils.getJsonResponse(SA_INBLOOM);
		List<Album> albums = LastFmParser.parseSearchAlbum(jsonResponse);

		// assertions
		Assert.assertEquals("The number of expected albums is incorrect.", 10, albums.size());
	}
	
	

}
