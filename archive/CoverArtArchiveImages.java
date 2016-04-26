package bo.roman.radio.cover.model.mapping;

import java.util.List;
import java.util.Map;

import bo.roman.radio.utilities.StringUtils;

/**
 * Class that maps the response JSON object
 * gotten from 'coverartarchive'
 * 
 * @author christian
 *
 */
public class CoverArtArchiveImages {

	private List<Image> images;
	
	public CoverArtArchiveImages(List<Image> images) {
		this.images = images;
	}

	public List<Image> getImages() {
		return images;
	}

	public void setImages(List<Image> images) {
		this.images = images;
	}

	public static class Image {

		private boolean front;
		private String image;
		private Map<String, String> thumbnails;

		public Image(boolean front, String image) {
			this.front = front;
			this.image = image;
		}

		public boolean isFront() {
			return front;
		}

		public void setFront(boolean front) {
			this.front = front;
		}

		public String getImage() {
			return image;
		}

		public void setImage(String image) {
			this.image = StringUtils.cleanIt(image);
		}
		
		public Map<String, String> getThumbnails() {
			return thumbnails;
		}
		
		public void setThumbnails(Map<String, String> thumbnails) {
			this.thumbnails = thumbnails;
		}

		@Override
		public String toString() {
			return "Image [front=" + front + ", image=" + image + ", thumbnails=" + thumbnails + "]";
		}

	}
}
