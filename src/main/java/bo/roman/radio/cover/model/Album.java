package bo.roman.radio.cover.model;

import static bo.roman.radio.utilities.StringUtils.nullIsEmpty;

import java.util.Optional;

public class Album extends MBEntity{
	private String songName;
	private String artistName;
	private String name;
	private String status;
	private Optional<CoverArt> coverArt;

	private Album(Builder builder) {
		super(builder.mbid);
		this.name = builder.name;
		this.status = builder.status;
		this.coverArt = builder.coverArt;
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
	
	public Optional<CoverArt> getCoverArt() {
		return coverArt;
	}
	
	
	@Override
	public String toString() {
		return "Album [songName=" + songName + ", artistName=" + artistName + ", name=" + name + ", status=" + status
				+ ", coverArt=" + coverArt + "] " + super.toString();
	}


	public static class Builder {
		private String artistName;
		private String songName;
		private String status;
		private String name;
		private String mbid;
		private Optional<CoverArt> coverArt = Optional.empty();

		public Builder name(String val) {
			name = nullIsEmpty(val);
			return this;
		}
		
		public Builder coverArt(Optional<CoverArt> val) {
			coverArt = val;
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
