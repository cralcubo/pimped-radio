package bo.roman.radio.cover.model;

public class Album {
	private String title;
	private String credits;
	private String mbid;
	private String status;
	private String coverUrl;

	private Album(Builder builder) {
		this.title = builder.title;
		this.credits = builder.credits;
		this.mbid = builder.mbid;
		this.status = builder.status;
		this.coverUrl = builder.coverUrl;
	}

	public String getTitle() {
		return title;
	}

	public String getCredits() {
		return credits;
	}

	public String getMbid() {
		return mbid;
	}

	public String getStatus() {
		return status;
	}

	public String getCoverUrl() {
		return coverUrl;
	}

	public static class Builder {
		public String status;
		private String title;
		private String credits;
		private String mbid;
		private String coverUrl;

		public Builder title(String val) {
			title = nullIsEmpty(val);
			return this;
		}

		public Builder credits(String val) {
			credits = nullIsEmpty(val);
			return this;
		}

		public Builder mbid(String val) {
			mbid = nullIsEmpty(val);
			return this;
		}

		public Builder coverUrl(String val) {
			coverUrl = nullIsEmpty(val);
			return this;
		}

		public Builder status(String val) {
			status = nullIsEmpty(val);
			return this;
		}

		public Album build() {
			return new Album(this);
		}

		private String nullIsEmpty(String val) {
			if (val == null) val = "";
			return val;
		}
	}
}
