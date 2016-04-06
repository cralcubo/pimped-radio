package bo.roman.radio.cover.album;

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
import bo.roman.radio.utilities.StringUtils;

public class AmazonCoverFinder implements CoverArtFindable {
	private final static Logger log = LoggerFactory.getLogger(AmazonCoverFinder.class);

	@Override
	public Optional<CoverArt> findCoverArt(Album album) throws IOException {
		if(album == null) {
			log.info("There is no Album to search its Cover Art in Amazon.");
			return Optional.empty();
		}
		
		String artistName = album.getArtistName();
		String albumName = album.getName();
		
		if(!StringUtils.exists(artistName) || !StringUtils.exists(albumName)) {
			log.info("There is no artistName or albumName to search a Cover Art in Amazon.");
			return Optional.empty();
		}
		
		// Generate the URL to send the REST request
		String url = AmazonUtil.generateGetRequestUrl(artistName, albumName);
		
		LoggerUtils.logDebug(log, () -> String.format("Sending Product information request to Amazon. URL[%s]", url));
		String xmlResponse = HttpUtils.doGet(url);
		
		// Convert XML the Object
		Optional<AmazonItems> oItems = unmarshalXml(xmlResponse);
		
		if(!oItems.isPresent() 
				|| !oItems.map(AmazonItems::getItemsWrapper).isPresent() 
				|| !oItems.map(AmazonItems::getItemsWrapper).map(ItemsWrapper::getItems).isPresent()) {
			
			log.info("No Amazon Items for the {} could be found.", album);
			return Optional.empty();
		}
		
		List<Item> allItems = oItems.map(AmazonItems::getItemsWrapper).map(ItemsWrapper::getItems).get();
		
		// Find if there is an Amazon Item that matches the name of the album. 
		Optional<Item> bestItem = allItems.stream()
				.filter(i -> i.getItemAttributes() != null)
				.filter(i -> i.getItemAttributes().getTitle().equalsIgnoreCase(albumName))
				.findFirst();
		
		if(bestItem.isPresent()) {
			Optional<CoverArt> ca = bestItem.map(AmazonCoverFinder::buildCoverArt);
			log.info("The best Amazon CoverArt for [{}] is {}", album, ca);
			return ca;
		}
		
		// In case there was no match found to the name of the album, return the first Amazon Item.
		Optional<CoverArt> coverArt = allItems.stream()
				.findFirst()
				.map(AmazonCoverFinder::buildCoverArt);
		log.info("CoverArt found for {} in Amazon. {}", album, coverArt);
		return coverArt;
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
	
	private static CoverArt buildCoverArt(Item i) {
		LoggerUtils.logDebug(log, () -> "Building CoverArt from Amazon Item=" + i);
		return new CoverArt.Builder()
				.largeUri(i.getLargeImageUrl())
				.mediumUri(i.getMediumImageUrl())
				.smallUri(i.getSmallImageUrl())
				.build();
	}
}
