package bo.roman.radio.cover.album;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.Ignore;
import org.junit.Test;

import bo.roman.radio.cover.model.Album;
import bo.roman.radio.cover.model.CoverArt;
import bo.roman.radio.cover.model.mapping.AmazonItems.ItemsWrapper.Item;
import bo.roman.radio.cover.model.mapping.AmazonItems.ItemsWrapper.Item.Image;
import bo.roman.radio.cover.model.mapping.AmazonItems.ItemsWrapper.Item.ItemAttributes;
import bo.roman.radio.cover.model.mapping.AmazonItems.ItemsWrapper.Item.ItemAttributes.Creator;
import bo.roman.radio.cover.model.mapping.AmazonItems.ItemsWrapper.Item.RelatedItems;
import bo.roman.radio.cover.model.mapping.AmazonItems.ItemsWrapper.Item.RelatedItems.RelatedItem;
import bo.roman.radio.cover.model.mapping.AmazonItems.ItemsWrapper.Item.Tracks;
import bo.roman.radio.cover.model.mapping.AmazonItems.ItemsWrapper.Item.Tracks.Disc;

@Ignore("Amazon is not used anymore")
public class AmazonUtilTest {
	
	@Test
	public void testItemToAlbum_Album() {
		String liUrl = "http://largeUrl";
		int lw, lh;
		lw = lh = 500;
		String miUrl = "http://mediumUrl";
		int mw, mh;
		mw = mh = 200;
		String siUrl = "http://smallUrl";
		int sw, sh;
		sw = sh = 100;
		CoverArt ca = new CoverArt.Builder()
				.largeUri(liUrl)
				.mediumUri(miUrl)
				.smallUri(siUrl)
				.maxHeight(lw)
				.maxWidth(lw)
				.build();
		
		String title = "Nevermind";
		String artist = "Nirvana";
		String productGroup = "Music";
		
		Item item = new Item();
		ItemAttributes itemAttributes = new ItemAttributes(artist, title, productGroup, null);
		item.setItemAttributes(itemAttributes);
		
		Image largeImage = new Image(liUrl, lh, lw);
		item.setLargeImage(largeImage );
		Image mImage = new Image(miUrl, mh, mw);
		item.setMediumImage(mImage);
		Image sImage = new Image(siUrl, sh, sw);
		item.setSmallImage(sImage );
		
		
		Optional<Album> oAlb = AmazonUtil.itemToAlbum(item, title);
		
		// Assertions
		
		assertThat(oAlb.isPresent(), is(true));
		Album a = oAlb.get();
		assertThat(a.getArtistName(), is(artist));
		assertThat(a.getSongName(), is(title));
		assertThat(a.getAlbumName(), is(title));
		assertThat(a.getCoverArt().get(), is(ca));
	}
	
	@Test
	public void testItemToAlbum_AlbumWithRelatedItems() {
		String liUrl = "http://largeUrl";
		int lw, lh;
		lw = lh = 500;
		String miUrl = "http://mediumUrl";
		int mw, mh;
		mw = mh = 200;
		String siUrl = "http://smallUrl";
		int sw, sh;
		sw = sh = 100;
		CoverArt ca = new CoverArt.Builder()
				.largeUri(liUrl)
				.mediumUri(miUrl)
				.smallUri(siUrl)
				.maxHeight(lw)
				.maxWidth(lw)
				.build();
		
		String title = "In Bloom";
		String artist = "Nirvana";
		String productGroup = "Music";
		
		Item item = new Item();
		ItemAttributes itemAttributes = new ItemAttributes(artist, title, productGroup, null);
		item.setItemAttributes(itemAttributes);
		
		Image largeImage = new Image(liUrl, lh, lw);
		item.setLargeImage(largeImage );
		Image mImage = new Image(miUrl, mh, mw);
		item.setMediumImage(mImage);
		Image sImage = new Image(siUrl, sh, sw);
		item.setSmallImage(sImage);
		
		Item albumItem = new Item();
		String albumName = "Nevermind";
		String albumPG = "Digital Music Album";
		Creator creator = new Creator("Primary Contributor", artist);

		ItemAttributes albumIA = new ItemAttributes(artist, albumName, albumPG, creator);
		albumItem.setItemAttributes(albumIA );
		RelatedItem relatedItem = new RelatedItem(albumItem );
		RelatedItems relatedItems = new RelatedItems(Arrays.asList(relatedItem));
		item.setRelatedItems(relatedItems);
		
		
		Optional<Album> oAlb = AmazonUtil.itemToAlbum(item, title);
		
		// Assertions
		
		assertThat(oAlb.isPresent(), is(true));
		Album a = oAlb.get();
		assertThat(a.getArtistName(), is(artist));
		assertThat(a.getSongName(), is(title));
		assertThat(a.getAlbumName(), is(title));
		assertThat(a.getCoverArt().get(), is(ca));
		
	}
	
	@Test
	public void testItemToAlbum_MultipleTracks() {
		String liUrl = "http://largeUrl";
		int lw, lh;
		lw = lh = 500;
		String miUrl = "http://mediumUrl";
		int mw, mh;
		mw = mh = 200;
		String siUrl = "http://smallUrl";
		int sw, sh;
		sw = sh = 100;
		CoverArt ca = new CoverArt.Builder()
				.largeUri(liUrl)
				.mediumUri(miUrl)
				.smallUri(siUrl)
				.maxHeight(lw)
				.maxWidth(lw)
				.build();
		
		String title = "Breed";
		String artist = "Nirvana";
		String albumName = "Nevermind";
		String productGroup = "Music";
		
		Item item = new Item();
		ItemAttributes itemAttributes = new ItemAttributes(artist, albumName, productGroup, null);
		item.setItemAttributes(itemAttributes);
		
		Image largeImage = new Image(liUrl, lh, lw);
		item.setLargeImage(largeImage );
		Image mImage = new Image(miUrl, mh, mw);
		item.setMediumImage(mImage);
		Image sImage = new Image(siUrl, sh, sw);
		item.setSmallImage(sImage);
		
		Disc disc1 = new Disc(Arrays.asList("Nevermind", "In Bloom"));
		Disc disc2 = new Disc(Arrays.asList("Come as you are", "Polly"));
		Disc disc3 = new Disc(Arrays.asList("Smells Like Teen Spirit", "Breed", "Drain you"));
		Tracks iTracks = new Tracks(Arrays.asList(disc1, disc2, disc3));
		item.setTracks(iTracks);
		
		Optional<Album> oAlb = AmazonUtil.itemToAlbum(item, title);
		
		// Assertions
		
		assertThat(oAlb.isPresent(), is(true));
		Album a = oAlb.get();
		assertThat(a.getArtistName(), is(artist));
		assertThat(a.getSongName(), is(title));
		assertThat(a.getAlbumName(), is(albumName));
		assertThat(a.getCoverArt().get(), is(ca));
	}
	
	@Test
	public void testItemToAlbum_MultipleTracksCloseMatchSong() {
		String liUrl = "http://largeUrl";
		int lw, lh;
		lw = lh = 500;
		String miUrl = "http://mediumUrl";
		int mw, mh;
		mw = mh = 200;
		String siUrl = "http://smallUrl";
		int sw, sh;
		sw = sh = 100;
		CoverArt ca = new CoverArt.Builder()
				.largeUri(liUrl)
				.mediumUri(miUrl)
				.smallUri(siUrl)
				.maxHeight(lw)
				.maxWidth(lw)
				.build();
		
		String title = "Breed";
		String artist = "Nirvana";
		String albumName = "Nevermind";
		String productGroup = "Music";
		
		Item item = new Item();
		ItemAttributes itemAttributes = new ItemAttributes(artist, albumName, productGroup, null);
		item.setItemAttributes(itemAttributes);
		
		Image largeImage = new Image(liUrl, lh, lw);
		item.setLargeImage(largeImage );
		Image mImage = new Image(miUrl, mh, mw);
		item.setMediumImage(mImage);
		Image sImage = new Image(siUrl, sh, sw);
		item.setSmallImage(sImage);
		
		Disc disc1 = new Disc(Arrays.asList("Nevermind", "In Bloom"));
		Disc disc2 = new Disc(Arrays.asList("Come as you are", "Polly"));
		Disc disc3 = new Disc(Arrays.asList("Smells Like Teen Spirit", "Breed (Live)", "Drain you"));
		Tracks iTracks = new Tracks(Arrays.asList(disc1, disc2, disc3));
		item.setTracks(iTracks);
		
		Optional<Album> oAlb = AmazonUtil.itemToAlbum(item, title);
		
		// Assertions
		
		assertThat(oAlb.isPresent(), is(true));
		Album a = oAlb.get();
		assertThat(a.getArtistName(), is(artist));
		assertThat(a.getSongName(), is(title + " (Live)"));
		assertThat(a.getAlbumName(), is(albumName));
		assertThat(a.getCoverArt().get(), is(ca));
	}
	
	@Test
	public void testItemToAlbum_MultipleTracksNoMatchSong() {
		String liUrl = "http://largeUrl";
		int lw, lh;
		lw = lh = 500;
		String miUrl = "http://mediumUrl";
		int mw, mh;
		mw = mh = 200;
		String siUrl = "http://smallUrl";
		int sw, sh;
		sw = sh = 100;
		CoverArt ca = new CoverArt.Builder()
				.largeUri(liUrl)
				.mediumUri(miUrl)
				.smallUri(siUrl)
				.maxHeight(lw)
				.maxWidth(lw)
				.build();
		
		String title = "Plateu";
		String artist = "Nirvana";
		String albumName = "Nevermind";
		String productGroup = "Music";
		
		Item item = new Item();
		ItemAttributes itemAttributes = new ItemAttributes(artist, albumName, productGroup, null);
		item.setItemAttributes(itemAttributes);
		
		Image largeImage = new Image(liUrl, lh, lw);
		item.setLargeImage(largeImage );
		Image mImage = new Image(miUrl, mh, mw);
		item.setMediumImage(mImage);
		Image sImage = new Image(siUrl, sh, sw);
		item.setSmallImage(sImage);
		
		Disc disc1 = new Disc(Arrays.asList("Nevermind", "In Bloom"));
		Disc disc2 = new Disc(Arrays.asList("Smells Like Teen Spirit", "Breed"));
		Tracks iTracks = new Tracks(Arrays.asList(disc1, disc2));
		item.setTracks(iTracks);
		
		Optional<Album> oAlb = AmazonUtil.itemToAlbum(item, title);
		
		// Assertions
		
		assertThat(oAlb.isPresent(), is(true));
		Album a = oAlb.get();
		assertThat(a.getArtistName(), is(artist));
		assertThat(a.getSongName(), is(albumName));
		assertThat(a.getAlbumName(), is(albumName));
		assertThat(a.getCoverArt().get(), is(ca));
	}
	
	@Test
	public void testItemToAlbum_NoTracks() {
		String liUrl = "http://largeUrl";
		int lw, lh;
		lw = lh = 500;
		String miUrl = "http://mediumUrl";
		int mw, mh;
		mw = mh = 200;
		String siUrl = "http://smallUrl";
		int sw, sh;
		sw = sh = 100;
		CoverArt ca = new CoverArt.Builder()
				.largeUri(liUrl)
				.mediumUri(miUrl)
				.smallUri(siUrl)
				.maxHeight(lw)
				.maxWidth(lw)
				.build();
		
		String title = "Plateu";
		String artist = "Nirvana";
		String albumName = "Nevermind";
		String productGroup = "Music";
		
		Item item = new Item();
		ItemAttributes itemAttributes = new ItemAttributes(artist, albumName, productGroup, null);
		item.setItemAttributes(itemAttributes);
		
		Image largeImage = new Image(liUrl, lh, lw);
		item.setLargeImage(largeImage );
		Image mImage = new Image(miUrl, mh, mw);
		item.setMediumImage(mImage);
		Image sImage = new Image(siUrl, sh, sw);
		item.setSmallImage(sImage);
		
		Optional<Album> oAlb = AmazonUtil.itemToAlbum(item, title);
		
		// Assertions
		
		assertThat(oAlb.isPresent(), is(true));
		Album a = oAlb.get();
		assertThat(a.getArtistName(), is(artist));
		assertThat(a.getSongName(), is(albumName));
		assertThat(a.getAlbumName(), is(albumName));
		assertThat(a.getCoverArt().get(), is(ca));
	}
	
	@Test
	public void testItemToAlbum_Track() {
		String liUrl = "http://largeUrl";
		int lw, lh;
		lw = lh = 500;
		String miUrl = "http://mediumUrl";
		int mw, mh;
		mw = mh = 200;
		String siUrl = "http://smallUrl";
		int sw, sh;
		sw = sh = 100;
		CoverArt ca = new CoverArt.Builder()
				.largeUri(liUrl)
				.mediumUri(miUrl)
				.smallUri(siUrl)
				.maxHeight(lw)
				.maxWidth(lw)
				.build();
		
		String title = "Breed";
		String artist = "Nirvana";
		String productGroup = "Digital Music Track";
		Creator creator = new Creator("Primary Contributor", artist);
		
		Item item = new Item();
		ItemAttributes itemAttributes = new ItemAttributes(null, title, productGroup, creator);
		item.setItemAttributes(itemAttributes);
		
		Image largeImage = new Image(liUrl, lh, lw);
		item.setLargeImage(largeImage );
		Image mImage = new Image(miUrl, mh, mw);
		item.setMediumImage(mImage);
		Image sImage = new Image(siUrl, sh, sw);
		item.setSmallImage(sImage);
		
		Item albumItem = new Item();
		String albumName = "Nevermind";
		String albumPG = "Digital Music Album";
		ItemAttributes albumIA = new ItemAttributes(artist, albumName, albumPG, creator);
		albumItem.setItemAttributes(albumIA );
		RelatedItem relatedItem = new RelatedItem(albumItem );
		RelatedItems relatedItems = new RelatedItems(Arrays.asList(relatedItem));
		item.setRelatedItems(relatedItems);
		
		Optional<Album> oAlb = AmazonUtil.itemToAlbum(item, title);
		
		// Assertions
		
		assertThat(oAlb.isPresent(), is(true));
		Album a = oAlb.get();
		assertThat(a.getArtistName(), is(artist));
		assertThat(a.getSongName(), is(title));
		assertThat(a.getAlbumName(), is(albumName));
		assertThat(a.getCoverArt().get(), is(ca));
	}
	
	@Test
	public void testItemToAlbum_RelatedItemTracks() {
		String liUrl = "http://largeUrl";
		int lw, lh;
		lw = lh = 500;
		String miUrl = "http://mediumUrl";
		int mw, mh;
		mw = mh = 200;
		String siUrl = "http://smallUrl";
		int sw, sh;
		sw = sh = 100;
		CoverArt ca = new CoverArt.Builder()
				.largeUri(liUrl)
				.mediumUri(miUrl)
				.smallUri(siUrl)
				.maxHeight(lw)
				.maxWidth(lw)
				.build();
		
		String requestedSong = "Drain you";
		
		String title = "Nevermind";
		String artist = "Nirvana";
		String productGroup = "Digital Music Album";
		Creator creator = new Creator("Primary Contributor", artist);
		
		Item item = new Item();
		ItemAttributes itemAttributes = new ItemAttributes(null, title, productGroup, creator);
		item.setItemAttributes(itemAttributes);
		
		Image largeImage = new Image(liUrl, lh, lw);
		item.setLargeImage(largeImage );
		Image mImage = new Image(miUrl, mh, mw);
		item.setMediumImage(mImage);
		Image sImage = new Image(siUrl, sh, sw);
		item.setSmallImage(sImage);
		
		String[] songs = {"Breed", "Smells like teen spirit", "Drain you", "Nevermind"};
		List<RelatedItem> relatedItemsList = new ArrayList<>();
		for(String s : songs) {
			Item trackItem1 = new Item();
			String albumPG = "Digital Music Track";
			ItemAttributes albumIA1= new ItemAttributes(null, s, albumPG, creator);
			trackItem1.setItemAttributes(albumIA1);
			
			relatedItemsList.add(new RelatedItem(trackItem1));
		}
		
		RelatedItems relatedItems = new RelatedItems(relatedItemsList);
		item.setRelatedItems(relatedItems);
		
		Optional<Album> oAlb = AmazonUtil.itemToAlbum(item, requestedSong);
		
		// Assertions
		assertThat(oAlb.isPresent(), is(true));
		Album a = oAlb.get();
		assertThat(a.getArtistName(), is(artist));
		assertThat(a.getSongName(), is(requestedSong));
		assertThat(a.getAlbumName(), is(title));
		assertThat(a.getCoverArt().get(), is(ca));
	}
	
	@Test
	public void testItemToAlbum_NoItemAttributes() {
		Optional<Album> oAlb = AmazonUtil.itemToAlbum(new Item(), "aTitle");
		
		// Assertions
		assertThat(oAlb.isPresent(), is(false));
	}
	
	@Test
	public void testItemToAlbum_TrackNoRelatedItems() {
		String liUrl = "http://largeUrl";
		int lw, lh;
		lw = lh = 500;
		String miUrl = "http://mediumUrl";
		int mw, mh;
		mw = mh = 200;
		String siUrl = "http://smallUrl";
		int sw, sh;
		sw = sh = 100;
		
		String title = "Breed";
		String artist = "Nirvana";
		String productGroup = "Digital Music Track";
		Creator creator = new Creator("Primary Contributor", artist);
		
		Item item = new Item();
		ItemAttributes itemAttributes = new ItemAttributes(null, title, productGroup, creator);
		item.setItemAttributes(itemAttributes);
		
		Image largeImage = new Image(liUrl, lh, lw);
		item.setLargeImage(largeImage );
		Image mImage = new Image(miUrl, mh, mw);
		item.setMediumImage(mImage);
		Image sImage = new Image(siUrl, sh, sw);
		item.setSmallImage(sImage);
		
		Optional<Album> oAlb = AmazonUtil.itemToAlbum(item, title);
		
		// Assertions
		assertThat(oAlb.isPresent(), is(false));
	}

}
