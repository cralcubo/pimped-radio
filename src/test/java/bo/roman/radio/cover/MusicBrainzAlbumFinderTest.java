package bo.roman.radio.cover;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.musicbrainz.controller.Recording;
import org.musicbrainz.model.entity.RecordingWs2;
import org.musicbrainz.model.entity.ReleaseWs2;
import org.musicbrainz.model.searchresult.RecordingResultWs2;

import bo.roman.radio.cover.model.Album;

@RunWith(MockitoJUnitRunner.class)
public class MusicBrainzAlbumFinderTest {
	private static final int LIMIT = 5;
	private AlbumFindable finder;
	
	private String testSong = "In Bloom";
	private String testArtist = "Nirvana";
	
	private String testQuery = String.format("\"%s\" AND artist:\"%s\"", testSong, testArtist);
	
	@Mock
	private Recording recording;

	@Before
	public void setUp() {
		finder = new MusicBrainzAlbumFinder(LIMIT, recording);
	}
	
	/* ***Tests*** */

	@Test
	public void testAlbumLimitSize() {
		List<Album> albums = doRunAlbumFinder();
		assertThat(albums, hasSize(LIMIT));
	}
	
	@Test
	public void testFilterOnlyOfficialReleases() {
		List<Album> albums = doRunAlbumFinder();
		List<String> officialAlbums = albums.stream().map(Album::getStatus).collect(Collectors.toList());
		officialAlbums.forEach(o -> assertThat(o, equalTo("Official")));
	}
	
	@Test
	public void testFilterCredits() {
		List<Album> albums = doRunAlbumFinder();
		List<String> credits = albums.stream().map(Album::getCredits).collect(Collectors.toList());
		credits.forEach(c -> assertTrue(c.equals("Nirvana") || c.equals("")));
	}
	
	@Test
	public void testAlbumPriority() {
		List<Album> albums = doRunAlbumFinder().stream().limit(3).collect(Collectors.toList());
		
		String[] titles = {"Nevermind", "Nevermind", "Nevermind"};      
		String[] credits = {"Nirvana", "", ""};
		String[] statuses = {"Official", "Official", "Official"};      
		String[] ids = {"3", "5", "6"};
		List<Album> expectedAlbums = entitiesGenerator(titles, credits, statuses, ids, (t,c,s,id) -> new Album.Builder().title(t).credits(c).status(s).mbid(id).build());
		
		for(int i = 0; i < albums.size(); i ++) {
			assertThat(albums.get(i), equalTo(expectedAlbums.get(i)));
		}		
	}
	
	/* ***Utilities*** */
	
	private List<Album> doRunAlbumFinder() {
		List<RecordingResultWs2> recordigs = recordingsFactory();
		when(recording.getFullSearchResultList()).thenReturn(recordigs);
		List<Album> albums = finder.findAlbums(testSong, testArtist);
		verify(recording, times(1)).search(testQuery);
		
		return albums;
	}

	private List<RecordingResultWs2> recordingsFactory() {
		RecordingResultWs2 r1 = new RecordingResultWs2();
		RecordingWs2 rec1 = new RecordingWs2();
		
		// Releases info
		String[] titles = {"The best of Nirvana", "Nevermind Underground", "Nevermind", "Unplugged", "Nevermind", "Nevermind", "The best of the 90s", "GTA soundtrack"};      
		String[] credits = {"", "Nirvana", "Nirvana", "Nirvana", "", "", "Various artist", "Varios artistas"};
		String[] statuses = {"Official", "Bottleg", "Official", "Official", "Official", "Official", "Official", ""};      
		String[] ids = {"1", "2", "3", "4", "5", "6", "7", "8"};
		
		List<ReleaseWs2> releases = entitiesGenerator(titles, credits, statuses, ids, (t, c, s, id) -> {
			ReleaseWs2 rel = Mockito.mock(ReleaseWs2.class);
			when(rel.getTitle()).thenReturn(t);
			when(rel.getArtistCreditString()).thenReturn(c);
			when(rel.getStatus()).thenReturn(s);
			when(rel.getId()).thenReturn(id);
			return rel;
		});

		rec1.setReleases(releases);
		r1.setRecording(rec1);

		return Arrays.asList(r1);
	}
	
	
	private <E> List<E> entitiesGenerator(String[] titles, String[] credits, String[] statuses, String[] ids, SuperFunction<String, E> f) {
		List<E> list = new ArrayList<>();
		for(int i = 0; i < titles.length; i++) {
			String t = titles[i];
			String c = credits[i];
			String s = statuses[i];
			String id = ids[i];
			E e = f.apply(t, c, s, id);
			list.add(e);
		}
		
		return list;
	}
	
	@FunctionalInterface
	private interface SuperFunction<S, R> {
		R apply(S a, S b, S c, S d);
	}

}
