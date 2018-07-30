package bo.roman.radio.player.exception;

import java.util.Optional;

public class RadioStopException extends Exception {
	private static final long serialVersionUID = 1L;

	public RadioStopException(Optional<String> streamUrl, Optional<String> radio) {
		super(String.format("Stream: %s of the radio: %s unexpectedly stopped.", streamUrl.orElse("Unknown"), radio.orElse("Unknown")));
	}

}
