package bo.roman.radio.cover.model.mapping.last.fm;

import java.util.ArrayList;
import java.util.List;

public class TrackInfoMapping {
	public Track track;

	public static class Track{
		public String name;
		public Artist artist;
		public Album album;
	}

	public static class Artist {
		public String name;
	}

	public static class Album {
		public String title;
		public List<LastFmImage> image = new ArrayList<>();
	}

}
