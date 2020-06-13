package dev.alexisok.untitledbot.logging;

import java.io.PrintStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author AlexIsOK
 * @since 0.0.1
 */
public class Logger {
	
	//example: 2020-06-12@15:41:21
	private static final DateFormat df = new SimpleDateFormat("yyyy-MM-dd'@'HH:mm:ss");
	
	public static PrintStream output = System.out;
	
	/**
	 * Log something to the console.
	 * 
	 * For error outputting, see {@link Logger#critical(String, int, boolean)}
	 * 
	 * Output: [ISO8601 TIME] - MESSAGE
	 * 
	 * @see Logger#critical(String, int, boolean)
	 * @param message the message to output.
	 */
	public static void log(String message) {
		output.println("[" + df.format(new Date()) + "] - " + message);
	}
	
	/**
	 * 
	 * Critical error with an exit code of 4.
	 * 
	 * @see Logger#critical(String, int, boolean) for more.
	 * @param message the message to send to the console.
	 */
	public static void critical(String message) {
		
	}
	
	/**
	 * 
	 * Critical error with a custom exit code.
	 * Please use one that is not already in use, or use 4 for a generic code.
	 * 
	 * @see Logger#critical(String, int, boolean) for more documentation.
	 * 
	 * @param message the message to send to the console.
	 * @param status the status code to exit with.
	 *               Set to 4 for a generic exit code, otherwise
	 *               use one that is not in use already.  See
	 *               exitCodes.txt at the root of this repository.
	 */
	public static void critical(String message, int status) {
		critical(message, status, true);
	}
	
	/**
	 * 
	 * Output a track trace followed by the message.
	 * 
	 * 
	 * @param message the message to send to the console.
	 * @param status the status code to exit with (ignored if shutdown is false)
	 *               Set to 4 for a generic exit code, otherwise
	 *               use one that is not in use already.  See
	 *               exitCodes.txt at the root of this repository.   
	 * @param shutdown TRUE to exit the program, FALSE otherwise.
	 *                    
	 */
	public static void critical(String message, int status, boolean shutdown) {
		Thread.dumpStack();
		System.err.println("[" + df.format(new Date()) + "] - " + message);
		if(shutdown) Runtime.getRuntime().exit(status);
	}
}
