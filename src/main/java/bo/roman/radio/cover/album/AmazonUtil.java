package bo.roman.radio.cover.album;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bo.roman.radio.cover.model.Album;
import bo.roman.radio.cover.model.CoverArt;
import bo.roman.radio.cover.model.mapping.AmazonItems.ItemsWrapper.Item;
import bo.roman.radio.cover.model.mapping.AmazonItems.ItemsWrapper.Item.Image;
import bo.roman.radio.cover.model.mapping.AmazonItems.ItemsWrapper.Item.ItemAttributes;
import bo.roman.radio.cover.model.mapping.AmazonItems.ItemsWrapper.Item.RelatedItems;
import bo.roman.radio.cover.model.mapping.AmazonItems.ItemsWrapper.Item.ItemAttributes.Creator;
import bo.roman.radio.cover.model.mapping.AmazonItems.ItemsWrapper.Item.RelatedItems.RelatedItem;
import bo.roman.radio.utilities.LoggerUtils;

public class AmazonUtil {
	
	private static final Logger log = LoggerFactory.getLogger(AmazonUtil.class);
	
	public static final String PERFORMER_ROLE = "Performer";
	public static final String PRIMARYCONTRIBUTOR_ROLE = "Primary Contributor";
	
	public static final String PRODUCTGROUP_ALBUM = "Digital Music Album";
	public static final String PRODUCTGROUP_MUSIC = "Music";
	
	private static final String AMAZON_UNKNOWN = "AMAZON_UNKNOWN";

	/**
	 * Helper method to convert an Amazon Item to a Pimped Radio Album.
	 * 
	 * @param item
	 * @return
	 */
	public static Optional<Album> itemToAlbum(Item item) {
		if (item == null) {
			log.info("There is no Amazon Item to build an Album.");
			return Optional.empty();
		}

		// To build an Album we need: Song, Artist, albumName, CoverArt
		log.info("Building Album from AmazonItem");
		LoggerUtils.logDebug(log, () -> item.toString());
		
		/* CoverArt */
		Optional<Image> largeImage = Optional.ofNullable(item.getLargeImage());
		CoverArt coverArt = new CoverArt.Builder()
				.largeUri(item.getLargeImageUrl())
				.mediumUri(item.getMediumImageUrl())
				.smallUri(item.getSmallImageUrl())
				.maxWidth(largeImage.map(Image::getWidth).orElse(0))
				.maxHeight(largeImage.map(Image::getHeight).orElse(0))
				.build();
		Optional<CoverArt> oCoverArt = Optional.of(coverArt);

		/* Song | Artist | Album */
		Optional<ItemAttributes> oItemAttribute = Optional.ofNullable(item.getItemAttributes());
		Optional<String> oTitle = oItemAttribute.map(ItemAttributes::getTitle);

		// Check the type of ProductGroup
		String pg = oItemAttribute.map(ItemAttributes::getProductGroup).orElse(AMAZON_UNKNOWN);
		// Song name is Album name when ProductGroup is: Music
		Optional<String> oArtist = oItemAttribute.map(ItemAttributes::getArtist);
		
		LoggerUtils.logDebug(log, () -> "Product Group of the Song: " + pg);
		if (pg.equals(PRODUCTGROUP_MUSIC)) {
			Album a = new Album.Builder().artistName(oArtist.orElse(AMAZON_UNKNOWN))
					.songName(oTitle.orElse(AMAZON_UNKNOWN))
					.name(oTitle.orElse(AMAZON_UNKNOWN))
					.coverArt(oCoverArt)
					.build();
			log.info("Built {}", a);
			return Optional.of(a);
		}

		// We need to find the album from the RelatedItem
		// Album name:
		Optional<ItemAttributes> oAlbumItem = Optional.ofNullable(item.getRelatedItems())
				.map(RelatedItems::getRelatedItem)
				.map(RelatedItem::getItem)
				.map(Item::getItemAttributes);

		String pgAlbum = oAlbumItem.map(ItemAttributes::getProductGroup).orElse(AMAZON_UNKNOWN);
		Optional<String> oAlbumName = oAlbumItem.map(ItemAttributes::getTitle);
		
		// The creator of the Album is the Artist that we are looking for
		Optional<Creator> oCreator = oAlbumItem.map(ItemAttributes::getCreator);
		// This is the Artist:
		Optional<String> oCreatorArtist = oCreator.filter(c -> PRIMARYCONTRIBUTOR_ROLE.equals(c.getRole()) || PERFORMER_ROLE.equals(c.getRole()))
												  .map(Creator::getValue);
		
		LoggerUtils.logDebug(log, () -> "Product Group of the Album: " + pgAlbum);
		if (pgAlbum.equals(PRODUCTGROUP_ALBUM)) {
			Album a = new Album.Builder().artistName(oCreatorArtist.orElse(AMAZON_UNKNOWN))
					.songName(oTitle.orElse(AMAZON_UNKNOWN))
					.name(oAlbumName.orElse(AMAZON_UNKNOWN))
					.coverArt(oCoverArt)
					.build();
			log.info("Built {}", a);
			return Optional.of(a);
		}

		// Not enough info to build an Album
		log.info("Not enough info to build an Album.");
		return Optional.empty();
	}

}
