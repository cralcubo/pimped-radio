package bo.roman.radio.cover.model.mapping;

import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

import bo.roman.radio.utilities.StringUtils;

/**
 * Class that maps the XML response from Amazon Items.
 * 
 * @author christian
 *
 */
@XmlRootElement(name = "ItemSearchResponse", namespace = "http://webservices.amazon.com/AWSECommerceService/2011-08-01")
public class AmazonItems {

	private ItemsWrapper itemsWrapper;

	public ItemsWrapper getItemsWrapper() {
		return itemsWrapper;
	}

	@XmlElement(name = "Items")
	public void setItemsWrapper(ItemsWrapper items) {
		this.itemsWrapper = items;
	}

	public static class ItemsWrapper {
		private List<Item> items;

		public List<Item> getItems() {
			return items;
		}

		@XmlElement(name = "Item")
		public void setItems(List<Item> items) {
			this.items = items;
		}
		
		public static class Item {
			private RelatedItems relatedItems;
			private ItemAttributes itemAttributes;
			private Tracks tracks;

			private Image largeImage;
			private Image mediumImage;
			private Image smallImage;

			public ItemAttributes getItemAttributes() {
				return itemAttributes;
			}

			@XmlElement(name = "ItemAttributes")
			public void setItemAttributes(ItemAttributes itemAttributes) {
				this.itemAttributes = itemAttributes;
			}

			public Image getLargeImage() {
				return largeImage;
			}

			public String getLargeImageUrl() {
				if (largeImage != null) {
					return largeImage.getUrl().trim();
				}

				return "";
			}

			@XmlElement(name = "LargeImage")
			public void setLargeImage(Image largeImage) {
				this.largeImage = largeImage;
			}

			public Image getMediumImage() {
				return mediumImage;
			}

			public String getMediumImageUrl() {
				if (mediumImage != null) {
					return mediumImage.getUrl();
				}

				return "";
			}

			@XmlElement(name = "MediumImage")
			public void setMediumImage(Image mediumImage) {
				this.mediumImage = mediumImage;
			}

			public Image getSmallImage() {
				return smallImage;
			}

			public String getSmallImageUrl() {
				if (smallImage != null) {
					return smallImage.getUrl();
				}

				return "";
			}

			@XmlElement(name = "SmallImage")
			public void setSmallImage(Image smallImage) {
				this.smallImage = smallImage;
			}
			
			public RelatedItems getRelatedItems() {
				return relatedItems;
			}
			
			@XmlElement(name = "RelatedItems")
			public void setRelatedItems(RelatedItems relatedItems) {
				this.relatedItems = relatedItems;
			}
			
			public Tracks getTracks() {
				return tracks;
			}
			
			@XmlElement(name = "Tracks")
			public void setTracks(Tracks tracks) {
				this.tracks = tracks;
			}

			@Override
			public String toString() {
				return "Item [relatedItems=" + relatedItems + ", itemAttributes=" + itemAttributes + ", largeImage=" + largeImage + ", mediumImage="
						+ mediumImage + ", smallImage=" + smallImage + ", tracks=" + tracks + "]";
			}

			public static class Tracks {

				private List<Disc> discs;
				
				public Tracks() {
					this(null);
				}
				
				public Tracks(List<Disc> discs) {
					this.discs = discs;
				}
				
				public List<Disc> getDiscs() {
					return discs;
				}
				
				@XmlElement(name = "Disc")
				public void setDiscs(List<Disc> discs) {
					this.discs = discs;
				}
				
				@Override
				public String toString() {
					return "Tracks [discs=" + discs + "]";
				}

				public static class Disc {
					private List<String> tracks;
					
					public Disc() {
						this(null);
					}
					
					public Disc(List<String> tracks) {
						this.tracks = tracks;
					}

					public List<String> getTracks() {
						return tracks;
					}
					
					@XmlElement(name = "Track")
					public void setTracks(List<String> tracks) {
						this.tracks = tracks;
					}

					@Override
					public String toString() {
						return "Disc [tracks=" + tracks + "]";
					}
				}
				
			}

			public static class ItemAttributes {
				private String artist;
				private String title;
				private String productGroup;

				private Creator creator;
				
				public ItemAttributes() {
					this(null,null,null,null);
				}
				
				public ItemAttributes(String artist, String title, String productGroup, Creator creator) {
					this.artist = artist;
					this.title = title;
					this.productGroup = productGroup;
					this.creator = creator;
				}

				@XmlElement(name = "Title")
				public void setTitle(String title) {
					this.title = StringUtils.cleanIt(title);
				}

				public String getTitle() {
					return title;
				}

				@XmlElement(name = "Creator")
				public void setCreator(Creator creator) {
					this.creator = creator;
				}

				public Creator getCreator() {
					return creator;
				}

				@XmlElement(name = "ProductGroup")
				public void setProductGroup(String productGroup) {
					this.productGroup = StringUtils.cleanIt(productGroup);
				}

				public String getProductGroup() {
					return productGroup;
				}

				@XmlElement(name = "Artist")
				public void setArtist(String artist) {
					this.artist = StringUtils.cleanIt(artist);
				}

				public String getArtist() {
					return artist;
				}

				@Override
				public String toString() {
					return "ItemAttributes [title=" + title + ", artist=" + artist + ", creator=" + creator
							+ ", productGroup=" + productGroup + "]";
				}

				public static class Creator {
					private String role;
					private String value;

					public Creator() {
						this(null,null);
					}
					
					public Creator(String role, String value) {
						this.role = role;
						this.value = value;
					}

					public String getRole() {
						return role;
					}

					@XmlAttribute(name = "Role")
					public void setRole(String role) {
						this.role = role;
					}

					public String getValue() {
						return value;
					}

					@XmlValue
					public void setValue(String value) {
						this.value = StringUtils.cleanIt(value);
					}

					@Override
					public String toString() {
						return "Creator [role=" + role + ", value=" + value + "]";
					}

				}

			}

			public static class Image {
				private String url;
				private int height;
				private int width;
				
				public Image() {
					this(null, 0 ,0);
				}
				
				public Image(String url, int height, int width) {
					this.url = url;
					this.height = height;
					this.width = width;
				}

				public String getUrl() {
					return url;
				}

				@XmlElement(name = "URL")
				public void setUrl(String url) {
					this.url = StringUtils.cleanIt(url);
				}

				public int getHeight() {
					return height;
				}

				@XmlElement(name = "Height")
				public void setHeight(int height) {
					this.height = height;
				}

				public int getWidth() {
					return width;
				}

				@XmlElement(name = "Width")
				public void setWidth(int width) {
					this.width = width;
				}

				@Override
				public String toString() {
					return "Image [url=" + url + ", height=" + height + ", width=" + width + "]";
				}
			}
			
			public static class RelatedItems {
				private List<RelatedItem> relatedItems;

				public RelatedItems() {
					this(null);
				}
				
				public RelatedItems(List<RelatedItem> relatedItems) {
					this.relatedItems = relatedItems;
				}

				public List<RelatedItem> getRelatedItem() {
					return relatedItems;
				}

				@XmlElement(name = "RelatedItem")
				public void setRelatedItem(List<RelatedItem> relatedItems) {
					this.relatedItems = relatedItems;
				}

				@Override
				public String toString() {
					return "RelatedItems [relatedItem=" + relatedItems + "]";
				}
				
				public static class RelatedItem {
					private Item item;
					
					public RelatedItem() {
						this(null);
					}
					
					public RelatedItem(Item item) {
						this.item = item;
					}

					public Item getItem() {
						return item;
					}

					@XmlElement(name = "Item")
					public void setItem(Item item) {
						this.item = item;
					}

					@Override
					public String toString() {
						return "RelatedItem [item=" + item + "]";
					}
				}
			}
		}
	}

}
