package bo.roman.radio.player.listener;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bo.roman.radio.utilities.LoggerUtils;

public class Subject<T> {
	private final Logger log = LoggerFactory.getLogger(Subject.class);
	
	private List<Observer<T>> observers = new ArrayList<>();
	
	public void registerObserver(Observer<T> o) {
		LoggerUtils.logDebug(log, () -> "Adding Observer=" + o);
		observers.add(o);
	}
	
	public void notifyObservers(T t) {
		log.info("Notifying observers with Entity[{}]", t);
		observers.forEach(o -> o.update(t));
	}

}
