package bo.roman.radio.cover.model;

import static bo.roman.radio.utilities.StringUtils.nullIsEmpty;

public class Song {
	private final String name;
	private final String artist;
	
	private Song(Builder builder) {
		this.name = builder.name;
		this.artist = builder.artist;
	}
	
	public Song(String name, String artist) {
		this.name = name;
		this.artist = artist;
	}
	
	public String getArtist() {
		return artist;
	}
	
	public String getName() {
		return name;
	}
	
	@Override
	public String toString() {
		return "Song [name=" + name + ", artist=" + artist + "]";
	}

	public static class Builder {
		private String name;
		private String artist;
		
		public Builder name(String val) {
			name = nullIsEmpty(val);
			return this;
		}
		
		public Builder artist(String val) {
			artist = nullIsEmpty(val);
			return this;
		}
		
		public Song build() {
			return new Song(this);
		}
		
	} 

}
