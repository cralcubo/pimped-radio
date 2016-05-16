package bo.roman.radio.cover.album;

import static bo.roman.radio.utilities.StringUtils.exists;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bo.roman.radio.cover.model.Album;
import bo.roman.radio.cover.model.CoverArt;
import bo.roman.radio.cover.model.mapping.AmazonItems;
import bo.roman.radio.cover.model.mapping.AmazonItems.ItemsWrapper;
import bo.roman.radio.cover.model.mapping.AmazonItems.ItemsWrapper.Item;
import bo.roman.radio.cover.model.mapping.AmazonItems.ItemsWrapper.Item.Image;
import bo.roman.radio.cover.model.mapping.AmazonItems.ItemsWrapper.Item.ItemAttributes;
import bo.roman.radio.utilities.HttpUtils;
import bo.roman.radio.utilities.ImageUtil;
import bo.roman.radio.utilities.LoggerUtils;
import bo.roman.radio.utilities.StringUtils;

public class AmazonCoverFinder implements CoverArtFindable {
	
	private final static Logger log = LoggerFactory.getLogger(AmazonCoverFinder.class);

	public enum SearchType {SEARCHBY_ALBUM, SEARCHBY_KEYWORD, UNKNOWN}

	private static final String MUSICWORD_REGEX = "(?i)\\bmusic\\b";
	private static final String PRIMARYCONTRIBUTOR_ROLE = "Primary Contributor";
	
	@Override
	public Optional<CoverArt> findCoverArt(Album album) throws IOException {
		if(album == null) {
			log.info("There is no Album to search its Cover Art in Amazon.");
			return Optional.empty();
		}
		
		// What kind of search will be done in Amazon?
		SearchType searchType = determineSearchType(album);
		LoggerUtils.logDebug(log, () -> "Search type to be executed in Amazon :" + searchType);
		// Generate the URL to send the REST request
		final String url;
		switch (searchType) {
		case SEARCHBY_ALBUM:
			url = AmazonConnectionUtil.generateSearchByAlbumRequestUrl(album);
			break;
		case SEARCHBY_KEYWORD:
			url = AmazonConnectionUtil.generateSearchByKeywordRequestUrl(String.format(album.getSongName(), album.getArtistName()));
			break;
		default:
			log.info("There is no artistName or albumName or songName to search a Cover Art in Amazon.");
			return Optional.empty();
		}
		
		LoggerUtils.logDebug(log, () -> String.format("Sending Product information request to Amazon. URL[%s]", url));
		String xmlResponse = HttpUtils.doGet(url);
		
		// Convert XML response to an AmazonItem object
		Optional<AmazonItems> oItems = unmarshalXml(xmlResponse);
		if(!oItems.isPresent() 
				|| !oItems.map(AmazonItems::getItemsWrapper).isPresent() 
				|| !oItems.map(AmazonItems::getItemsWrapper).map(ItemsWrapper::getItems).isPresent()) {
			
			log.info("No Amazon Items for the {} could be found.", album);
			return Optional.empty();
		}
		
		List<Item> allItems = oItems
				.map(AmazonItems::getItemsWrapper)
				.map(ItemsWrapper::getItems)
				.get().stream()
				.filter(AmazonCoverFinder::isMusicItem)
				.filter(AmazonCoverFinder::hasCoverArt)
				.filter(AmazonCoverFinder::isBigEnough)
				.collect(Collectors.toList());
		
		log.info("[{}] Items found in Amazon.", allItems.size());
		
		// Find if there is an Amazon Item that matches the name of the album
		// or the name of the artist if the search was made keyword.
		Optional<Item> bestItem = allItems.stream()
				.filter(i -> isItemTitleEqualsTo(i, album.getName()) || isItemCreatorEqualsTo(i, album.getArtistName()))
				.max(new ImageComparator());
		
		if(bestItem.isPresent()) {
			Optional<CoverArt> coverArt = bestItem.map(AmazonCoverFinder::buildCoverArt);
			LoggerUtils.logDebug(log, () -> String.format("The best Amazon CoverArt for [%s] is from %s", album, bestItem));
			log.info("The best Amazon CoverArt for [{} - {} - {}] is {}", album.getSongName(), album.getArtistName(), album.getName(), coverArt);
			return coverArt;
		}
		
		// In case there was no match found to the name of the album, return the Amazon Item with the biggest Image.
		Optional<Item> firstItem = allItems.stream()
				.max(new ImageComparator());
		Optional<CoverArt> coverArt = firstItem.map(AmazonCoverFinder::buildCoverArt);
		
		LoggerUtils.logDebug(log, () -> String.format("CoverArt found for %s in Amazon from Item %s", album, firstItem));
		log.info("CoverArt found for [{} - {} - {}] is {}", album.getSongName(), album.getArtistName(), album.getName(), coverArt);

		return coverArt;
	}

	/**
	 * Determine, depending on the information contained
	 * in the Album object the type of search to be requested
	 * to Amazon.
	 * 
	 * If album has: name and artistName => searchByAlbum
	 * If album has only: songName and artistName => searchByKeyword
	 * If none of the above => unknown.
	 * 
	 * @param album
	 * @return
	 */
	private SearchType determineSearchType(Album album) {
		String songName = album.getSongName();
		String artistName = album.getArtistName();
		String albumName = album.getName();
		
		// Giving priority to search by Album
		if(exists(albumName) && exists(artistName)) {
			return SearchType.SEARCHBY_ALBUM;
		}
		
		if(exists(songName)) {
			return SearchType.SEARCHBY_KEYWORD;
		}
		
		return SearchType.UNKNOWN;
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
			
			LoggerUtils.logDebug(log, () -> {
				if(ai.getItemsWrapper() != null && ai.getItemsWrapper().getItems() != null) {
					List<Item> items = ai.getItemsWrapper().getItems();
					return String.format("[%d] Items retrieved from Amazon [%s]", items.size(), items); 
				}
				return "No items retrieved from Amazon.";
			});
			
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
	 * Encapsulating the check if the creator of the Item is equals to the artistName.
	 * to remove all the Null checkings from the 
	 * stream filter.
	 * 
	 * @param i
	 * @param artistName
	 * @return
	 */
	private boolean isItemCreatorEqualsTo(Item i, String artistName) {
		artistName = StringUtils.nullIsEmpty(artistName);
		return i.getItemAttributes() != null 
				&& i.getItemAttributes().getCreator() != null 
				&& PRIMARYCONTRIBUTOR_ROLE.equals(i.getItemAttributes().getCreator().getRole())
				&& artistName.equalsIgnoreCase(i.getItemAttributes().getCreator().getValue());
	}
	
	/**
	 * Encapsulating  the check if the Item title is equal to the expected name.
	 * 
	 * @param item
	 * @param name
	 * @return
	 */
	private boolean isItemTitleEqualsTo(Item item, String name) {
		name = StringUtils.nullIsEmpty(name);
		return item.getItemAttributes() != null && name.equalsIgnoreCase(item.getItemAttributes().getTitle());
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
		
		String productGroup = StringUtils.nullIsEmpty(ia.getProductGroup());
		Matcher m = Pattern.compile(MUSICWORD_REGEX).matcher(productGroup);
		return m.find();
	}
	
	/**
	 * Check that the Amazon Item contains the largest
	 * Item Image.
	 */
	private static boolean hasCoverArt(Item i) {
		Image largeImage = i.getLargeImage();
		return largeImage != null && StringUtils.exists(largeImage.getUrl()); 
	}
	
	private static boolean isBigEnough(Item i) {
		Image largeImage = i.getLargeImage();
		return ImageUtil.isBigEnough(largeImage.getWidth(), largeImage.getHeight(), largeImage.getUrl());
	}
	
	private static CoverArt buildCoverArt(Item i) {
		LoggerUtils.logDebug(log, () -> "Building CoverArt from Amazon Item=" + i);
		return new CoverArt.Builder()
				.mediumUri(i.getLargeImageUrl())
				.smallUri(i.getMediumImageUrl())
				.tinyUri(i.getSmallImageUrl())
				.build();
	}
	
	private static class ImageComparator implements Comparator<Item> {
		@Override
		public int compare(Item it1, Item it2) {
			Image i1 = it1.getLargeImage();
			Image i2 = it2.getLargeImage();
			int a1 = i1.getWidth() * i1.getHeight();
			int a2 = i2.getWidth() * i2.getHeight();
			return Integer.compare(a1, a2);
		}
	}
}
