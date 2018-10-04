package bo.roman.radio.player.model;

import static bo.roman.radio.utilities.StringUtils.nullIsEmpty;

public class MediaPlayerInformation {
	private final String song;
	private final String artist;
	private final String radioName;

	private MediaPlayerInformation(String song, String artist, String radioName) {
		this.song = song;
		this.artist = artist;
		this.radioName = radioName;
	}

	public String getSong() {
		return song;
	}

	public String getArtist() {
		return artist;
	}

	public String getRadio() {
		return radioName;
	}

	public static class Builder {
		private String song;
		private String artist;
		private String radioName;

		public Builder song(String val) {
			song = nullIsEmpty(val);
			return this;
		}

		public Builder artist(String val) {
			artist = nullIsEmpty(val);
			return this;
		}

		public Builder radioName(String val) {
			radioName = nullIsEmpty(val);
			return this;
		}

		public MediaPlayerInformation build() {
			return new MediaPlayerInformation(song, artist, radioName);
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((artist == null) ? 0 : artist.hashCode());
			result = prime * result + ((radioName == null) ? 0 : radioName.hashCode());
			result = prime * result + ((song == null) ? 0 : song.hashCode());
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
			Builder other = (Builder) obj;
			if (artist == null) {
				if (other.artist != null)
					return false;
			} else if (!artist.equals(other.artist))
				return false;
			if (radioName == null) {
				if (other.radioName != null)
					return false;
			} else if (!radioName.equals(other.radioName))
				return false;
			if (song == null) {
				if (other.song != null)
					return false;
			} else if (!song.equals(other.song))
				return false;
			return true;
		}

	}

}
