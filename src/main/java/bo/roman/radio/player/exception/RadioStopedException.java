package bo.roman.radio.player.exception;

public class RadioStopedException extends Exception {
	private static final long serialVersionUID = 1L;

	public RadioStopedException(String metaTitle) {
		super("There was an error playing: " + metaTitle);
	}

}
