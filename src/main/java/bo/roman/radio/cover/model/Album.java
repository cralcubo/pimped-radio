package bo.roman.radio.cover.model;

import static bo.roman.radio.utilities.StringUtils.nullIsEmpty;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

public class Album extends MBEntity{
	private String songName;
	private String artistName;
	private String name;
	private String status;
	private Optional<URI> coverUri;

	private Album(Builder builder) {
		super(builder.mbid);
		this.name = builder.name;
		this.status = builder.status;
		this.coverUri = builder.coverUri;
		this.songName = builder.songName;
		this.artistName = builder.artistName;
	}
	
	public String getSongName() {
		return songName;
	}
	
	public String getArtistName() {
		return artistName;
	}
	
	public String getName() {
		return name;
	}
	
	public String getStatus() {
		return status;
	}
	
	public Optional<URI> getCoverUri() {
		return coverUri;
	}
	
	
	@Override
	public String toString() {
		return "Album [songName=" + songName + ", artistName=" + artistName + ", name=" + name + ", status=" + status
				+ ", coverUri=" + coverUri + "] " + super.toString();
	}


	public static class Builder {
		private String artistName;
		private String songName;
		private Optional<URI> coverUri;
		private String status;
		private String name;
		private String mbid;

		public Builder name(String val) {
			name = nullIsEmpty(val);
			return this;
		}
		
		public Builder coverUri(String val) {
			try {
				coverUri = Optional.of(new URI(val));
			} catch (URISyntaxException e) {
				coverUri = Optional.empty();
			}
			return this;
		}
		
		public Builder songName(String val) {
			songName = nullIsEmpty(val);
			return this;
		}
		
		public Builder artistName(String val) {
			artistName = nullIsEmpty(val);
			return this;
		}

		public Builder mbid(String val) {
			mbid = nullIsEmpty(val);
			return this;
		}

		public Builder status(String val) {
			status = nullIsEmpty(val);
			return this;
		}

		public Album build() {
			return new Album(this);
		}
	}
}
