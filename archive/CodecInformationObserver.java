package bo.roman.radio.player.listener.observers;

import bo.roman.radio.player.listener.Observer;
import bo.roman.radio.player.model.CodecInformation;

public interface CodecInformationObserver extends Observer<CodecInformation> {
	
	void update(final CodecInformation codecInformation);

}
