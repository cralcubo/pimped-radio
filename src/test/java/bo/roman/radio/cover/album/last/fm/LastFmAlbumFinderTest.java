package bo.roman.radio.cover.album.last.fm;

import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.core.classloader.annotations.SuppressStaticInitializationFor;
import org.powermock.modules.junit4.PowerMockRunner;

import bo.roman.radio.cover.model.Album;
import bo.roman.radio.cover.model.CoverArt;
import bo.roman.radio.utilities.HttpUtils;
import bo.roman.radio.utilities.ReflectionUtils;
import bo.roman.radio.utilities.RequestValidator.RequestStatus;
import bo.roman.radio.utilities.SecretFileProperties;
import bo.roman.radio.utilities.StringUtils;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ HttpUtils.class, SecretFileProperties.class, LastFmRequestsManager.class, AlbumRequestValidator.class })
@SuppressStaticInitializationFor("bo.roman.radio.utilities.SecretFileProperties")
public class LastFmAlbumFinderTest {
	private static final String ST_INBLOOM = LastFmTestsUtils.RESOURCES_PATH + "searchTrack_InBloom.json";
	private static final String ST_BONAPPETIT = LastFmTestsUtils.RESOURCES_PATH + "searchTrack_BonAppetit.json";

	private final static String TI_INBLOOM = LastFmTestsUtils.RESOURCES_PATH + "trackInfo_InBloom.json";
	private static final String TI_KASKADE = LastFmTestsUtils.RESOURCES_PATH + "trackInfo_Kaskade.json";
	private static final String TI_BONAPPETIT = LastFmTestsUtils.RESOURCES_PATH + "trackInfo_BonAppetit.json";

	private static final String AI_KASKADE = LastFmTestsUtils.RESOURCES_PATH + "albumInfo_Kaskade.json";

	private static final String AISONG_BONAPPETIT = LastFmTestsUtils.RESOURCES_PATH + "albumInfo_SongBonAppetit.json";

	private static final String LASTFM_ERROR = LastFmTestsUtils.RESOURCES_PATH + "lastFmError.json";

	private final String SEARCHTRACK_QUERY;
	private final String TRACKINFO_QUERY;
	private final String ALBUMINFO_QUERY;

	private LastFmAlbumFinder finder;

	public LastFmAlbumFinderTest() throws Exception {
		PowerMockito.mockStatic(SecretFileProperties.class);
		PowerMockito.when(SecretFileProperties.get("lastfm.apiKey")).thenReturn("aKey");

		finder = new LastFmAlbumFinder();
		SEARCHTRACK_QUERY = (String) ReflectionUtils.getPrivateConstant(finder, "SEARCHTRACK_QUERY");
		TRACKINFO_QUERY = (String) ReflectionUtils.getPrivateConstant(finder, "TRACKINFO_QUERY");
		ALBUMINFO_QUERY = (String) ReflectionUtils.getPrivateConstant(finder, "ALBUMINFO_QUERY");
	}

	@Before
	public void setUp() throws InterruptedException {
		PowerMockito.mockStatic(HttpUtils.class);
		PowerMockito.mockStatic(LastFmRequestsManager.class);
		PowerMockito.mockStatic(AlbumRequestValidator.class);
	}

	@Test
	public void testFindNoSongNoTrack() {
		String song = null;
		String artist = "";
		PowerMockito.when(AlbumRequestValidator.getRequestStatus(song, artist)).thenReturn(RequestStatus.INVALID);
		Assert.assertEquals("No Albums were expected.", Collections.emptyList(), finder.findAlbums(song, artist));
	}

	@Test
	public void testFindOneAlbumFeaturingArtistFeaturingSong() throws Exception {
		String song = "In Bloom feat. Metallica";
		String artist = "Nirvana featuring Korn";

		String cleanSong = "In Bloom";
		String cleanArtist = "Nirvana";

		PowerMockito.when(AlbumRequestValidator.getRequestStatus(song, artist)).thenReturn(RequestStatus.VALID);
		String requestQuery = String.format(TRACKINFO_QUERY, cleanSong, cleanArtist);
		mockRequest(requestQuery, TI_INBLOOM);
		List<Album> albums = finder.findAlbums(song, artist);

		// Assert
		Assert.assertEquals("Just one album expected.", 1, albums.size());
		assertIsValidAlbum(albums.get(0));
	}

	@Test
	public void testFindOneAlbum() throws Exception {
		String song = "In Bloom";
		String artist = "Nirvana";

		PowerMockito.when(AlbumRequestValidator.getRequestStatus(song, artist)).thenReturn(RequestStatus.VALID);
		String requestQuery = String.format(TRACKINFO_QUERY, song, artist);
		mockRequest(requestQuery, TI_INBLOOM);
		List<Album> albums = finder.findAlbums(song, artist);

		// Assert
		Assert.assertEquals("Just one album expected.", 1, albums.size());
		assertIsValidAlbum(albums.get(0));
	}

	@Test
	public void testAlbumNameNoCover() throws IOException {
		String song = "I remember";
		String artist = "Kaskade";
		String album = "Strobelite Seduction";

		PowerMockito.when(AlbumRequestValidator.getRequestStatus(song, artist)).thenReturn(RequestStatus.VALID);
		String trackInfoQuery = String.format(TRACKINFO_QUERY, song, artist);
		mockRequest(trackInfoQuery, TI_KASKADE);
		String albumInfoQuery = String.format(ALBUMINFO_QUERY, album, artist);
		mockRequest(albumInfoQuery, AI_KASKADE);

		List<Album> albums = finder.findAlbums(song, artist);

		// Assertions
		Assert.assertEquals("Just one album expected", 1, albums.size());
		assertIsValidAlbum(albums.get(0));
	}

	@Test
	public void testNoAlbumMultipleTracks() throws IOException {
		String song = "Bon Appétit";
		String artist = "Katy Perry";

		PowerMockito.when(AlbumRequestValidator.getRequestStatus(song, artist)).thenReturn(RequestStatus.VALID);
		String trackInfoQuery = String.format(TRACKINFO_QUERY, song, artist);
		mockRequest(trackInfoQuery, TI_BONAPPETIT);
		String searchTrackQuery = String.format(SEARCHTRACK_QUERY, song, artist);
		mockRequest(searchTrackQuery, ST_BONAPPETIT);

		List<Album> albums = finder.findAlbums(song, artist);

		// Assert
		Assert.assertEquals("The number of albums expected failed.", 10, albums.size());
		albums.forEach(this::assertIsValidAlbum);
	}

	@Test
	public void testSearchSongAsAlbum() throws IOException {
		String song = "Bon Appétit";
		String artist = "Katy Perry";

		PowerMockito.when(AlbumRequestValidator.getRequestStatus(song, artist)).thenReturn(RequestStatus.VALID);
		String albumInfo = String.format(ALBUMINFO_QUERY, song, artist);
		mockRequest(albumInfo, AISONG_BONAPPETIT);

		List<Album> albums = finder.findAlbums(song, artist);

		// Assert
		Assert.assertEquals("Just one album expected.", 1, albums.size());
		assertIsValidAlbum(albums.get(0));
	}

	@Test
	public void testNoAlbumsNoTracks() throws IOException {
		String song = "Bon Appétit";
		String artist = "Katy Perry";

		PowerMockito.when(AlbumRequestValidator.getRequestStatus(song, artist)).thenReturn(RequestStatus.VALID);
		String albumInfoRequest = String.format(ALBUMINFO_QUERY, song, artist);
		mockRequest(albumInfoRequest, LASTFM_ERROR);
		String songInfoReq = String.format(TRACKINFO_QUERY, song, artist);
		mockRequest(songInfoReq, LASTFM_ERROR);
		String searchSongReq = String.format(SEARCHTRACK_QUERY, song, artist);
		mockRequest(searchSongReq, LASTFM_ERROR);

		List<Album> albums = finder.findAlbums(song, artist);

		// Assertions
		Assert.assertEquals("No Albums expected", Collections.emptyList(), albums);
	}

	@Test
	public void testJustAlbumsWithNameAndCover() throws IOException {
		String artist = "Nirvana";
		String song = "In Bloom";

		PowerMockito.when(AlbumRequestValidator.getRequestStatus(song, artist)).thenReturn(RequestStatus.VALID);
		String albumInfoRequest = String.format(ALBUMINFO_QUERY, song, artist);
		mockRequest(albumInfoRequest, LASTFM_ERROR);
		String songInfoReq = String.format(TRACKINFO_QUERY, song, artist);
		mockRequest(songInfoReq, LASTFM_ERROR);

		String searchSongReq = String.format(SEARCHTRACK_QUERY, song, artist);
		mockRequest(searchSongReq, ST_INBLOOM);

		List<Album> albums = finder.findAlbums(song, artist);

		// Assertions
		Assert.assertEquals(8, albums.size());
		albums.forEach(a -> assertIsValidAlbum(a));
	}

	/*
	 * Utilities
	 */

	private void mockRequest(String query, String filePathResponse) throws IOException {
		PowerMockito.when(LastFmRequestsManager.enableRequest()).thenReturn(true);
		PowerMockito.when(HttpUtils.doGet(query)).thenReturn(LastFmTestsUtils.getJsonResponse(filePathResponse));
	}

	private void assertIsValidAlbum(Album album) {
		Assert.assertTrue("Album name expected.", StringUtils.exists(album.getAlbumName()));
		Assert.assertTrue("Song name expected.", StringUtils.exists(album.getSongName()));
		Assert.assertTrue("Artist name expected.", StringUtils.exists(album.getArtistName()));
		// CoverArt
		Assert.assertTrue("CoverArt expected.", album.getCoverArt().isPresent());
		Assert.assertTrue("Large cover expected",
				StringUtils.exists(album.getCoverArt().flatMap(CoverArt::getLargeUri).map(URI::toString).get()));
	}

}
