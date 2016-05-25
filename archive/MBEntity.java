package bo.roman.radio.cover.model;

import java.util.Optional;

import bo.roman.radio.utilities.StringUtils;

public abstract class MBEntity {
	
	private Optional<String> mbid = Optional.empty();
	
	protected MBEntity(String mbid) {
		this.mbid = Optional.of(StringUtils.nullIsEmpty(mbid));
	}
	
	public Optional<String> getMbid() {
		return mbid;
	}
	
	public void setMbid(Optional<String> mbid) {
		this.mbid = mbid;
	}
	
	@Override
	public String toString() {
		return "MBEntity [mbid=" + mbid + "]";
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
		MBEntity other = (MBEntity) obj;
		if (mbid == null) {
			if (other.mbid != null)
				return false;
		} else if (!mbid.equals(other.mbid))
			return false;
		return true;
	}
}
