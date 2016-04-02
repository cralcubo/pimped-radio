package bo.roman.radio.cover.station;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bo.roman.radio.utilities.HttpUtils;
import bo.roman.radio.utilities.StringUtils;

import static bo.roman.radio.utilities.LoggerUtils.*;

/**
 * Class that will use the Facebook Graph API
 * to send requests to Facebook.
 * 
 * @author christian
 *
 */
public class FacebookUtil {
	private final static Logger log = LoggerFactory.getLogger(FacebookUtil.class);
	
	private static final String ACCESSTOKEN_PATH = "secure/token.secret";
	private static final String ACCESS_TOKEN = getAccessToken();
	
	
	private static final String GETQUERY_TEMPLATE = "https://graph.facebook.com/%s&access_token=%s";
	
	public static void main(String[] args) {
		String searchQuery = String.format("q='%s'&type=page&fields=id,name,category&limit=%s", "radio paradise", 10);
		Optional<String> response = FacebookUtil.doSearch(searchQuery);
		System.out.println(response);
	}

	/** 
	 * Method that will send an HTTP GET
	 * request to Facebook.
	 * The format of the request is:
	 * https://graph.facebook.com/{queryType}
	 * 
	 * @param queryType can be: 
	 * <ul>
	 * <li>search/{searchQuery}</li>
	 * <li>id/{infoType}</li>
	 * </ul>
	 * 
	 * @return a String that is a JSON object.
	 */
	public static Optional<String> doGet(String queryType) {
		String getQuery = String.format(GETQUERY_TEMPLATE, queryType, ACCESS_TOKEN);
		// Do not show the Access_Token in the logs
		logDebug(log, () -> String.format("GET [%s]", hideAccessToken(getQuery)));
		try {
			String response = HttpUtils.doGet(getQuery);
			logDebug(log, () -> "GET response=" + response);
			return Optional.of(response);
		} catch (IOException e) {
			log.error(String.format("The query=[%s] did not return an expected response.", hideAccessToken(getQuery)), e);
		}
		
		return Optional.empty();
	}

	/**
	 * Method that will do a search query to Facebook
	 * the request send to Facebook will have the following format:
	 * search?{searchQuery}
	 * 
	 * @param searchQuery can have the following format:
	 * <i>q={WHAT_IS_BEING_LOOK_FOR}&type={page,user,event,group}&fields={id,name,category}</i>
	 * 
	 * @return a String that is a JSON object.
	 */
	public static Optional<String> doSearch(String searchQuery) {
		String query = String.format("search?%s", searchQuery);
		logDebug(log, () -> "Search query=" + query);
		return FacebookUtil.doGet(query);
	}
	
	
	public static String hideAccessToken(String getQuery) {
		int index = getQuery.indexOf("access_token");
		return getQuery.substring(0, index - 1);
	}
	
	static String getAccessToken() {
		if(StringUtils.exists(ACCESS_TOKEN)) {
			return ACCESS_TOKEN;
		}
		// Read the Access Token
		try {
			List<String> lines = Files.readAllLines(Paths.get(ACCESSTOKEN_PATH));
			if(lines.isEmpty() || lines.size() > 1){
				log.error("The file in {}, does not have an expected token. The token needs to be in the first line of the file.", ACCESSTOKEN_PATH);
				return "";
			}
			
			return lines.get(0);
		} catch (IOException e) {
			log.error("There is no token in {}", ACCESSTOKEN_PATH);
			return "";
		}
	}
}
