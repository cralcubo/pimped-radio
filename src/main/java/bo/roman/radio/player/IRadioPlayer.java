package bo.roman.radio.player;

import uk.co.caprica.vlcj.player.MediaPlayerEventAdapter;

public interface IRadioPlayer {
	
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
	
	/**
	 * Set the audio volume of the 
	 * Media Player.
	 * 
	 * @param vol must be a number between 0 and 100
	 */
	void setVolume(int vol);

}
