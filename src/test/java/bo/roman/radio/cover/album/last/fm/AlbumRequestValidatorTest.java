package bo.roman.radio.cover.album.last.fm;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import bo.roman.radio.utilities.RequestValidator.RequestStatus;

public class AlbumRequestValidatorTest {
	
	@Before
	public void setUp() {
		// Clean Request Checker
		AlbumRequestValidator.getRequestStatus("test", "test");
	}
	
	@Test
	public void testRepeatedSongArtist() {
		String s = "s";
		String a = "a";
		
		Assert.assertEquals(AlbumRequestValidator.getRequestStatus(s, a), RequestStatus.VALID);
		
		// Repeat the song/artist request
		Assert.assertEquals(AlbumRequestValidator.getRequestStatus(s, a), RequestStatus.REPEATED);

		// Repeat the song/artist request
		Assert.assertEquals(AlbumRequestValidator.getRequestStatus(s, a), RequestStatus.REPEATED);
	}
	
	@Test
	public void testRepeatedSongArtistAfterTimeout() throws InterruptedException {
		String s = "s";
		String a = "a";
		
		Assert.assertEquals(AlbumRequestValidator.getRequestStatus(s, a), RequestStatus.VALID);
		
		// New song/artist request
		String ns = "ns";
		String na = "na";
		Assert.assertEquals(AlbumRequestValidator.getRequestStatus(ns, na), RequestStatus.VALID);

		// Repeat the song/artist request
		Assert.assertEquals(AlbumRequestValidator.getRequestStatus(ns, na), RequestStatus.REPEATED);
	}
	
	@Test
	public void testNewSongArtist() {
		String s = "x";
		String a = "y";
		Assert.assertEquals(AlbumRequestValidator.getRequestStatus(s, a), RequestStatus.VALID);
		
		// New song/artist request
		String ns = "ns";
		String na = "na";
		Assert.assertEquals(AlbumRequestValidator.getRequestStatus(ns, na), RequestStatus.VALID);
	}
	
	@Test
	public void testNullSongArtist() {
		Assert.assertEquals(RequestStatus.INVALID, AlbumRequestValidator.getRequestStatus(null, null));
	}
	
	@Test
	public void testEmptySongArtist() {
		Assert.assertEquals(RequestStatus.INVALID, AlbumRequestValidator.getRequestStatus("  ", ""));
	}
	
	@Test
	public void testOnlySong() {
		Assert.assertEquals(RequestStatus.INVALID, AlbumRequestValidator.getRequestStatus("testSong", ""));
	}
	
	@Test
	public void testOnlyArtist() {
		Assert.assertEquals(RequestStatus.INVALID, AlbumRequestValidator.getRequestStatus("", "testSong"));
	}
	

}
