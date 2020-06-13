package dev.alexisok.untitledbot;

import dev.alexisok.untitledbot.logging.Logger;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.jetbrains.annotations.NotNull;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.EnumSet;
import java.util.Properties;

/**
 * 
 * Main class, contains the main method.
 * This class is what sets {@link BotClass} as a bot.
 * 
 * For more information on developing, use the wiki
 * posted on the GitHub page.
 * 
 * @author AlexIsOK
 * @since 0.0.1
 */
public class Main {
	
	public static final String VERSION = "0.0.1";
	public static final String DATA_PATH;
	
	public static boolean checkForUpdates = true;
	
	private static boolean onlyStats = false;
	
	
	static {
		//temp for final string
		String DATA_PATH1;
		
		try {
			
			Properties p = new Properties();
			p.load(new FileInputStream(new File("bot.properties")));
			
			DATA_PATH1 = p.getProperty("DataPath");
			
			if (!DATA_PATH1.endsWith("/")) DATA_PATH1 += "/";
			
			//TODO make this work for NTFS as well.
			//right now, this only matches against *nix filesystem allowed
			//characters, but some users may be running this on Windows 10.
			//This will "work" for NT, but doesn't ensure that the file
			//names are correct, I need a way of determining the host OS.
			if(!DATA_PATH1.matches("(/?[^\0/])+|/"))
				throw new IOException();
			
		} catch (IOException ignored) {
			DATA_PATH1 = "./data/"; //default
		}
		DATA_PATH = DATA_PATH1;
	}
	
	/**
	 * 
	 * Arguments (NOT case sensitive):
	 *      --IKnowWhatImDoingIDontWantToUpgrade - skip upgrade checks.
	 *      --Stats - prints statistics about the bot and then exits.
	 *      --Version
	 * 
	 * @param args command line arguments, first one is for the token, any
	 *             other arguments not listed in this methods JavaDoc will
	 *             be ignored.
	 */
	public static void main(@NotNull String[] args) {
		
		Logger.log("Starting untitled bot " + VERSION + ".");
		
		if (args.length > 0) Logger.log("Checking arguments...");
		else                 Logger.critical("No arguments, exiting...", 5);
		
		checkArgs(args);
		
		try {
			JDA a = new JDABuilder(args[0]).setDisabledCacheFlags(
					EnumSet.of(CacheFlag.EMOTE)
			).build();
		} catch (LoginException e) {
			e.printStackTrace();
			Logger.critical("Could not login to Discord!", 1);
		}
	}
	
	/**
	 * Check the arguments.  The arguments have their own method because
	 * I feel as if it is cleaner to do it this way than to have
	 * everything be done in the main method.
	 * 
	 * @param args arguments
	 */
	private static void checkArgs(@NotNull String[] args) {
		
		for(int i = 0; i < args.length; i++) {
			args[i] = args[i].toLowerCase();
		}
		
		for(String s : args) {
			switch(s) {
				case "--iknowwhatimdoingidontwanttoupgrade": //don't check for upgrades
					checkForUpdates = false; break;
				case "--stats": //only print stats
					onlyStats = true; break;
				case "--instantbreak":
					Logger.critical("Instant break: activated!", 2);
			}
		}
	}
	
}
