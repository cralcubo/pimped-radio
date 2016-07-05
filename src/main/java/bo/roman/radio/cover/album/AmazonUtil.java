package bo.roman.radio.cover.album;

import java.util.Optional;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bo.roman.radio.cover.model.Album;
import bo.roman.radio.cover.model.CoverArt;
import bo.roman.radio.cover.model.mapping.AmazonItems.ItemsWrapper.Item;
import bo.roman.radio.cover.model.mapping.AmazonItems.ItemsWrapper.Item.Image;
import bo.roman.radio.cover.model.mapping.AmazonItems.ItemsWrapper.Item.ItemAttributes;
import bo.roman.radio.cover.model.mapping.AmazonItems.ItemsWrapper.Item.ItemAttributes.Creator;
import bo.roman.radio.cover.model.mapping.AmazonItems.ItemsWrapper.Item.RelatedItems;
import bo.roman.radio.cover.model.mapping.AmazonItems.ItemsWrapper.Item.Tracks;
import bo.roman.radio.utilities.LoggerUtils;
import bo.roman.radio.utilities.PhraseCalculator;

public class AmazonUtil {
	
	private static final Logger log = LoggerFactory.getLogger(AmazonUtil.class);
	
	public static final String PERFORMER_ROLE = "Performer";
	public static final String PRIMARYCONTRIBUTOR_ROLE = "Primary Contributor";
	
	public static final String PRODUCTGROUP_ALBUM = "Digital Music Album";
	public static final String PRODUCTGROUP_TRACK = "Digital Music Track";
	public static final String PRODUCTGROUP_MUSIC = "Music";
	
	private static final String AMAZON_UNKNOWN = "AMAZON_UNKNOWN";
	
	/**
	 * Helper method to convert an Amazon Item to a Pimped Radio Album.
	 * 
	 * @param item
	 * @return
	 */
	public static Optional<Album> itemToAlbum(Item item, String requestedSong) {
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
		
		Optional<Album> oa = itemToAlbumHelper(item, requestedSong, Optional.of(new Album.Builder().coverArt(Optional.of(coverArt)).build()));
		
		log.info("Built Album {}", oa);
		return oa;
	}
	
	/**
	 * Recursive method to retrieve information of an Amazon Item
	 * and build a Pimped Radio Album.
	 * 
	 * @param item
	 * @param requestedSong
	 * @param oAlbum
	 * @return
	 */
	private static Optional<Album> itemToAlbumHelper(Item item, String requestedSong, Optional<Album> oAlbum) {
		if (item == null) {
			log.info("There is no Amazon Item to retrieve information.");
			return Optional.empty();
		}
		
		Optional<ItemAttributes> oItemAttribute = Optional.ofNullable(item.getItemAttributes());
		
		Optional<String> oTitle = oItemAttribute.map(ItemAttributes::getTitle);
		Optional<String> oArtist = oItemAttribute.map(ItemAttributes::getArtist);
		Optional<Creator> sCreator = oItemAttribute.map(ItemAttributes::getCreator);
		Optional<String> sCreatorArtist = sCreator.filter(c -> PRIMARYCONTRIBUTOR_ROLE.equals(c.getRole()) || PERFORMER_ROLE.equals(c.getRole()))
						  								  .map(Creator::getValue);
		
		// Check the type of ProductGroup retrieved
		String productGroup =  oItemAttribute.map(ItemAttributes::getProductGroup).orElse(AMAZON_UNKNOWN);
		final Album tempAlbum;
		switch (productGroup) {
		case PRODUCTGROUP_MUSIC:
			String songName = oTitle.orElse(AMAZON_UNKNOWN);
			if(!PhraseCalculator.phrase(requestedSong).isCloseTo(songName)) {
				//Check if between all the tracks there is the requested song
				Optional<Tracks> oItemTracks = Optional.ofNullable(item.getTracks());
				Optional<String> trackSong = oItemTracks.map(Tracks::getDiscs)
												        .flatMap(discs -> discs.stream()
												    		              	.flatMap(d -> d.getTracks().stream())
												    		              	.filter(trackName -> PhraseCalculator.phrase(requestedSong).isCloseTo(trackName))
												    		              	.findFirst());
				if(trackSong.isPresent()) {
					songName = trackSong.get();
				}
			}
			
			Album a = new Album.Builder()
					.artistName(oArtist.orElseGet(() -> sCreatorArtist.orElse(AMAZON_UNKNOWN)))
					.songName(songName)
					.name(oTitle.orElse(AMAZON_UNKNOWN))
					.coverArt(oAlbum.flatMap(Album::getCoverArt))
					.build();
			
			LoggerUtils.logDebug(log, () -> String.format("Built Album from productGroup[%s]=%s", productGroup, a));
			return Optional.of(a);
		case PRODUCTGROUP_TRACK:
			Optional<String> oAlbumName = oAlbum.map(Album::getAlbumName);
			// If album name is present then check if the song matches the requestedSong
			if(oAlbumName.isPresent()) {
				if(PhraseCalculator.phrase(requestedSong).isCloseTo(oTitle.get())) {
					Album songAlbum = new Album.Builder()
										.songName(oTitle.get())
										.name(oAlbumName.get())
										.artistName(oAlbum.map(Album::getArtistName).get())
										.coverArt(oAlbum.flatMap(Album::getCoverArt))
										.build();
					LoggerUtils.logDebug(log, () -> String.format("Built Album from productGroup[%s]=%s", productGroup, songAlbum));
					return Optional.of(songAlbum);
				}
				
				return Optional.empty();
			}
			
			// No Album name present, find it:
			tempAlbum = new Album.Builder()
					.songName(oTitle.orElse(AMAZON_UNKNOWN))
					.artistName(sCreatorArtist.orElse(AMAZON_UNKNOWN))
					.coverArt(oAlbum.flatMap(Album::getCoverArt))
					.build();
			break;
		case PRODUCTGROUP_ALBUM:
			Optional<String> oAlbumSongName = oAlbum.map(Album::getSongName);
			// Song name present create an album with the information retrieved 
			// from the item
			if(oAlbumSongName.isPresent()) {
				Album album = new Album.Builder()
								.songName(oAlbumSongName.get())
								.artistName(oArtist.orElseGet(() -> sCreatorArtist.orElse(oAlbum.map(Album::getArtistName).get())))
								.name(oTitle.orElse(AMAZON_UNKNOWN))
								.coverArt(oAlbum.flatMap(Album::getCoverArt))
								.build();
				
				LoggerUtils.logDebug(log, () -> String.format("Built Album from productGroup[%s]=%s", productGroup, album));
				return Optional.of(album);
			}
			// Check if there are tracks to retrieve the requested song
			tempAlbum = new Album.Builder()
						.artistName(oArtist.orElseGet(() -> sCreatorArtist.orElse(AMAZON_UNKNOWN)))
						.name(oTitle.orElse(AMAZON_UNKNOWN))
						.coverArt(oAlbum.flatMap(Album::getCoverArt))
						.build();
			break;
		default:
			LoggerUtils.logDebug(log, () -> "It is not possible to build an Album from productGroup=" + productGroup);
			return Optional.empty();
		}
		
		Optional<RelatedItems> oRelItems = Optional.ofNullable(item.getRelatedItems());
		Optional<Album> albumFound = oRelItems.flatMap(ris -> ris.getRelatedItem().stream()
																 .map(ri -> itemToAlbumHelper(ri.getItem(), requestedSong, Optional.of(tempAlbum)))
																 .flatMap(o -> o.isPresent() ? Stream.of(o.get()) : Stream.empty())
																 .findFirst());
		
		LoggerUtils.logDebug(log, () -> String.format("Built Album from productGroup[%s]=%s", productGroup, albumFound));
		return albumFound;
	}
	
}
