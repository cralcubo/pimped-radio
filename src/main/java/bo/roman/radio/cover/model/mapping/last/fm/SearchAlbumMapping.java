package bo.roman.radio.cover.model.mapping.last.fm;

import java.util.ArrayList;
import java.util.List;

public class SearchAlbumMapping {
	public Results results;

	public static class Results {
		public AlbumMatches albummatches;
	}

	public static class AlbumMatches {
		public List<LastFmAlbum> album = new ArrayList<>();
	}
}
