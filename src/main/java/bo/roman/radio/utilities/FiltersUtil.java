package bo.roman.radio.utilities;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;

public class FiltersUtil {
	
	/**
	 * Returns a predicate to check 
	 * if the Function key passed to the method
	 * is unique.
	 */
	public static <T> Predicate<T> distinctByKey(Function<? super T,Object> keyExtractor) {
	    Map<Object,Boolean> seen = new ConcurrentHashMap<>();
	    return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
	}

}
