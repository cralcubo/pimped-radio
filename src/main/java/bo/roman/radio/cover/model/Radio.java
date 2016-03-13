package bo.roman.radio.cover.model;

public class Radio {
	private final String name;
	private final String coverUrl;

	public Radio(String name, String coverUrl) {
		this.name = name;
		this.coverUrl = coverUrl;
	}

	public String getName() {
		return name;
	}

	public String getCoverUrl() {
		return coverUrl;
	}

	@Override
	public String toString() {
		return "Radio [name=" + name + ", coverUrl=" + coverUrl + "]";
	}

}
