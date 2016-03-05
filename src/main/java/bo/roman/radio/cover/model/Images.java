package bo.roman.radio.cover.model;

import java.util.List;

/**
 * Class that maps the response JSON object
 * gotten from 'coverartarchive'
 * 
 * @author christian
 *
 */
public class Images {

	private List<Image> images;
	
	public Images(List<Image> images) {
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
			this.image = image;
		}

		@Override
		public String toString() {
			return "Image [front=" + front + ", image=" + image + "]";
		}
	}
}
