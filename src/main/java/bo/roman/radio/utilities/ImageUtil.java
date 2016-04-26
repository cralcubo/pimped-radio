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

public class ImageUtil {
	private final static Logger logger = LoggerFactory.getLogger(ImageUtil.class);
	
	private static final int MIN_HEIGHT = 300;
	private static final int MIN_WIDTH = 300;
	
	/**
	 * Method that will find the width and height of an image
	 * and will compare it with the MIN_HEIGHT and MIN_WIDTH
	 * set in this class.
	 * 
	 * @param imageUri
	 * @return If the size is bigger or equal to the MIN expected
	 * the method returns TRUE.
	 * 
	 * @throws URISyntaxException if an invalid URI is passed.
	 * @throws IOException if an image cannot be read.
	 */
	public static boolean isBigEnough(String imageUri) throws URISyntaxException, IOException {
		if(!StringUtils.exists(imageUri)) {
			throw new IllegalArgumentException("There is no URI to check size of image.");
		}
		
		URL url = new URI(imageUri).toURL();
		try(ImageInputStream in = ImageIO.createImageInputStream(url.openStream())) {
			Iterator<ImageReader> readers = ImageIO.getImageReaders(in);
			while(readers.hasNext()) {
				ImageReader reader = readers.next();
				try {
					reader.setInput(in);
					return isBigEnough(reader.getWidth(0), reader.getHeight(0), imageUri);
				} finally {
					reader.dispose();
				}
			}
			
			throw new IOException("Could not read image.");
		}
	}
	
	public static boolean isBigEnough(int w, int h, String imageIdentifier) {
		LoggerUtils.logDebug(logger, () -> String.format("Size [%d x %d] for image [%s]", w, h, imageIdentifier));
		return w >= MIN_WIDTH && h >= MIN_HEIGHT;
	}

}
