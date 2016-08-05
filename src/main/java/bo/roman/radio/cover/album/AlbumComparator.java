package bo.roman.radio.cover.album;

import java.util.Comparator;

import bo.roman.radio.cover.model.Album;
import bo.roman.radio.utilities.PhraseCalculator;

public class AlbumComparator implements Comparator<Album> {
	
	private final String song;
	private final String artist;
	
	public AlbumComparator(String song, String artist) {
		this.song = song;
		this.artist = artist;
	}
	
	/**
	 * Comparator rules: 
	 * First priority: Album Name match Song Name 
	 * Second priority: Song and Artist Match  
	 * Third priority: Song or Artist Match 
	 * 
	 * @return
	 */
	@Override
	public int compare(Album a1, Album a2) {
		/*
		 * First priority: Album Name match Song Name
		 */
		boolean songAlbumMatch1 = PhraseCalculator.phrase(song).isExactTo(a1.getAlbumName()) 
				&& PhraseCalculator.phrase(artist).isExactTo(a1.getArtistName());
		boolean songAlbumMatch2 = PhraseCalculator.phrase(song).isExactTo(a2.getAlbumName()) 
				&& PhraseCalculator.phrase(artist).isExactTo(a2.getArtistName());
		if (songAlbumMatch1 || songAlbumMatch2) {
			if (songAlbumMatch1 && !songAlbumMatch2) {
				return -1;
			} else if(!songAlbumMatch1 && songAlbumMatch2) {
				return 1;
			}
		}
		
		/*
		 * Second priority: Song and Artist Match 
		 */
		boolean songAndArtistMatch1 = PhraseCalculator.phrase(song).isCloseTo(a1.getSongName())
				&& PhraseCalculator.phrase(artist).isCloseTo(a1.getArtistName());
		boolean songAndArtistMatch2 = PhraseCalculator.phrase(song).isCloseTo(a2.getSongName())
				&& PhraseCalculator.phrase(artist).isCloseTo(a2.getArtistName());
		
		if (songAndArtistMatch1 || songAndArtistMatch2) {
			float songVal1 = matchValueCalulator(song, a1.getSongName());
			float artistVal1 = matchValueCalulator(artist, a1.getArtistName());
			float tot1 = (songVal1 + artistVal1) / 2;
			
			float songVal2 = matchValueCalulator(song, a2.getSongName());
			float artistVal2 = matchValueCalulator(artist, a2.getArtistName());
			float tot2 = (songVal2 + artistVal2) / 2;
			if(tot1 > tot2) {
				return -1;
			} else if(tot1 < tot2) {
				return 1;
			}
		}

		/*
		 * Third priority: Song or Artist Match 
		 */

		boolean songOrArtistMatch1 = PhraseCalculator.phrase(song).isCloseTo(a1.getSongName())
				|| PhraseCalculator.phrase(artist).isCloseTo(a1.getArtistName());
		boolean songOrArtistMatch2 = PhraseCalculator.phrase(song).isCloseTo(a2.getSongName())
				|| PhraseCalculator.phrase(artist).isCloseTo(a2.getArtistName());
		
		if (songOrArtistMatch1 || songOrArtistMatch2) {
			float songVal1 = matchValueCalulator(song, a1.getSongName());
			float artistVal1 = matchValueCalulator(artist, a1.getArtistName());
			float tot1 = Math.max(songVal1, artistVal1);
			
			float songVal2 = matchValueCalulator(song, a2.getSongName());
			float artistVal2 = matchValueCalulator(artist, a2.getArtistName());
			float tot2 = Math.max(songVal2, artistVal2);
			if(tot1 > tot2) {
				return -1;
			} else if(tot1 < tot2) {
				return 1;
			}
		}

		return 0;
	}
	
	/**
	 * Return in a value how close both values set in the parameters are.
	 * 
	 * @param val1
	 * @param val2
	 * 
	 * @return Exact: 1.0, Similar: 0.5 - 0.99, Other: 0
	 */
	private float matchValueCalulator(String val1, String val2) {
		int charsDiff = PhraseCalculator.phrase(val1.toLowerCase()).calculateCharsDifference(val2.toLowerCase());
		if(charsDiff > 50) {
			return 0;
		}
		return (100 - charsDiff)/100f;
	}

}
