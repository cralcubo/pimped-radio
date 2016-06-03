package bo.roman.radio.player.listener.subjects;

import bo.roman.radio.player.listener.observers.CodecInformationObserver;
import bo.roman.radio.player.model.CodecInformation;

public interface CodecInformationSubject {
	
	void registerObserver(CodecInformationObserver observer);
	
	void notifyObservers(CodecInformation codecInformation);

}
