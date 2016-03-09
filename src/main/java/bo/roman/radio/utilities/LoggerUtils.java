package bo.roman.radio.utilities;

import java.util.function.Supplier;

import org.slf4j.Logger;

public class LoggerUtils {

	public static void logDebug(Logger logger, Supplier<String> msgSupplier) {
		logDebug(logger, msgSupplier, null);
	}

	public static void logDebug(Logger logger, Supplier<String> msgSupplier, Throwable exception) {
		if (logger.isDebugEnabled()) {
			logger.debug(msgSupplier.get(), exception);
		}
	}
}
