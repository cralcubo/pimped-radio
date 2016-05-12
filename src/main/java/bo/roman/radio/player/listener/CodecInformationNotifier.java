package bo.roman.radio.player.listener;

import java.util.ArrayList;
import java.util.List;

import bo.roman.radio.player.model.CodecInformation;

public class CodecInformationNotifier implements CodecInformationSubject {
	
	private List<CodecInformationObserver> observers = new ArrayList<>();

	@Override
	public void registerObservers(CodecInformationObserver observer) {
		observers.add(observer);
	}

	@Override
	public void notifyObservers(CodecInformation codecInformation) {
		observers.forEach(o -> o.update(codecInformation));
	}

}
