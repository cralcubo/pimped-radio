package bo.roman.radio.player.listener;

import bo.roman.radio.player.model.CodecInformation;

public interface CodecInformationSubject {
	
	void registerObservers(CodecInformationObserver observer);
	
	void notifyObservers(CodecInformation codecInformation);

}
