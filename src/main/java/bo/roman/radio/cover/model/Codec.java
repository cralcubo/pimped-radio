package bo.roman.radio.cover.model;

public class Codec {
	private final String codec;
	private final int channels;
	private final float sampleRate;
	private final float bitRate;

	private Codec(Builder b) {
		codec = b.codec;
		channels = b.channels;
		sampleRate = b.sampleRate;
		bitRate = b.bitRate;
	}

	public String getCodec() {
		return codec;
	}

	public int getChannels() {
		return channels;
	}

	public float getSampleRate() {
		return sampleRate;
	}

	public float getBitRate() {
		return bitRate;
	}

	@Override
	public String toString() {
		return "CodecInformation [codec=" + codec + ", channels=" + channels + ", sampleRate=" + sampleRate
				+ ", bitRate=" + bitRate + "]";
	}

	public static class Builder {
		private String codec;
		private int channels;
		private float sampleRate;
		private float bitRate;

		public Builder codec(String val) {
			codec = val;
			return this;
		}

		public Builder channels(int val) {
			channels = val;
			return this;
		}

		public Builder sampleRate(float val) {
			sampleRate = val;
			return this;
		}

		public Builder bitRate(float val) {
			bitRate = val;
			return this;
		}

		public Codec build() {
			return new Codec(this);
		}

	}

}
