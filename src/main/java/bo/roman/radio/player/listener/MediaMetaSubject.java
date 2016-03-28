package bo.roman.radio.player.listener;

import uk.co.caprica.vlcj.player.MediaMeta;

public interface MediaMetaSubject {
	
	void registerObserver(MediaMetaObserver o);
	
    void notifyObservers(final MediaMeta meta);

}
