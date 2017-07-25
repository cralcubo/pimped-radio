package bo.roman.radio.cover.model.mapping.last.fm;

import java.util.ArrayList;
import java.util.List;

public class AlbumInfoMapping {
	public Album album;
	
	public static class Album {
		public String name;
		public String artist;
		public List<LastFmImage> image = new ArrayList<>();
	}

}
