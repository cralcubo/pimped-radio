package bo.roman.radio.cover.album;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.hamcrest.text.IsEmptyString;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import bo.roman.radio.cover.model.Album;
import bo.roman.radio.utilities.SecretFileProperties;

@RunWith(PowerMockRunner.class)
@PrepareForTest(SecretFileProperties.class)
@PowerMockIgnore("javax.crypto.*") 
public class AmazonConnectionUtilTest {
	private static final String PARAMETERSREGEX_TMPL = "(?<=%s=)('.+?'|[^&]+)";
	
	private String awsSecretKey = "aSecretKey";
	private String associateTag = "aTag";
	private String awsAccessKeyId = "anAccessKeyId";
	
	@Before
	public void setUp() {
		PowerMockito.mockStatic(SecretFileProperties.class);
	}
	
	@SuppressWarnings("unchecked")
	@Test(expected=RuntimeException.class)
	public void testNoAccessKeys_searchAll() {
		String song = "testSong";
		String artist = "testArtist";
		
		// Prepare Mock
		PowerMockito.when(SecretFileProperties.get("amazon.awsAccessKeyId")).thenThrow(RuntimeException.class);
		
		AmazonConnectionUtil.generateSearchByKeywordRequestUrl(String.format("%s,%s", song,artist));
	}
	
	@SuppressWarnings("unchecked")
	@Test(expected=RuntimeException.class)
	public void testNoAccessKeys_searchAlbum() {
		String song = "testSong";
		String artist = "testArtist";
		String albName = "testAlbum";
		Album album = new Album.Builder()
				.songName(song)
				.artistName(artist)
				.name(albName)
				.build();
		
		// Prepare Mock
		PowerMockito.when(SecretFileProperties.get("amazon.awsAccessKeyId")).thenThrow(RuntimeException.class);
		
		AmazonConnectionUtil.generateSearchByAlbumRequestUrl(album);
	}
	
	@Test
	public void testGenerateSearchAlbumRequestUrl() {
		Album album = setUpGenerateUrlTest();
		
		// Run the method
		String url = AmazonConnectionUtil.generateSearchByAlbumRequestUrl(album);
		
		// Assert that all the parameters are present
		String pAWSAccessKeyId = "AWSAccessKeyId";
		assertThat(getParameter(pAWSAccessKeyId, url), is(equalTo(awsAccessKeyId)));
		
		String pArtist = "Artist";
		assertThat(getParameter(pArtist, url), is(equalTo(String.format("'%s'", album.getArtistName()))));
		
		String pAssociateTag = "AssociateTag";
		assertThat(getParameter(pAssociateTag, url), is(equalTo(associateTag)));
		
		String pOperation = "Operation";
		assertThat(getParameter(pOperation, url), is(equalTo("ItemSearch")));
		
		String pResponseGroup = "ResponseGroup";
		assertThat(getParameter(pResponseGroup, url), is(equalTo("Images,ItemAttributes")));
		
		String pSearchIndex = "SearchIndex";
		assertThat(getParameter(pSearchIndex, url), is(equalTo("Music")));
		
		String pService = "Service";
		assertThat(getParameter(pService, url), is(equalTo("AWSECommerceService")));
		
		String pTimestamp = "Timestamp";
		assertThat(getParameter(pTimestamp, url), not(IsEmptyString.isEmptyOrNullString()));
		
		String pTitle = "Title";
		assertThat(getParameter(pTitle, url), is(equalTo(String.format("'%s'", album.getName()))));
		
		String pSignature = "Signature";
		assertThat(getParameter(pSignature, url), not(IsEmptyString.isEmptyOrNullString()));
	}
	
	@Test
	public void testGenereateSearchAllRequest() {
		Album album = setUpGenerateUrlTest();
		
		// Run the method
		String url = AmazonConnectionUtil.generateSearchByKeywordRequestUrl(String.format("%s,%s", album.getSongName(), album.getArtistName()));
		
		// Assert that all the parameters are present
		String pAWSAccessKeyId = "AWSAccessKeyId";
		assertThat(getParameter(pAWSAccessKeyId, url), is(equalTo(awsAccessKeyId)));
		
		String pAssociateTag = "AssociateTag";
		assertThat(getParameter(pAssociateTag, url), is(equalTo(associateTag)));
		
		String pKeywords = "Keywords";
		assertThat(getParameter(pKeywords, url), is(equalTo(String.format("'%s,%s'", album.getSongName(), album.getArtistName()))));
		
		String pOperation = "Operation";
		assertThat(getParameter(pOperation, url), is(equalTo("ItemSearch")));
		
		String pRelationship = "RelationshipType";
		assertThat(getParameter(pRelationship, url), is(equalTo("Tracks")));
		
		String pResponseGroup = "ResponseGroup";
		assertThat(getParameter(pResponseGroup, url), is(equalTo("RelatedItems,Images,Small")));
		
		String pSearchIndex = "SearchIndex";
		assertThat(getParameter(pSearchIndex, url), is(equalTo("All")));
		
		String pService = "Service";
		assertThat(getParameter(pService, url), is(equalTo("AWSECommerceService")));
		
		String pTimestamp = "Timestamp";
		assertThat(getParameter(pTimestamp, url), not(IsEmptyString.isEmptyOrNullString()));
		
		String pSignature = "Signature";
		assertThat(getParameter(pSignature, url), not(IsEmptyString.isEmptyOrNullString()));
	}
	
	/* *** Utilities *** */
	
	private Album setUpGenerateUrlTest() {
		String song = "testSong";
		String artist = "testArtist";
		String albName = "testAlbum";
		Album album = new Album.Builder()
				.songName(song)
				.artistName(artist)
				.name(albName)
				.build();
		
		// Prepare Mock
		PowerMockito.when(SecretFileProperties.get("amazon.awsSecretKey")).thenReturn(awsSecretKey);
		PowerMockito.when(SecretFileProperties.get("amazon.associateTag")).thenReturn(associateTag);
		PowerMockito.when(SecretFileProperties.get("amazon.awsAccessKeyId")).thenReturn(awsAccessKeyId);
		
		return album;
	}

	private String getParameter(String parameter, String url) {
		String regEx = String.format(PARAMETERSREGEX_TMPL, parameter);
		Matcher matcher = Pattern.compile(regEx).matcher(url);
		if(matcher.find()) {
			return matcher.group();
		}
		
		return null;
	}

}
