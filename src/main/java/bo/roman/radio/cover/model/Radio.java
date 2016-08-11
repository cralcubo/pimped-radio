package bo.roman.radio.cover.model;

import static bo.roman.radio.utilities.StringUtils.nullIsEmpty;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import bo.roman.radio.utilities.StringUtils;

public class Radio {
	
	private static final String PAGELOGO_TEMPLATE = "https://graph.facebook.com/%s/picture?width=500&height=500";
	
	private String name;
	private String id;
	private String category;
	private String streamUrl;
	private Picture picture;
	private Optional<URI> logoUri;
	
	public Radio(String name, Optional<URI> logoUri){
		this.name = name;
		this.logoUri = logoUri;
	}
	
	private Radio(Builder b) {
		name = b.name;
		id = b.id;
		category = b.category;
		streamUrl = b.streamUrl;
		picture = b.picture;
		logoUri = b.logoUri;
	}

	public String getName() {
		return name;
	}

	public String getId() {
		return id;
	}

	public String getCategory() {
		return category;
	}
	
	public String getStreamUrl() {
		return streamUrl;
	}
	
	/**
	 * If the facebook id of the 
	 * Radio Page is present, we can return
	 * the URL of the logo of the page.
	 * The format of the URL is:
	 * <center>https://graph.facebook.com/PAGE_ID/picture?type=SIZE</center>
	 * Where size can be:
	 * <ul>
	 * <li>square</li>
	 * <li>normal</li>
	 * <li>small</li>
	 * <li>large</li>
	 * </ul> 
	 * 
	 * In case any exception is thrown, the logoUri will be empty.
	 * 
	 * @return the URI of the Page Logo.
	 */
	public Optional<URI> getLogoUri() {
		if (StringUtils.exists(id)) {
			try {
				logoUri = Optional.of(new URI(String.format(PAGELOGO_TEMPLATE, id)));
			} catch (URISyntaxException e) {
				logoUri = Optional.empty();
			}
		}

		return logoUri;
	}
	
	/**
	 * If logo was found for this radio
	 * then is_silhouette == false
	 * @return
	 */
	public boolean hasLogo() {
		if(picture != null && picture.getData() != null) {
			return !Boolean.valueOf(picture.getData().get("is_silhouette").toString());
		}
		
		return false;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setPicture(Picture picture) {
		this.picture = picture;
	}
	
	public void setStreamUrl(String streamUrl) {
		this.streamUrl = streamUrl;
	}
	
	@Override
	public String toString() {
		return "Radio [name=" + name + ", id=" + id + ", category=" + category + ", logoUrl=" + getLogoUri() + ", streamUrl=" + streamUrl + "]";
	}
		
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Radio other = (Radio) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	public static class Builder {
		private String name;
		private String id;
		private String category;
		private String streamUrl;
		private Picture picture;
		private Optional<URI> logoUri;
		
		public Builder logoUri(Optional<URI> val) {
			this.logoUri = val;
			return this;
		}
		
		public Builder picture(Picture val) {
			this.picture = val;
			return this;
		}
		
		public Builder streamUrl(String val) {
			this.streamUrl = nullIsEmpty(val);
			return this;
		}
		
		public Builder name(String val) {
			name = nullIsEmpty(val);
			return this;
		}
		
		public Builder id(String val) {
			id = nullIsEmpty(val);
			return this;
		}
		
		public Builder category(String val) {
			category = nullIsEmpty(val);
			return this;
		}
		
		public Radio build() {
			return new Radio(this);
		}
		
	}
	
	public static class Radios {
		private List<Radio> data;

		public Radios(List<Radio> data) {
			super();
			this.data = data;
		}

		public List<Radio> getData() {
			return data;
		}

		public void setData(List<Radio> data) {
			this.data = data;
		}
	}
	
	public static class Picture {
		private Map<String, Object> data;

		public Picture(Map<String, Object> data) {
			super();
			this.data = data;
		}
		
		public Map<String, Object> getData() {
			return data;
		}

		public void setData(Map<String, Object> data) {
			this.data = data;
		}

		@Override
		public String toString() {
			return "Picture [data=" + data + "]";
		}
		
	}
	
}
