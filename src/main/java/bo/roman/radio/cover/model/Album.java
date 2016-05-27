package bo.roman.radio.cover.model;

import static bo.roman.radio.utilities.StringUtils.nullIsEmpty;

import java.util.Optional;

public class Album {
	private String songName;
	private String artistName;
	private String name;
	private Optional<CoverArt> coverArt;

	private Album(Builder builder) {
		this.name = builder.name;
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
	
	public String getAlbumName() {
		return name;
	}
	
	public Optional<CoverArt> getCoverArt() {
		return coverArt;
	}
	
	@Override
	public String toString() {
		return "Album [songName=" + songName + ", artistName=" + artistName + ", albumName=" + name
				+ ", coverArt=" + coverArt + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((artistName == null) ? 0 : artistName.hashCode());
		result = prime * result + ((coverArt == null) ? 0 : coverArt.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((songName == null) ? 0 : songName.hashCode());
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
		Album other = (Album) obj;
		if (artistName == null) {
			if (other.artistName != null)
				return false;
		} else if (!artistName.equals(other.artistName))
			return false;
		if (coverArt == null) {
			if (other.coverArt != null)
				return false;
		} else if (!coverArt.equals(other.coverArt))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (songName == null) {
			if (other.songName != null)
				return false;
		} else if (!songName.equals(other.songName))
			return false;
		return true;
	}

	public static class Builder {
		private String artistName;
		private String songName;
		private String name;
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

		public Album build() {
			return new Album(this);
		}
	}
}
