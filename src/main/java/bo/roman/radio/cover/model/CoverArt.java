package bo.roman.radio.cover.model;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bo.roman.radio.utilities.StringUtils;

public class CoverArt {
	private final static Logger log = LoggerFactory.getLogger(CoverArt.class);

	private int maxWidth;
	private int maxHeight;

	private Optional<URI> largeUri;
	private Optional<URI> mediumUri;
	private Optional<URI> smallUri;

	private CoverArt(Builder b) {
		largeUri = b.largeUri;
		mediumUri = b.mediumUri;
		smallUri = b.smallUri;
		maxWidth = b.maxWidth;
		maxHeight = b.maxHeight;
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

	public int getMaxHeight() {
		return maxHeight;
	}

	public int getMaxWidth() {
		return maxWidth;
	}

	@Override
	public String toString() {
		return "CoverArt [[w=" + maxWidth + " x h=" + maxHeight + "] largeUri=" + largeUri + ", mediumUri=" + mediumUri
				+ ", smallUri=" + smallUri + "]";
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
		private Optional<URI> largeUri = Optional.empty();
		private Optional<URI> mediumUri = Optional.empty();
		private Optional<URI> smallUri = Optional.empty();

		private int maxWidth;
		private int maxHeight;

		public Builder maxWidth(int val) {
			maxWidth = val;
			return this;
		}

		public Builder maxHeight(int val) {
			maxHeight = val;
			return this;
		}

		public Builder largeUri(String val) {
			if (StringUtils.exists(val)) {
				largeUri = buildUri(val);
			}
			return this;
		}

		public Builder mediumUri(String val) {
			if (StringUtils.exists(val)) {
				mediumUri = buildUri(val);
			}
			return this;
		}

		public Builder smallUri(String val) {
			if (StringUtils.exists(val)) {
				smallUri = buildUri(val);
			}
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
