package bo.roman.radio.cover.model.mapping.last.fm;

import java.util.ArrayList;
import java.util.List;

public class SearchTrackMapping {
	public Results results;

	public static class Results {
		public TrackMatches trackmatches;
	}

	public static class TrackMatches {
		public List<Track> track = new ArrayList<>();
	}

	public static class Track {
		public String name;
		public List<LastFmImage> image = new ArrayList<>();
		public String artist;
	}

}
