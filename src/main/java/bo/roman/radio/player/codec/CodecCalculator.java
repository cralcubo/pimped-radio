package bo.roman.radio.player.codec;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bo.roman.radio.player.model.CodecInformation;
import uk.co.caprica.vlcj.binding.internal.libvlc_media_stats_t;
import uk.co.caprica.vlcj.player.AudioTrackInfo;
import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.TrackInfo;
import uk.co.caprica.vlcj.player.TrackType;

public class CodecCalculator {
	private static final Logger log = LoggerFactory.getLogger(CodecCalculator.class);
	
	private static final int ONE_KB = 8000;
	
	public static Optional<CodecInformation> calculate(MediaPlayer mediaPlayer) {
		if(mediaPlayer == null) {
			log.error("There is no mediaPlayer to retrieve codec information.");
			return Optional.empty();
		}
		
		List<TrackInfo> trackInfos = mediaPlayer.getTrackInfo(TrackType.AUDIO);
		if(trackInfos.isEmpty() || !(trackInfos.get(0) instanceof AudioTrackInfo)) {
			log.info("There is no AudioTrackInfo to retrieve codec information.");
			return Optional.empty();
		}
		
		AudioTrackInfo audioInfo = (AudioTrackInfo) trackInfos.get(0);
		String codecName = audioInfo.codecName();
		int bitRate = audioInfo.bitRate();
		int channels = audioInfo.channels();
		int sampleRate = audioInfo.rate();
		
		
		if(bitRate <= 0) {
			bitRate = calculateStableBitRate(mediaPlayer.getMediaStatistics());
		}
		
		return null;
	}

	private static int calculateStableBitRate(libvlc_media_stats_t mediaStatistics) {
		// Loop max 7 times till finding a stable bit rate
		int times = 7;
		int bitRate = 0;
		int delta = 10;
		
		for(int i = 0; i < 7; i++) {
			bitRate = (int) (mediaStatistics.f_demux_bitrate * ONE_KB);
		}
		

		return 0;
	}

}
