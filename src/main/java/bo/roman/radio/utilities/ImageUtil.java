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
	
	private static final int MIN_SIZE = 300;
	private static final float MAXRATIO_DIFF = 0.1f;
	
	
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
		
		// Calculate the Width - Height ratio to avoid rectangular images.
		// Acceptable ratio_diff will be 0.1 so the images are as square as possible.
		float ratio_diff = Math.abs(w * 1.0f/ h - 1.0f);
		LoggerUtils.logDebug(logger, () -> String.format("Size [w=%d x h=%d] ratio_diff=%.2f for image [%s]", w, h, ratio_diff, imageIdentifier));
		return ratio_diff <= MAXRATIO_DIFF && w >= MIN_SIZE && h >= MIN_SIZE;
	}

}
