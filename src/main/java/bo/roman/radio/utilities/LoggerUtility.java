package bo.roman.radio.utilities;

import java.util.function.Supplier;

import org.slf4j.Logger;

public class LoggerUtility {
	
	public static void logDebug(Logger logger, Supplier<String> msgSupplier){
		if(logger.isDebugEnabled()){
			logger.debug(msgSupplier.get());
		}
	}

}
