package bo.roman.radio.utilities;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bo.roman.radio.cover.album.CoverArtArchiveFinder;

public class ImageUtil {
	private final static Logger LOG = LoggerFactory.getLogger(CoverArtArchiveFinder.class);
	
	private static final int MIN_HEIGHT = 300;
	private static final int MIN_WIDTH = 300;
	
	public static boolean isBigEnough(String imageUri) {
		if(!StringUtils.exists(imageUri)) {
			return false;
		}
		
		try {
			URL url = new URI(imageUri).toURL();
			try(ImageInputStream in = ImageIO.createImageInputStream(url.openStream())) {
				Iterator<ImageReader> readers = ImageIO.getImageReaders(in);
				while(readers.hasNext()) {
					ImageReader reader = readers.next();
					try {
						reader.setInput(in);
						return isBigEnough(reader.getWidth(0), reader.getHeight(0));
					} finally {
						reader.dispose();
					}
				}
				
				return false;
			} 
		} catch (IOException | URISyntaxException e) {
			LOG.error("There was an error getting the image from {}", imageUri, e);
			return false;
		}
	}
	
	public static boolean isBigEnough(int w, int h) {
		LoggerUtils.logDebug(LOG, () -> String.format("Size [%d x %d]", w, h));
		return w >= MIN_WIDTH && h >= MIN_HEIGHT;
	}

}
