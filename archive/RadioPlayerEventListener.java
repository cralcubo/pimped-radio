package bo.roman.radio.player.listener;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bo.roman.radio.player.IRadioPlayer;
import bo.roman.radio.player.codec.CodecCalculator;
import bo.roman.radio.player.listener.subjects.CodecInformationSubject;
import bo.roman.radio.player.listener.subjects.MediaMetaSubject;
import bo.roman.radio.player.model.CodecInformation;
import bo.roman.radio.utilities.LoggerUtils;
import uk.co.caprica.vlcj.player.MediaMeta;
import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerEventAdapter;

public class RadioPlayerEventListener extends MediaPlayerEventAdapter{
	
	private static final int METATYPE_NOWPLAYING = 12;

	private static final Logger log = LoggerFactory.getLogger(RadioPlayerEventListener.class);
	
	private final IRadioPlayer radioPlayer;
	private final MediaMetaSubject metaNotifier;
	private final CodecInformationSubject codecNotifier;
	
	public RadioPlayerEventListener(IRadioPlayer radioPlayer, MediaMetaSubject metaNotifier, CodecInformationSubject codecNotifier) {
		this.radioPlayer = radioPlayer;
		this.metaNotifier = metaNotifier;
		this.codecNotifier = codecNotifier;
	}
	
	@Override
	public void mediaMetaChanged(MediaPlayer mediaPlayer, int metaType) {
		LoggerUtils.logDebug(log, () -> String.format("Media Meta Changed[metaType=%s]", metaType));
		if(metaType == METATYPE_NOWPLAYING) { // 12 -> Changed NowPlying
			// Update NowPLaying info
			MediaMeta meta = mediaPlayer.getMediaMeta();
			metaNotifier.notifyObservers(meta);
			meta.release();
			
			// Update Codec info
			Optional<CodecInformation> oCodecInfo = CodecCalculator.calculate(mediaPlayer);
			oCodecInfo.ifPresent(ci -> codecNotifier.notifyObservers(ci));
		}
	}

	@Override
	public void error(MediaPlayer mediaPlayer) {
		String streamName = "";
		if(mediaPlayer.getMediaMeta() != null) {
			streamName = mediaPlayer.getMediaMeta().getTitle();
		}
		log.error("There was an error trying to play the stream [{}]", streamName);
		
		radioPlayer.stop();
		System.exit(0);
	}

}
