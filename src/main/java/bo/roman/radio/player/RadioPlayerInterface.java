package bo.roman.radio.player;

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

}
