package bo.roman.radio.cover.album;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;

import bo.roman.radio.cover.model.Album;
import bo.roman.radio.cover.model.CoverArt;

public class AlbumComparatorTest {
	
	private AlbumComparator comparator;
	private final String song = "Work (Explicit)";
	private final String artist = "Rhianna ft. Drake";
	private final String albumName = "Anti";
	
	@Before
	public void setUp() {
		comparator = new AlbumComparator(song, artist);
	}
	
	
	@Test
	public void testSort_bestSongFirst() {
		CoverArt squareCover = new CoverArt.Builder().maxHeight(500).maxWidth(500).largeUri("http://rect.uri").build();
		
		Album a0 = new Album.Builder().artistName("x band").songName("x song").name("x album").coverArt(Optional.of(squareCover)).build();
		Album a1 = new Album.Builder().artistName(artist).songName("Work").name(albumName).coverArt(Optional.of(squareCover)).build();
		Album a2 = new Album.Builder().artistName(artist).songName("WORK [EXPLICIT]").name(albumName + " (Remastered)").coverArt(Optional.of(squareCover)).build();
		Album a3 = new Album.Builder().artistName(artist).songName("Work (Clean)").name(albumName + " (Unplugged)").coverArt(Optional.of(squareCover)).build();
		Album a4 = new Album.Builder().artistName(artist).songName(song).name(albumName).coverArt(Optional.of(squareCover)).build();
		List<Album> albums = Arrays.asList(a0, a1, a2, a3, a4);
		
		List<Album> sortedAlbums = albums.stream().sorted(comparator).collect(Collectors.toList());
		// Assertions
		MatcherAssert.assertThat(sortedAlbums, Matchers.contains(a4, a2, a3, a1, a0));
	}
	
	@Test
	public void testSort_bestArtistFirst() {
		CoverArt squareCover = new CoverArt.Builder().maxHeight(500).maxWidth(500).largeUri("http://rect.uri").build();
		
		Album a0 = new Album.Builder().artistName("Rhianna").songName(song).name("x album").coverArt(Optional.of(squareCover)).build();
		Album a1 = new Album.Builder().artistName("Drake").songName(song).name(albumName).coverArt(Optional.of(squareCover)).build();
		Album a2 = new Album.Builder().artistName("Various Artist").songName(song).name(albumName + " (Remastered)").coverArt(Optional.of(squareCover)).build();
		Album a3 = new Album.Builder().artistName(artist).songName(song).name(albumName + " (Unplugged)").coverArt(Optional.of(squareCover)).build();
		List<Album> albums = Arrays.asList(a0, a1, a2, a3);
		
		List<Album> sortedAlbums = albums.stream().sorted(comparator).collect(Collectors.toList());
		// Assertions
		MatcherAssert.assertThat(sortedAlbums, Matchers.contains(a3, a0, a1, a2));
	}
	
	@Test
	public void testSort_bestSongArtistFirst() {
		CoverArt squareCover = new CoverArt.Builder().maxHeight(500).maxWidth(500).largeUri("http://rect.uri").build();
		
		Album a0 = new Album.Builder().artistName("Rhianna").songName(song).name("x album").coverArt(Optional.of(squareCover)).build();
		Album a1 = new Album.Builder().artistName("Drake").songName("Work [Explicit]").name(albumName).coverArt(Optional.of(squareCover)).build();
		Album a2 = new Album.Builder().artistName("Various Artist").songName("Work").name(albumName + " (Remastered)").coverArt(Optional.of(squareCover)).build();
		Album a3 = new Album.Builder().artistName(artist).songName(song).name(albumName + " (Unplugged)").coverArt(Optional.of(squareCover)).build();
		List<Album> albums = Arrays.asList(a0, a1, a2, a3);
		
		List<Album> sortedAlbums = albums.stream().sorted(comparator).collect(Collectors.toList());
		// Assertions
		MatcherAssert.assertThat(sortedAlbums, Matchers.contains(a3, a0, a1, a2));
	}
	
	@Test
	public void testSort_bestAlbumFirst() {
		CoverArt squareCover = new CoverArt.Builder().maxHeight(500).maxWidth(500).largeUri("http://rect.uri").build();
		
		Album a0 = new Album.Builder().artistName(artist).songName(artist + "-" + song).name(song).coverArt(Optional.of(squareCover)).build();
		Album a1 = new Album.Builder().artistName("Various Artist").songName("Work").name(albumName + " (Remastered)").coverArt(Optional.of(squareCover)).build();
		Album a2 = new Album.Builder().artistName(artist).songName(song).name(albumName + " (Unplugged)").coverArt(Optional.of(squareCover)).build();
		Album a3 = new Album.Builder().artistName("Drake").songName("Work [Explicit]").name("Work [Explicit]").coverArt(Optional.of(squareCover)).build();
		List<Album> albums = Arrays.asList(a0, a1, a2, a3);
		
		List<Album> sortedAlbums = albums.stream().sorted(comparator).collect(Collectors.toList());
		// Assertions
		MatcherAssert.assertThat(sortedAlbums, Matchers.contains(a0, a2, a3, a1));
	}

}
