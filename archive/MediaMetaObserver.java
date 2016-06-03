package bo.roman.radio.player.listener.observers;

import bo.roman.radio.player.listener.Observer;
import bo.roman.radio.player.model.RadioPlayerEntity;

public interface MediaMetaObserver extends Observer<RadioPlayerEntity> {
	
	void update(RadioPlayerEntity rpe);

}
