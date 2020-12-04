package dev.alexisok.untitledbot.logging;

import dev.alexisok.untitledbot.Main;
import net.dv8tion.jda.internal.JDAImpl;

import java.io.PrintStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Utility for logging things to the console.
 * 
 * @author AlexIsOK
 * @since 0.0.1
 */
public final class Logger {
	
	/**
	 * Log something to the console.
	 * 
	 * @see Logger#critical(String, int, boolean)
	 * @param message the message to output.
	 */
	public static synchronized void log(String message) {
		if(message == null) return;
		JDAImpl.LOG.info(message);
	}
	
	/**
	 * Debug.  The debug option in the config file must be
	 * enabled for this to activate.
	 * 
	 * @see Logger#log(String)
	 * @param message the message.
	 */
	public static synchronized void debug(String message) {
		if(message == null) return;
		if(Main.DEBUG)
			JDAImpl.LOG.info(message);
	}
	
	/**
	 * Output a track trace followed by the message.
	 * 
	 * @param message the message to send to the console.
	 */
	public static synchronized void critical(String message) {
		JDAImpl.LOG.error(message);
	}
}
