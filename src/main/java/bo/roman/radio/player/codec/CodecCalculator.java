package bo.roman.radio.player.codec;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bo.roman.radio.player.model.Codec;
import bo.roman.radio.utilities.LoggerUtils;
import uk.co.caprica.vlcj.binding.internal.libvlc_media_stats_t;
import uk.co.caprica.vlcj.player.AudioTrackInfo;
import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.TrackInfo;
import uk.co.caprica.vlcj.player.TrackType;

public class CodecCalculator {
	private static final Logger log = LoggerFactory.getLogger(CodecCalculator.class);

	private static final int ONE_KB = 8000;

	private static final float ERROR_MARGIN = 0.05f;
	
	public static Optional<Codec> calculate(MediaPlayer mediaPlayer) {
		return retryMechanism(Optional.empty(), 0, mediaPlayer);
	}
	
	private static Optional<Codec> retryMechanism(Optional<Codec> codec, int counter, MediaPlayer mediaPlayer) {
		LoggerUtils.logDebug(log, () -> "["+counter+"] Calculating codec...");
		if(counter >= 5) {
			log.error("It was not possible to calculate the codec information");
			return Optional.empty();
		}
		
		Optional<Codec> c = calculateInternal(mediaPlayer);
		if(c.isPresent()) {
			return c;
		}
		// Wait a bit
		long sleepTime = (long) (Math.pow(2, counter) * 1000);
		sleep(sleepTime);
		
		return retryMechanism(c, counter + 1, mediaPlayer);
 	}

	private static Optional<Codec> calculateInternal(MediaPlayer mediaPlayer) {
		if (mediaPlayer == null) {
			log.error("There is no mediaPlayer to retrieve codec information.");
			return Optional.empty();
		}

		log.info("Retrieving CodecInformation.");
		List<TrackInfo> trackInfos = mediaPlayer.getTrackInfo(TrackType.AUDIO);
		if (trackInfos.isEmpty() || !(trackInfos.get(0) instanceof AudioTrackInfo)) {
			log.info("There is no AudioTrackInfo to retrieve codec information.");
			return Optional.empty();
		}
		AudioTrackInfo audioInfo = (AudioTrackInfo) trackInfos.get(0);
		LoggerUtils.logDebug(log, () -> audioInfo.toString());

		String codecName = audioInfo.codecName();
		float bitRate = audioInfo.bitRate() * 1.0f / 1000;
		int channels = audioInfo.channels();
		float sampleRate = audioInfo.rate() * 1.0f / 1000;

		// If there is no bitrate we proceed to calculate it
		// using the MediaStatistics provided by the MediaPlayer
		if (bitRate <= 0) {
			bitRate = calculateAverageBitRate(mediaPlayer);
		}

		Codec codecInfo = new Codec.Builder()//
				.bitRate(bitRate)//
				.channels(channels)//
				.codec(codecName)//
				.sampleRate(sampleRate)//
				.build();

		log.info("Returning {}", codecInfo);
		return Optional.of(codecInfo);
	}

	/**
	 * Loop max n times till finding a stable bit rate. Every loop will wait 1 sec
	 * to check the bit rate at that time.
	 * 
	 * The right bitRate will result from the comparison of the current bitRate and
	 * the previous bitRate calculated.
	 * 
	 * The formula for the current bitRate is: bRc = f_demux_bitrate * 8000
	 * 
	 * To return a bitRate, it will be compared bRc0 and bRc1 if the difference
	 * between them is +/- 10% the value will be acceptable and an average of both
	 * values will be returned.
	 * 
	 */
	private static float calculateAverageBitRate(MediaPlayer mediaPlayer) {
		log.info("Calculating average bitRate.");

		int times = 7;
		float previousBitRate = 0;
		for (int i = 0; i < times; i++) {
			sleep(1000);
			libvlc_media_stats_t ms = mediaPlayer.getMediaStatistics();
			LoggerUtils.logDebug(log, () -> ms.toString());

			float demuxBitRate = ms.f_demux_bitrate * ONE_KB;
			if (demuxBitRate <= 0) {
				continue;
			}

			float diff = Math.abs(previousBitRate / demuxBitRate - 1.0f);

			LoggerUtils.logDebug(log, () -> String.format("DemuxBitRate[%.2f], diff=%.2f", demuxBitRate, diff));
			if (diff <= ERROR_MARGIN) {
				float bitRate = (demuxBitRate + previousBitRate) / 2;
				log.info("BitRate calculated = {}", bitRate);
				return bitRate;
			}

			previousBitRate = demuxBitRate;
		}

		return 0;
	}

	private static void sleep(long time) {
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			log.error("Sleep was interrupted.", e);
		}
	}

}
