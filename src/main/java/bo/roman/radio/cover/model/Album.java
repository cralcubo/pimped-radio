package bo.roman.radio.cover.model;

import static bo.roman.radio.utilities.StringUtils.nullIsEmpty;

public class Album extends MBEntity{
	private String songName;
	private String artistName;
	private String name;
	private String status;
	private String coverUrl;

	private Album(Builder builder) {
		super(builder.mbid);
		this.name = builder.name;
		this.status = builder.status;
		this.coverUrl = builder.coverUrl;
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
	
	public String getCoverUrl() {
		return coverUrl;
	}

	@Override
	public String toString() {
		return "Album [songName=" + songName + ", artistName=" + artistName + ", name=" + name + ", coverUrl="
				+ coverUrl + "]";
	}
	
	public static class Builder {
		private String artistName;
		private String songName;
		private String coverUrl;
		private String status;
		private String name;
		private String mbid;

		public Builder name(String val) {
			name = nullIsEmpty(val);
			return this;
		}
		
		public Builder coverUrl(String val) {
			coverUrl = nullIsEmpty(val);
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
