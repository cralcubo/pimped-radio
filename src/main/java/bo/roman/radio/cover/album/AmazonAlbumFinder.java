package bo.roman.radio.cover.album;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bo.roman.radio.cover.model.Album;
import bo.roman.radio.cover.model.mapping.AmazonItems;
import bo.roman.radio.cover.model.mapping.AmazonItems.ItemsWrapper;
import bo.roman.radio.cover.model.mapping.AmazonItems.ItemsWrapper.Item;
import bo.roman.radio.cover.model.mapping.AmazonItems.ItemsWrapper.Item.Image;
import bo.roman.radio.cover.model.mapping.AmazonItems.ItemsWrapper.Item.ItemAttributes;
import bo.roman.radio.utilities.HttpUtils;
import bo.roman.radio.utilities.ImageUtil;
import bo.roman.radio.utilities.LoggerUtils;
import bo.roman.radio.utilities.RegExUtil;
import bo.roman.radio.utilities.StringUtils;

public class AmazonAlbumFinder implements AlbumFindable {
	private static final Logger log = LoggerFactory.getLogger(AmazonAlbumFinder.class);
	
	private static final String NOWPLAYING_TEMPL = "%s,%s";

	@Override
	public List<Album> findAlbums(String song, String artist) {
		if(!StringUtils.exists(song) || !StringUtils.exists(artist)) {
			log.info("There is no Song name and/or Artist to find an Album.");
			return Collections.emptyList();
		}
		
		String nowPLaying = String.format(NOWPLAYING_TEMPL, song, artist);
		log.info("Requesting Album from Amazon for: {}", nowPLaying);
		String reqUrl = AmazonConnectionUtil.generateSearchByKeywordRequestUrl(nowPLaying);
		
		try {
			LoggerUtils.logDebug(log, () -> String.format("Sending Product information request to Amazon. URL[%s]", reqUrl));
			String xmlResponse = HttpUtils.doGet(reqUrl);
			// Convert XML response to an AmazonItem object
			Optional<AmazonItems> oItems = unmarshalXml(xmlResponse);
			
			// Filter no Music Items
			if(!oItems.isPresent() 
					|| !oItems.map(AmazonItems::getItemsWrapper).isPresent() 
					|| !oItems.map(AmazonItems::getItemsWrapper).map(ItemsWrapper::getItems).isPresent()) {
				
				log.info("No Amazon Items for [{}-{}] could be found.", song, artist);
				return Collections.emptyList();
			}
			
			// Convert Amazon Music Items with a big enough CoverArt to Albums
			List<Album> allAlbums = oItems
					.map(AmazonItems::getItemsWrapper)
					.map(ItemsWrapper::getItems)
					.get().stream()
					.filter(AmazonAlbumFinder::isMusicItem)
					.filter(AmazonAlbumFinder::hasCoverArt)
					.filter(AmazonAlbumFinder::isBigEnough)
					.map(AmazonUtil::itemToAlbum)
					.flatMap(oa -> oa.map(Stream::of).orElseGet(Stream::empty))
					.collect(Collectors.toList());
			
			// Find the albums that match the Song and Artist used to search them
			List<Album> matchingAlbums = allAlbums.stream()
					.filter(a -> matchSongArtist(song, artist, a))
					.collect(Collectors.toList());
			
			if(matchingAlbums.isEmpty()) {
				LoggerUtils.logDebug(log, () -> "No matching albums found. Check swaping Song - Artist.");
				matchingAlbums = allAlbums.stream()
						.filter(a -> matchSongArtist(artist, song, a))
						.collect(Collectors.toList());
			}
			
			log.info("[{}] Albums found in Amazon.", matchingAlbums.size());
			return matchingAlbums;
			
		} catch (IOException e) {
			log.error("There was an error trying to connect to Amazon to find Albums.", e);
			return Collections.emptyList();
		}
		
	}

	private Optional<AmazonItems> unmarshalXml(String xmlResponse) {
		LoggerUtils.logDebug(log, () -> "Unmarshalling XML Response");
		try {
			JAXBContext context = JAXBContext.newInstance(AmazonItems.class);
			Unmarshaller unmarshaller = context.createUnmarshaller();
			AmazonItems ai = (AmazonItems) unmarshaller.unmarshal(new ByteArrayInputStream(xmlResponse.getBytes(HttpUtils.UTF_8)));
			
			if(ai == null) {
				LoggerUtils.logDebug(log, () -> "The XML response could not be unmarshaled. Review the binding of the XML with the class AmazonItems");
				return Optional.empty();
			}
			
			/* ***Logging*** */
			LoggerUtils.logDebug(log, () -> {
				if(ai.getItemsWrapper() != null && ai.getItemsWrapper().getItems() != null) {
					List<Item> items = ai.getItemsWrapper().getItems();
					return String.format("[%d] Items retrieved from Amazon [%s]", items.size(), items); 
				}
				return "No items retrieved from Amazon.";
			});
			/* ***** */
			
			return Optional.of(ai);
		} 
		catch( UnsupportedEncodingException e) {
			throw new RuntimeException(String.format("Totally unexpected, %s is supposed to be an accepted character encoding.", HttpUtils.UTF_8), e);
		}
		catch (JAXBException e) {
			LoggerUtils.logDebug(log, () -> "Couldn't unmarshal XML Response.", e);
			return Optional.empty();
		}
	}
	
	/**
	 * Check if the Item ProductGroup is Music.
	 * This checking is used when an Item is searched in all the amazon store,
	 * so by this checking we can filter just the Music Items.
	 *  
	 * @param i
	 * @return
	 */
	private static boolean isMusicItem(Item i) {
		ItemAttributes ia = i.getItemAttributes();
		if(ia == null) {
			return false;
		}
		LoggerUtils.logDebug(log, () -> "ProductGroup of Item is " + ia.getProductGroup());
		
		return StringUtils.exists(ia.getProductGroup()) && RegExUtil.phrase(ia.getProductGroup()).containsIgnoreCase("music");
	}
	
	/**
	 * Check if the song and artist match
	 * the Album information retrieved from Amazon.
	 * If the Album retrieved contains the word expected, ignoring
	 * the case, the match will be true.
	 * i.e. 
	 * Song name is 'In Bloom' will match 'In bloom by Nirvana'
	 * 
	 * @param song
	 * @param artist
	 * @param a
	 * @return
	 */
	private boolean matchSongArtist(String song, String artist, Album a) {
		LoggerUtils.logDebug(log, () -> "Checking match of song and artist for " + a);
		
		// Check Song Name
		String albumSong = a.getSongName();
		LoggerUtils.logDebug(log, () -> "Item Title=" + albumSong);
		if(!StringUtils.exists(albumSong) || 
				(!RegExUtil.phrase(albumSong).containsIgnoreCase(song) && !RegExUtil.phrase(song).containsIgnoreCase(albumSong))) {
			LoggerUtils.logDebug(log, () -> String.format("Item Title[%s] does not match Song=%s", albumSong, song));
			return false;
		}
		
		// Check Artist
		String albumArtist = a.getArtistName();
		LoggerUtils.logDebug(log, () -> "Item Artist=" + albumArtist);
		if(StringUtils.exists(albumArtist)) {
			boolean match = RegExUtil.phrase(albumArtist).containsIgnoreCase(artist) || RegExUtil.phrase(artist).containsIgnoreCase(albumArtist); 
			LoggerUtils.logDebug(log, () ->  String.format("Item matches Artist[%s]=%s", artist, match));
			return match;
		}
		
		return false;
	}
	
	/**
	 * Check that the Amazon Item contains the largest
	 * Item Image.
	 */
	private static boolean hasCoverArt(Item i) {
		Image largeImage = i.getLargeImage();
		LoggerUtils.logDebug(log, () -> "LargeImage of Amazon Items is " + largeImage);
		return largeImage != null && StringUtils.exists(largeImage.getUrl()); 
	}
	
	private static boolean isBigEnough(Item i) {
		Image largeImage = i.getLargeImage();
		LoggerUtils.logDebug(log, () -> "Checking LargeImage size of Amazon Item " + largeImage);
		return largeImage != null && ImageUtil.isBigEnough(largeImage.getWidth(), largeImage.getHeight(), largeImage.getUrl());
	}

}
