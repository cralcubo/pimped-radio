package bo.roman.radio.cover.model;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CoverArt {
	private final static Logger log = LoggerFactory.getLogger(CoverArt.class);
	
	private Optional<URI> largeUri = Optional.empty();
	private Optional<URI> mediumUri = Optional.empty();
	private Optional<URI> smallUri = Optional.empty();
	private Optional<URI> tinyUri = Optional.empty();
	
	private CoverArt(Builder b) {
		largeUri = b.largeUri;
		mediumUri = b.mediumUri;
		smallUri = b.smallUri;
		tinyUri = b.tinyUri;
	}
	
	public Optional<URI> getLargeUri() {
		return largeUri;
	}
	
	public Optional<URI> getMediumUri() {
		return mediumUri;
	}
	
	public Optional<URI> getSmallUri() {
		return smallUri;
	}
	
	@Override
	public String toString() {
		return "CoverArt [largeUri=" + largeUri + ", mediumUri=" + mediumUri + ", smallUri=" + smallUri + ", tinyUri=" + tinyUri + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((largeUri == null) ? 0 : largeUri.hashCode());
		result = prime * result + ((mediumUri == null) ? 0 : mediumUri.hashCode());
		result = prime * result + ((smallUri == null) ? 0 : smallUri.hashCode());
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
		CoverArt other = (CoverArt) obj;
		if (largeUri == null) {
			if (other.largeUri != null)
				return false;
		} else if (!largeUri.equals(other.largeUri))
			return false;
		if (mediumUri == null) {
			if (other.mediumUri != null)
				return false;
		} else if (!mediumUri.equals(other.mediumUri))
			return false;
		if (smallUri == null) {
			if (other.smallUri != null)
				return false;
		} else if (!smallUri.equals(other.smallUri))
			return false;
		return true;
	}
	
	public static class Builder {
		private Optional<URI> largeUri;
		private Optional<URI> mediumUri;
		private Optional<URI> smallUri;
		private Optional<URI> tinyUri;
		
		public Builder largeUri(String val) {
			largeUri = buildUri(val);
			return this;
		}
		
		public Builder mediumUri(String val) {
			mediumUri = buildUri(val);
			return this;
		}
		
		public Builder smallUri(String val) {
			smallUri = buildUri(val);
			return this;
		}
		
		public Builder tinyUri(String val) {
			tinyUri = buildUri(val);
			return this;
		}
		
		public CoverArt build() {
			return new CoverArt(this);
		}
		
		private Optional<URI> buildUri(String val) {
			try {
				return Optional.of(new URI(val));
			} catch (URISyntaxException e) {
				log.warn("Could not form a valid URI from [{}]", val);
				return Optional.empty();
			}
		}
	}

}
