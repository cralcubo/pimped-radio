package bo.roman.radio.cover.album;

import static bo.roman.radio.utilities.StringUtils.exists;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Optional;

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
import bo.roman.radio.utilities.HttpUtils;
import bo.roman.radio.utilities.LoggerUtils;

public class AmazonCoverFinder implements CoverArtFindable {
	private final static Logger log = LoggerFactory.getLogger(AmazonCoverFinder.class);
	
	public enum SearchType {SEARCHBY_ALBUM, SEARCHBY_KEYWORD, UNKNOWN}

	@Override
	public Optional<CoverArt> findCoverArt(Album album) throws IOException {
		if(album == null) {
			log.info("There is no Album to search its Cover Art in Amazon.");
			return Optional.empty();
		}
		
		// What kind of search will be done in Amazon?
		SearchType searchType = determineSearchType(album);
		// Generate the URL to send the REST request
		final String url;
		switch (searchType) {
		case SEARCHBY_ALBUM:
			url = AmazonUtil.generateSearchAlbumRequestUrl(album);
			break;
		case SEARCHBY_KEYWORD:
			url = AmazonUtil.generateSearchAllRequestUrl(album);
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
				.get();
		
		// Find if there is an Amazon Item that matches the name of the album
		// or the name of the artist if the search was made keyword.
		Optional<Item> bestItem = allItems.stream()
				.filter(i -> isItemTitleEqualsTo(i, album.getName()) || isItemCreatorEqualsTo(i, album.getArtistName())) 
				.findFirst();
		
		if(bestItem.isPresent()) {
			Optional<CoverArt> coverArt = bestItem.map(AmazonCoverFinder::buildCoverArt);
			LoggerUtils.logDebug(log, () -> String.format("The best Amazon CoverArt for [%s] is from %s", album, bestItem));
			log.info("The best Amazon CoverArt for [{}] is {}", album, coverArt);
			return coverArt;
		}
		
		// In case there was no match found to the name of the album, return the first Amazon Item.
		Optional<Item> firstItem = allItems.stream().findFirst();
		Optional<CoverArt> coverArt = firstItem.map(AmazonCoverFinder::buildCoverArt);
		
		LoggerUtils.logDebug(log, () -> String.format("CoverArt found for %s in Amazon from Item %s", album, firstItem));
		log.info("CoverArt found for {} in Amazon is {}", album, coverArt);

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
		LoggerUtils.logDebug(log, () -> "Unmarshalling XML=" + xmlResponse);
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
					return "Items retrieved from Amazon=" + ai.getItemsWrapper().getItems(); 
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
	
	private boolean isItemCreatorEqualsTo(Item i, String artistName) {
		return i.getItemAttributes() != null 
				&& i.getItemAttributes().getCreator() != null 
				&& i.getItemAttributes().getCreator().getRole().equals("Primary Contributor") && i.getItemAttributes().getCreator().getValue().equalsIgnoreCase(artistName);
	}

	private boolean isItemTitleEqualsTo(Item item, String name) {
		return item.getItemAttributes() != null && item.getItemAttributes().getTitle().equalsIgnoreCase(name);
	}
	
	private static CoverArt buildCoverArt(Item i) {
		LoggerUtils.logDebug(log, () -> "Building CoverArt from Amazon Item=" + i);
		return new CoverArt.Builder()
				.largeUri(i.getLargeImageUrl())
				.mediumUri(i.getMediumImageUrl())
				.smallUri(i.getSmallImageUrl())
				.build();
	}
}
