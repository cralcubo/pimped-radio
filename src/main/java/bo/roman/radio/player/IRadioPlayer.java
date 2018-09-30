package bo.roman.radio.player;

import java.util.Optional;

import bo.roman.radio.player.model.Codec;
import bo.roman.radio.player.model.MediaPlayerInformation;
import io.reactivex.Observable;

public interface IRadioPlayer {
	// The radio player will be a singleton
	static IRadioPlayer getInstance = new RadioPlayer();
	
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
	 * Release the MediaPlayer
	 */
	void close();
	
	/**
	 * Set the audio volume of the 
	 * Media Player.
	 * 
	 * @param vol must be a number between 0 and 100
	 */
	void setVolume(int vol);
	
	/**
	 * Calculate the codec of the media currently played.
	 */
	Optional<Codec> calculateCodec();
	
	/**
	 * Creates an observable that will stream all the mediaMeta change events
	 * 
	 */
	Observable<MediaPlayerInformation> getMediaObservable();

}
