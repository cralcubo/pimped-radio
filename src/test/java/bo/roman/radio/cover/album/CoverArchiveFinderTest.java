package bo.roman.radio.cover.album;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import bo.roman.radio.cover.model.Album;
import bo.roman.radio.cover.model.CoverArt;
import bo.roman.radio.utilities.ImageUtil;
import bo.roman.radio.utilities.ReflectionUtils;

@RunWith(PowerMockRunner.class)
@PrepareForTest(ImageUtil.class)
public class CoverArchiveFinderTest {
	
	private String LARGECOVER_TMPL;
	private String MEDIUMCOVER_TMPL;
	private String SMALLCOVER_TMPL;
	
	private CoverArchiveFinder finder;
	
	@Before
	public void setUp() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		finder = new CoverArchiveFinder();
		LARGECOVER_TMPL = (String) ReflectionUtils.getPrivateConstant(finder, "LARGECOVER_TMPL");
		MEDIUMCOVER_TMPL = (String) ReflectionUtils.getPrivateConstant(finder, "MEDIUMCOVER_TMPL");
		SMALLCOVER_TMPL = (String) ReflectionUtils.getPrivateConstant(finder, "SMALLCOVER_TMPL");
		
		PowerMockito.mockStatic(ImageUtil.class);
	}
	
	@Test
	public void testFindCoverArt() throws IOException, URISyntaxException {
		String albumName = "aName";
		String mbid = "123";
		Album album = new Album.Builder().name(albumName).mbid(mbid).build();
		
		String mediumUri = String.format(MEDIUMCOVER_TMPL, mbid);
		PowerMockito.when(ImageUtil.isBigEnough(mediumUri)).thenReturn(true);
		Optional<CoverArt> oCover = finder.findCoverArt(album);
		
		//Assert
		assertThat(oCover.isPresent(), is(true));
		
		String largeUri = String.format(LARGECOVER_TMPL, mbid);
		String smallUri = String.format(SMALLCOVER_TMPL, mbid);
		CoverArt expected = new CoverArt.Builder()
				.largeUri(largeUri)
				.mediumUri(mediumUri)
				.smallUri(smallUri)
				.build();
		
		assertThat(oCover.get(), is(expected));
	}
	
	@Test
	public void testFindCover_smallCover() throws Exception{
		String albumName = "aName";
		String mbid = "123";
		Album album = new Album.Builder().name(albumName).mbid(mbid).build();
		
		String mediumUri = String.format(MEDIUMCOVER_TMPL, mbid);
		PowerMockito.when(ImageUtil.isBigEnough(mediumUri)).thenReturn(false);
		Optional<CoverArt> oCover = finder.findCoverArt(album);
		
		//Assert
		assertThat(oCover.isPresent(), is(false));
	}
	
	@Test
	public void testFindCover_noAlbum() throws Exception {
		Optional<CoverArt> oCover = finder.findCoverArt(null);
		assertThat(oCover.isPresent(), is(false));
	}
	
	@Test
	public void testFindCover_noMBID() throws Exception {
		String albumName = "aName";
		Album album = new Album.Builder().name(albumName).build();
		Optional<CoverArt> oCover = finder.findCoverArt(album);
		
		assertThat(oCover.isPresent(), is(false));
	}
	
	@SuppressWarnings("unchecked")
	@Test(expected=RuntimeException.class)
	public void testFindCover_wrongUriFormat() throws Exception{
		String albumName = "aName";
		String mbid = "123";
		Album album = new Album.Builder().name(albumName).mbid(mbid).build();
		
		String mediumUri = String.format(MEDIUMCOVER_TMPL, mbid);
		PowerMockito.when(ImageUtil.isBigEnough(mediumUri)).thenThrow(URISyntaxException.class);
		finder.findCoverArt(album);
	}

}
