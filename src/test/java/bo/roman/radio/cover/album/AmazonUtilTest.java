package bo.roman.radio.cover.album;

import java.util.Optional;

import org.junit.Test;

import bo.roman.radio.cover.model.Album;
import bo.roman.radio.cover.model.CoverArt;
import bo.roman.radio.cover.model.mapping.AmazonItems.ItemsWrapper.Item;
import bo.roman.radio.cover.model.mapping.AmazonItems.ItemsWrapper.Item.Image;
import bo.roman.radio.cover.model.mapping.AmazonItems.ItemsWrapper.Item.ItemAttributes;
import bo.roman.radio.cover.model.mapping.AmazonItems.ItemsWrapper.Item.ItemAttributes.Creator;
import bo.roman.radio.cover.model.mapping.AmazonItems.ItemsWrapper.Item.RelatedItems;
import bo.roman.radio.cover.model.mapping.AmazonItems.ItemsWrapper.Item.RelatedItems.RelatedItem;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


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
		
		
		Optional<Album> oAlb = AmazonUtil.itemToAlbum(item);
		
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
		RelatedItems relatedItems = new RelatedItems(relatedItem );
		item.setRelatedItems(relatedItems);
		
		
		Optional<Album> oAlb = AmazonUtil.itemToAlbum(item);
		
		// Assertions
		
		assertThat(oAlb.isPresent(), is(true));
		Album a = oAlb.get();
		assertThat(a.getArtistName(), is(artist));
		assertThat(a.getSongName(), is(title));
		assertThat(a.getAlbumName(), is(title));
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
		RelatedItems relatedItems = new RelatedItems(relatedItem );
		item.setRelatedItems(relatedItems);
		
		
		Optional<Album> oAlb = AmazonUtil.itemToAlbum(item);
		
		// Assertions
		
		assertThat(oAlb.isPresent(), is(true));
		Album a = oAlb.get();
		assertThat(a.getArtistName(), is(artist));
		assertThat(a.getSongName(), is(title));
		assertThat(a.getAlbumName(), is(albumName));
		assertThat(a.getCoverArt().get(), is(ca));
	}
	
	@Test
	public void testItemToAlbum_NoItemAttributes() {
		Optional<Album> oAlb = AmazonUtil.itemToAlbum(new Item());
		
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
		
		Optional<Album> oAlb = AmazonUtil.itemToAlbum(item);
		
		// Assertions
		assertThat(oAlb.isPresent(), is(false));
	}

}
