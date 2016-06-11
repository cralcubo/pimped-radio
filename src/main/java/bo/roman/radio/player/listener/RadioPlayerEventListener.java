package bo.roman.radio.player.listener;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bo.roman.radio.cover.model.Song;
import bo.roman.radio.player.codec.CodecCalculator;
import bo.roman.radio.player.model.CodecInformation;
import bo.roman.radio.player.model.ErrorInformation;
import bo.roman.radio.player.model.RadioPlayerEntity;
import bo.roman.radio.utilities.LoggerUtils;
import bo.roman.radio.utilities.MediaMetaUtils;
import uk.co.caprica.vlcj.player.MediaMeta;
import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerEventAdapter;

public class RadioPlayerEventListener extends MediaPlayerEventAdapter {
	
	private static final Logger log = LoggerFactory.getLogger(RadioPlayerEventListener.class);
	
	private static final int METATYPE_NOWPLAYING = 12;
	
	private final Subject<RadioPlayerEntity> radioEntitySubject;
	private final Subject<CodecInformation> codecSubject;
	private final Subject<ErrorInformation> errorSubject;
	
	private final RadioInformationFinder radioInfoFinder;
	
	public RadioPlayerEventListener(List<Observer<RadioPlayerEntity>> radioEntityObservers, List<Observer<CodecInformation>> codecObservers, List<Observer<ErrorInformation>> errorObservers) {
		radioEntitySubject = new Subject<>();
		codecSubject = new Subject<>();
		errorSubject = new Subject<>();
		radioEntityObservers.forEach(radioEntitySubject::registerObserver);
		codecObservers.forEach(codecSubject::registerObserver);
		errorObservers.forEach(errorSubject::registerObserver);
		
		radioInfoFinder = new RadioInformationFinder();
	}
	
	@Override
	public void mediaMetaChanged(MediaPlayer mediaPlayer, int metaType) {
		LoggerUtils.logDebug(log, () -> String.format("Media Meta Changed[metaType=%s]", metaType));
		if(metaType == METATYPE_NOWPLAYING) { // 12 -> Changed NowPlying
			
			MediaMeta meta = mediaPlayer.getMediaMeta();
			LoggerUtils.logDebug(log, () -> "MediaMeta changed=" + meta);
			final Optional<String> oRadioName = MediaMetaUtils.findRadioName(meta);
			final Optional<Song> oSong = MediaMetaUtils.buildSong(meta);
			log.info("Changed MediaMeta. Radio[{}] and Song[{}]", oRadioName, oSong);
			meta.release();
			
			// Find the new RadioPlayer Info and update with
			// a RadioPlayerEntity
			RadioPlayerEntity rpe = radioInfoFinder.find(oRadioName, oSong);
			radioEntitySubject.notifyObservers(rpe);
			
			// Update Codec info
			Optional<CodecInformation> oCodecInfo = CodecCalculator.calculate(mediaPlayer);
			oCodecInfo.ifPresent(ci -> codecSubject.notifyObservers(ci));
		}
	}
	
	@Override
	public void error(MediaPlayer mediaPlayer) {
		String streamName = "";
		if(mediaPlayer.getMediaMeta() != null) {
			streamName = mediaPlayer.getMediaMeta().getTitle();
		}
		
		log.error("There was an error trying to play the stream [{}]", streamName);
		errorSubject.notifyObservers(new ErrorInformation("Error playing stream.", streamName));
	}

}
