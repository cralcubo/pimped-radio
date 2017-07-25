package bo.roman.radio.cover.album.last.fm;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public interface LastFmTestsUtils {
	
	final static String RESOURCES_PATH = "src/test/resources/lastFm/";
	
	static String getJsonResponse(String path) throws IOException {
		Path p = Paths.get(path);
		return Files.lines(p, StandardCharsets.UTF_8).reduce("", String::concat);
	}

}
