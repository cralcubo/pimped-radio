package bo.roman.radio.cover.model.mapping;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="ItemSearchResponse", namespace="http://webservices.amazon.com/AWSECommerceService/2011-08-01")
public class AmazonItems {
	
	private List<Item> items;

	public List<Item> getItems() {
		return items;
	}
	@XmlElementWrapper(name = "Items")
	@XmlElement(name="Item")
	public void setItems(List<Item> items) {
		this.items = items;
	}
	
	@XmlRootElement(name="Item")
	public static class Item {
		private String asin;
		
		private String title;
		
		private Image largeImage;
		
		private Image mediumImage;
		
		private Image smallImage;

		public String getAsin() {
			return asin;
		}
		@XmlElement(name="ASIN")
		public void setAsin(String asin) {
			this.asin = asin;
		}

		public String getTitle() {
			return title;
		}
		@XmlElement(name="Title")
		public void setTitle(String title) {
			this.title = title;
		}
		
		public Image getLargeImage() {
			return largeImage;
		}
		
		public String getLargeImageUrl() {
			if(largeImage != null) {
				return largeImage.getUrl();
			}
			
			return "";
		}
		@XmlElement(name="LargeImage")
		public void setLargeImage(Image largeImage) {
			this.largeImage = largeImage;
		}

		public Image getMediumImage() {
			return mediumImage;
		}
		
		public String getMediumImageUrl() {
			if(mediumImage != null) {
				return mediumImage.getUrl();
			}
			
			return "";
		}
		@XmlElement(name="MediumImage")
		public void setMediumImage(Image mediumImage) {
			this.mediumImage = mediumImage;
		}

		public Image getSmallImage() {
			return smallImage;
		}
		
		public String getSmallImageUrl() {
			if(smallImage != null) {
				return smallImage.getUrl();
			}
			
			return "";
		}
		
		@XmlElement(name="SmallImage")
		public void setSmallImage(Image smallImage) {
			this.smallImage = smallImage;
		}
		
		@Override
		public String toString() {
			return "Item [asin=" + asin + ", title=" + title + ", largeImage=" + largeImage + ", mediumImage="
					+ mediumImage + ", smallImage=" + smallImage + "]";
		}

		public static class Image {
			
			String url;
			int height;
			int width;
			
			public String getUrl() {
				return url;
			}
			@XmlElement(name="URL")
			public void setUrl(String url) {
				this.url = url;
			}
			public int getHeight() {
				return height;
			}
			@XmlElement(name="Height")
			public void setHeight(int height) {
				this.height = height;
			}
			public int getWidth() {
				return width;
			}
			@XmlElement(name="Width")
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
