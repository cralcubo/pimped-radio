package bo.roman.radio.player.listener;

import bo.roman.radio.player.model.RadioPlayerEntity;

public class PrintRadioPlayerObserver implements MediaMetaObserver {

	@Override
	public void update(RadioPlayerEntity rpe) {
		System.out.println("****************************");
		System.out.println("****************************");
		rpe.getRadio().ifPresent(r -> System.out.println("Radio: " + r));
		rpe.getSong().ifPresent(s -> System.out.println("Song: " + s));
		rpe.getAlbum().ifPresent(a -> System.out.println("Album: " + a));
		System.out.println("****************************");
		System.out.println("****************************");
	}
	

}
