package bo.roman.radio.cover.album;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bo.roman.radio.cover.model.Album;
import bo.roman.radio.cover.model.CoverArt;
import bo.roman.radio.cover.model.mapping.AmazonItems;
import bo.roman.radio.utilities.HttpUtils;
import bo.roman.radio.utilities.LoggerUtils;
import bo.roman.radio.utilities.StringUtils;

public class AmazonCoverFinder implements CoverArtFindable {
	private final static Logger log = LoggerFactory.getLogger(AmazonCoverFinder.class);
	
	// This is the type of request method to send to Amazon
	private final static String REQUEST_METHOD = "GET";
	// This is the store where the covers will be retrieved from
	private final static String AMAZONSTORE_SITE = "ecs.amazonaws.com";
	// This is the path that accepts REST requets
	private final static String REST_PATH = "/onca/xml";
	// Date pattern accepted by Amazon
	private static final String DATE_PATTERN = "yyyy-MM-dd'T'HH:mm:ss'Z'";
	// Algorithm used to generate the encripted signature
	private static final String HMAC_SHA256_ALGORITHM = "HmacSHA256";
	
	// Query template
	private final static String REQUESTQUERY_TEMPLATE = "AWSAccessKeyId=%s"
			+ "&Artist='%s'"
			+ "&AssociateTag=%s"
			+ "&Operation=ItemSearch"
			+ "&ResponseGroup=Images,ItemAttributes"
			+ "&SearchIndex=Music"
			+ "&Service=AWSECommerceService"
			+ "&Timestamp=%s"
			+ "&Title='%s'";
	
	private final String awsAccessKeyId;
	private final String awsSecretKey;
	private final String awsAssociateTag;
	
	public AmazonCoverFinder(String awsAccessKeyId, String awsSecretKey, String awsAssociateTag) {
		this.awsAccessKeyId = awsAccessKeyId;
		this.awsSecretKey = awsSecretKey;
		this.awsAssociateTag = awsAssociateTag;
	}

	@Override
	public Optional<CoverArt> findCoverUrl(Album album) throws IOException {
		if(album == null) {
			LoggerUtils.logDebug(log, () -> "There is no Album to search its Cover Art in Amazon.");
			return Optional.empty();
		}
		
		String artistName = album.getArtistName();
		String albumName = album.getName();
		
		if(!StringUtils.exists(artistName) || !StringUtils.exists(albumName)) {
			LoggerUtils.logDebug(log, () -> "There is no artistName or albumName to search a Cover Art in Amazon.");
			return Optional.empty();
		}
		
		String requestQuery = String.format(REQUESTQUERY_TEMPLATE
				, awsAccessKeyId
				, artistName
				, awsAssociateTag
				, getTimeStamp()
				, albumName);
		
		// Generate the URL to send the REST request
		String url = "http://" + AMAZONSTORE_SITE + REST_PATH + "?" + requestQuery + "&Signature=" + generateSignature(requestQuery);
		LoggerUtils.logDebug(log, () -> String.format("Sending Product information request to Amazon. URL[%]", url));
		String xmlResponse = HttpUtils.doGet(url);
		
		// Convert XML the Object
		Optional<AmazonItems> oItems = unmarshalXml(xmlResponse);
		
		if(!oItems.isPresent()) {
			log.info("No Amazon Music for the Album[{}] could be found.", album);
			return Optional.empty();
		}
		
		// Get the images found and assemble a CoverArt object
//		oItems.get().getItems().stream()
//		.findFirst()
//		.map(i -> new CoverArt.Builder()
//				.largeUri(i.get))
		
		
		return null;
	}
	
	private Optional<AmazonItems> unmarshalXml(String xmlResponse) {
		try {
			JAXBContext context = JAXBContext.newInstance(AmazonItems.class);
			Unmarshaller unmarshaller = context.createUnmarshaller();
			AmazonItems ai = (AmazonItems) unmarshaller.unmarshal(new ByteArrayInputStream(xmlResponse.getBytes(HttpUtils.UTF_8)));
			return Optional.of(ai);
		} 
		catch( UnsupportedEncodingException e) {
			throw new RuntimeException(String.format("Totally unexpected, %s is supposed to be an accepted character encoding.", HttpUtils.UTF_8), e);
		}
		catch (JAXBException e) {
			LoggerUtils.logDebug(log, () -> "Couldnt unmarshal XML=" + xmlResponse, e);
			return Optional.empty();
		}
	}

	private String generateSignature(String reqQuery){
		String toSign = REQUEST_METHOD + "\n"
				+ AMAZONSTORE_SITE + "\n"
				+ REST_PATH + "\n"
				+ HttpUtils.encodeParameters(reqQuery);
		
		byte[] secretKeyBytes;
		byte[] data;
		try {
			secretKeyBytes = awsSecretKey.getBytes(HttpUtils.UTF_8);
			data = toSign.getBytes(HttpUtils.UTF_8);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(String.format("Totally unexpected, %s is supposed to be an accepted character encoding.", HttpUtils.UTF_8), e);
		}
		
		SecretKeySpec sks = new SecretKeySpec(secretKeyBytes,HMAC_SHA256_ALGORITHM);
		
		Mac mac;
		try {
			mac = Mac.getInstance(HMAC_SHA256_ALGORITHM);
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(String.format("Totally unexpected, %s is supposed to be a valid algorithm.", HMAC_SHA256_ALGORITHM), e);
		}
		
		try {
			mac.init(sks);
		} catch (InvalidKeyException e) {
			log.error("The secret key [{}] is invalid. Verify that the Amazon secret was correctly set.", awsSecretKey, e);
			return "";
		}
		
		byte[] rawHmac = mac.doFinal(data);
		Base64 encoder = new Base64();
		
		return new String(encoder.encode(rawHmac));
	}
	
	private String getTimeStamp () {
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern(DATE_PATTERN);
		return dtf.format(LocalDateTime.now());
	}
}
