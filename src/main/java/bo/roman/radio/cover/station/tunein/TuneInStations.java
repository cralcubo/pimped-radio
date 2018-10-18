package bo.roman.radio.cover.station.tunein;

import java.util.List;

public class TuneInStations {
	SearchResults searchResults;

	static class SearchResults {
		List<ContainerItems> containerItems;

		static class ContainerItems {
			String containerType;
			List<Children> children;

			static class Children {
				String image;
				String title;
			}
		}

	}

}
