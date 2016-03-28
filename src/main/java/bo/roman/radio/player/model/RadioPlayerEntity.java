package bo.roman.radio.player.model;

import java.util.Optional;

import bo.roman.radio.cover.model.Album;
import bo.roman.radio.cover.model.Radio;
import bo.roman.radio.cover.model.Song;

public class RadioPlayerEntity {
	
	private Optional<Radio> radio;
	private Optional<Song> song;
	private Optional<Album> album;
	
	public RadioPlayerEntity(Optional<Radio> radio, Optional<Song> song, Optional<Album> album) {
		this.radio = radio;
		this.song = song;
		this.album = album;
	}
	
	public Optional<Radio> getRadio() {
		return radio;
	}
	
	public Optional<Song> getSong() {
		return song;
	}
	
	public Optional<Album> getAlbum() {
		return album;
	}

	@Override
	public String toString() {
		return "RadioPlayerEntity [radio=" + radio + ", song=" + song + ", album=" + album + "]";
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((album == null) ? 0 : album.hashCode());
		result = prime * result + ((radio == null) ? 0 : radio.hashCode());
		result = prime * result + ((song == null) ? 0 : song.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RadioPlayerEntity other = (RadioPlayerEntity) obj;
		if (album == null) {
			if (other.album != null)
				return false;
		} else if (!album.equals(other.album))
			return false;
		if (radio == null) {
			if (other.radio != null)
				return false;
		} else if (!radio.equals(other.radio))
			return false;
		if (song == null) {
			if (other.song != null)
				return false;
		} else if (!song.equals(other.song))
			return false;
		return true;
	}

}
