package bo.roman.radio.cover.model.mapping;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="ItemSearchResponse")
public class AmazonItems {
	@XmlElement(name="Items")
	private List<Item> items;

	public List<Item> getItems() {
		return items;
	}

	public void setItems(List<Item> items) {
		this.items = items;
	}

	public static class Item {
		@XmlElement(name="ASIN")
		private String asin;
		
		@XmlElement(name="Title")
		private String title;
		
		@XmlElement(name="LargeImage")
		private Image largeImage;
		
		@XmlElement(name="MediumImage")
		private Image mediumImage;
		
		@XmlElement(name="SmallImage")
		private Image smallImage;

		public String getAsin() {
			return asin;
		}

		public void setAsin(String asin) {
			this.asin = asin;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}
		
		public Image getLargeImage() {
			return largeImage;
		}

		public void setLargeImage(Image largeImage) {
			this.largeImage = largeImage;
		}

		public Image getMediumImage() {
			return mediumImage;
		}

		public void setMediumImage(Image mediumImage) {
			this.mediumImage = mediumImage;
		}

		public Image getSmallImage() {
			return smallImage;
		}

		public void setSmallImage(Image smallImage) {
			this.smallImage = smallImage;
		}


		public static class Image {
			@XmlElement(name="URL")
			String url;
			@XmlElement(name="Height")
			int height;
			@XmlElement(name="Width")
			int width;
			public String getUrl() {
				return url;
			}
			public void setUrl(String url) {
				this.url = url;
			}
			public int getHeight() {
				return height;
			}
			public void setHeight(int height) {
				this.height = height;
			}
			public int getWidth() {
				return width;
			}
			public void setWidth(int width) {
				this.width = width;
			}
			
			@Override
			public String toString() {
				return "Image [url=" + url + ", height=" + height + ", width=" + width + "]";
			}
		}

	}

}
