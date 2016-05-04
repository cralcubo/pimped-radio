package bo.roman.radio.player.model;

public class CodecInformation {
	private final String codec;
	private final int channels;
	private final int sampleRate;
	private final int bitRate;

	private CodecInformation(Builder b) {
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

	public int getSampleRate() {
		return sampleRate;
	}

	public int getBitRate() {
		return bitRate;
	}

	public class Builder {
		private String codec;
		private int channels;
		private int sampleRate;
		private int bitRate;

		public Builder codec(String val) {
			codec = val;
			return this;
		}

		public Builder channels(int val) {
			channels = val;
			return this;
		}

		public Builder sampleRate(int val) {
			sampleRate = val;
			return this;
		}

		public Builder bitRate(int val) {
			bitRate = val;
			return this;
		}

		public CodecInformation build() {
			return new CodecInformation(this);
		}

	}

}
