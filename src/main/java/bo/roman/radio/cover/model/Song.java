package bo.roman.radio.cover.model;

import static bo.roman.radio.utilities.StringUtils.nullIsEmpty;

public class Song extends MBEntity {
	private final String name;
	private final String artist;
	
	private Song(Builder builder) {
		super(builder.mbid);
		this.name = builder.name;
		this.artist = builder.artist;
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
		private String mbid;
		
		public Builder name(String val) {
			name = nullIsEmpty(val);
			return this;
		}
		
		public Builder artist(String val) {
			artist = nullIsEmpty(val);
			return this;
		}
		
		public Builder mbid(String val) {
			mbid = nullIsEmpty(val);
			return this;
		}
		
		public Song build() {
			return new Song(this);
		}
		
	} 

}
