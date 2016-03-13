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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		Album other = (Album) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
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
