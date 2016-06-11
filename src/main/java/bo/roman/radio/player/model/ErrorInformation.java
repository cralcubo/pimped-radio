package bo.roman.radio.player.model;

public class ErrorInformation {
	
	private final String message;
	private final String source;
	
	public ErrorInformation(String message, String source) {
		super();
		this.message = message;
		this.source = source;
	}
	
	public String getMessage() {
		return message;
	}
	
	public String getSource() {
		return source;
	}

}
