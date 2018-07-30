package bo.roman.radio.player.model;

import java.util.Optional;

import bo.roman.radio.cover.model.Codec;
import bo.roman.radio.cover.model.Song;

public class MediaPlayerInformation {
	private final Optional<Codec> oCodec;
	private final Optional<Song> oSong;
	private final Optional<String> oRadioName;
	
	public MediaPlayerInformation(Optional<Codec> oCodec, Optional<Song> oSong, Optional<String> oRadioName) {
		this.oCodec = oCodec;
		this.oSong = oSong;
		this.oRadioName = oRadioName;
	}
	
	public Optional<Codec> getoCodec() {
		return oCodec;
	}
	
	public Optional<Song> getoSong() {
		return oSong;
	}
	
	public Optional<String> getoRadioName() {
		return oRadioName;
	}

	@Override
	public String toString() {
		return "MediaPlayerInformation [oCodec=" + oCodec + ", oSong=" + oSong + ", oRadioName=" + oRadioName + "]";
	}
}
