package bo.roman.radio.cover.album;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bo.roman.radio.cover.model.Album;
import bo.roman.radio.utilities.HttpUtils;

public class AmazonUtil {
	private final static Logger log = LoggerFactory.getLogger(AmazonUtil.class);
	
	// This is the type of request method to send to Amazon
	private final static String REQUEST_METHOD = "GET";
	
	// This is the store where the covers will be retrieved from
	private final static String AMAZONSTORE_SITE = "ecs.amazonaws.com";
	
	// This is the path that accepts REST request
	private final static String REST_PATH = "/onca/xml";
	
	// Query templates
	// Search Album by name
	private final static String SEARCHQUERY_TEMPLATE = "AWSAccessKeyId=%s"
			+ "&Artist='%s'"
			+ "&AssociateTag=%s"
			+ "&Operation=ItemSearch"
			+ "&ResponseGroup=Images,ItemAttributes"
			+ "&SearchIndex=Music"
			+ "&Service=AWSECommerceService"
			+ "&Timestamp=%s"
			+ "&Title='%s'";
	
	// Search Album by keyword
	private final static String SEARCHALLQUERY_TEMPLATE = "AWSAccessKeyId=%s"
			+ "&AssociateTag=%s"
			+ "&Keywords=%s"
			+ "&Operation=ItemSearch"
			+ "&ResponseGroup=Images,ItemAttributes"
			+ "&SearchIndex=All"
			+ "&Service=AWSECommerceService"
			+ "&Timestamp=%s";
	
	// Date pattern accepted by Amazon
	private static final String DATE_PATTERN = "yyyy-MM-dd'T'HH:mm:ss'Z'";
	
	// Algorithm used to generate the encripted signature
	private static final String HMAC_SHA256_ALGORITHM = "HmacSHA256";
	
	/**
	 * 
	 * @param keyword
	 * @return
	 */
	public static String generateSearchAllRequestUrl(Album album) {
		String keyword = String.format("%s,%s", album.getSongName(), album.getArtistName());
		String requestQuery = String.format(SEARCHALLQUERY_TEMPLATE
				, getAwsAccessKeyId()
				, getAwsAssociateTag()
				, keyword
				, getTimeStamp());
		
		// Generate the URL to send the REST request
		return  "http://" + AMAZONSTORE_SITE + REST_PATH + "?" + requestQuery + "&Signature=" + generateSignature(requestQuery);
	}
		
	/**
	 * Method used to generate the URL to which 
	 * a REST request will be sent to Amazon.
	 * 
	 * @param artistName
	 * @param albumName
	 * @return
	 */
	public static String generateSearchAlbumRequestUrl(Album album) {
		String requestQuery = String.format(SEARCHQUERY_TEMPLATE
				, getAwsAccessKeyId()
				, album.getArtistName()
				, getAwsAssociateTag()
				, getTimeStamp()
				, album.getName());
		
		// Generate the URL to send the REST request
		return  "http://" + AMAZONSTORE_SITE + REST_PATH + "?" + requestQuery + "&Signature=" + generateSignature(requestQuery);
	}
	
	private static String generateSignature(String reqQuery){
		String awsSecretKey = getAwsSecretKey();
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

	private static String getAwsSecretKey() {
		// TODO Auto-generated method stub
		return null;
	}
	
	private static String getAwsAssociateTag() {
		// TODO Auto-generated method stub
		return null;
	}

	private static String getAwsAccessKeyId() {
		// TODO Auto-generated method stub
		return null;
	}

	private static String getTimeStamp () {
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern(DATE_PATTERN);
		return dtf.format(LocalDateTime.now());
	}

}
