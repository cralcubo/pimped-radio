package bo.roman.radio.cover.model;

import java.util.Optional;

public class Album {
	private String title;
	private String credits;
	private String status;
	private Optional<String> mbid;

	private Album(Builder builder) {
		this.title = builder.title;
		this.credits = builder.credits;
		this.mbid = builder.mbid;
		this.status = builder.status;
	}

	public String getTitle() {
		return title;
	}

	public String getCredits() {
		return credits;
	}

	public Optional<String> getMbid() {
		return mbid;
	}

	public String getStatus() {
		return status;
	}
	
	
	@Override
	public String toString() {
		return "Album [title=" + title + ", credits=" + credits + ", status=" + status + ", mbid=" + mbid + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((mbid == null) ? 0 : mbid.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Album other = (Album) obj;
		if (mbid == null) {
			if (other.mbid != null)
				return false;
		} else if (!mbid.equals(other.mbid))
			return false;
		return true;
	}

	public static class Builder {
		private String status;
		private String title;
		private String credits;
		private Optional<String> mbid;

		public Builder title(String val) {
			title = nullIsEmpty(val);
			return this;
		}

		public Builder credits(String val) {
			credits = nullIsEmpty(val);
			return this;
		}

		public Builder mbid(String val) {
			mbid = Optional.of(nullIsEmpty(val));
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
