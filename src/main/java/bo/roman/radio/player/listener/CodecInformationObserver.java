package bo.roman.radio.player.listener;

import bo.roman.radio.player.model.CodecInformation;

public interface CodecInformationObserver {
	
	void update(final CodecInformation codecInformation);

}
