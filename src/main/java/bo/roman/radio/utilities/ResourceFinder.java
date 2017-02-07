package bo.roman.radio.utilities;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ResourceFinder {

	public static URL findFileUrl(String resourceName) {
		try {
			return findFileUri(resourceName).toURL();
		} catch (MalformedURLException e) {
			return null;
		}
	}
	
	public static URI findFileUri(String resourceName)
	{
		Path p = Paths.get(resourceName);
		return p.toUri();
	}

	public static String findFilePath(String resourceName) {
		Path p = Paths.get(resourceName);
		return p.toFile().getAbsolutePath();
	}
	
}
