package bo.roman.radio.utilities;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExecutorUtils {
	private static final Logger log = LoggerFactory.getLogger(ExecutorUtils.class);
	
	/**
	 * Get an executor that is optimized
	 * for the number of threads that are needed to 
	 * run.
	 * <p>
	 * The threads created by the factory are set as demons
	 * because they don't prevent the termination of the program.
	 * A Java program can't terminate or exit when a normal Thread 
	 * is running.
	 * </p>
	 *  
	 * @param threadsNeeded
	 * @return the Executor with a fixed Thread pool.
	 */
	public static Executor fixedThreadPoolFactory(int threadsNeeded) {
		if(threadsNeeded <= 0) {
			throw new IllegalArgumentException(String.format("This method creates a ThreadPool of at least 1 Thread. [Arg: threadsNeeded=%d]", threadsNeeded));
		}
		int numThreads = Math.min(threadsNeeded, getMaxThreadsAllowed());
		
		LoggerUtils.logDebug(log, () -> String.format("Generating [%s] threads.", numThreads));
		
		return Executors.newFixedThreadPool(numThreads, 
				new ThreadFactory() {
					@Override
					public Thread newThread(Runnable r) {
						Thread t = new Thread(r);
						t.setDaemon(true);
						return t;
					}
				});
	}
	
	/**
	 * Formula that will calculate the 
	 * max number of threads allowed.
	 * 
	 * <p>Nt = Ncpu * Ucpu (1 + W/C)</p>
	 * 
	 * Where:
	 * <ul>
	 * <li>Ncpu is the number of cores.</li>
	 * <li>Ucpu is the CPU utilization (between 0 and 1)</li>
	 * <li>W/C is the radio between wait time to compute time</li>
	 * </ul>
	 * 
	 * @return the max threads allowed (Nt)
	 */
	static int getMaxThreadsAllowed() {
		int Ncpu = Runtime.getRuntime().availableProcessors();
		float Ucpu = 0.25f;
		int wc = 99;
		
		return (int) (Ncpu * Ucpu * (1 + wc));
	}

}
