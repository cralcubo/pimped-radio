package bo.roman.radio.player;

import uk.co.caprica.vlcj.player.MediaPlayerEventAdapter;

public interface RadioPlayerInterface {
	
	/**
	 * Start playing a stream.
	 * 
	 * @param radioStationUrl the URL of the stream to play.
	 */
	void play(String radioStationUrl);
	
	/**
	 * Stop playing a stream.
	 */
	void stop();
	
	/**
	 * Add events listener to 
	 * decide what to do when the RadioPlayer is playing
	 * is stopped, MediaMeta changed, errors, etc.
	 * 
	 * @param eventsAdapter
	 */
	void addEventsListener(MediaPlayerEventAdapter eventsAdapter);

}
