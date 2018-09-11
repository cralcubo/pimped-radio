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

	}

}
