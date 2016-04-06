package bo.roman.radio.cover.model.mapping;

import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

import bo.roman.radio.utilities.StringUtils;

@XmlRootElement(name="ItemSearchResponse", namespace="http://webservices.amazon.com/AWSECommerceService/2011-08-01")
public class AmazonItems {
	
	private ItemsWrapper itemsWrapper;
	
	public ItemsWrapper getItemsWrapper() {
		return itemsWrapper;
	}
	
	@XmlElement(name="Items")
	public void setItemsWrapper(ItemsWrapper items) {
		this.itemsWrapper = items;
	}
	
	@XmlRootElement(name="Items")
	public static class ItemsWrapper {
		private List<Item> items;

		public List<Item> getItems() {
			return items;
		}
		
		@XmlElement(name="Item")
		public void setItems(List<Item> items) {
			this.items = items;
		}
		
		@XmlRootElement(name="Item")
		public static class Item {
			private ItemAttributes itemAttributes;
			
			private String asin;
			
			private Image largeImage;
			
			private Image mediumImage;
			
			private Image smallImage;
			
			public ItemAttributes getItemAttributes() {
				return itemAttributes;
			}
			
			@XmlElement(name="ItemAttributes")
			public void setItemAttributes(ItemAttributes itemAttributes) {
				this.itemAttributes = itemAttributes;
			}

			public String getAsin() {
				return asin;
			}
			@XmlElement(name="ASIN")
			public void setAsin(String asin) {
				this.asin = asin;
			}
			
			public Image getLargeImage() {
				return largeImage;
			}
			
			public String getLargeImageUrl() {
				if(largeImage != null) {
					return largeImage.getUrl().trim();
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
				return "Item [itemAttributes=" + itemAttributes + ", asin=" + asin + ", largeImage=" + largeImage
						+ ", mediumImage=" + mediumImage + ", smallImage=" + smallImage + "]";
			}
			
			@XmlRootElement(name="ItemAttributes")
			public static class ItemAttributes {
				private String title;
				private Creator creator;
				
				public void setTitle(String title) {
					this.title = title;
				}
				
				@XmlElement(name="Title")
				public String getTitle() {
					return title;
				}
				@XmlElement(name="Creator")
				public void setCreator(Creator creator) {
					this.creator = creator;
				}
				
				public Creator getCreator() {
					return creator;
				}

				@Override
				public String toString() {
					return "ItemAttributes [title=" + title + ", creator=" + creator + "]";
				}
				
				public static class Creator {
					private String role;
					private String value;
					
					public String getRole() {
						return role;
					}
					@XmlAttribute(name="Role")
					public void setRole(String role) {
						this.role = role;
					}
					
					public String getValue() {
						return value;
					}
					@XmlValue
					public void setValue(String value) {
						this.value = value;
					}
					
					@Override
					public String toString() {
						return "Creator [role=" + role + ", value=" + value + "]";
					}
					
				}
				
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
					this.url = StringUtils.nullIsEmpty(url).trim();
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

}
