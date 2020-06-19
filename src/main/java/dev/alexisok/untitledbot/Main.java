package dev.alexisok.untitledbot;

import dev.alexisok.untitledbot.command.CoreCommands;
import dev.alexisok.untitledbot.logging.Logger;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.jetbrains.annotations.NotNull;

import javax.security.auth.login.LoginException;
import java.io.*;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Properties;
import java.util.Scanner;

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
	public static final String CONFIG_PATH = Paths.get("").toAbsolutePath().toString();
	public static final String DATA_PATH;
	public static final String DEFAULT_PREFIX;
	
	public static boolean checkForUpdates = true;
	
	private static boolean onlyStats = false;
	private static boolean noCoreCommands = false;
	
	public static JDA jda;
	
	static {
		String DEFAULT_PREFIX1;
		//temp for final string
		String DATA_PATH1;
		
		try {
			
			Properties p = new Properties();
			try {
				p.load(new FileInputStream(new File(CONFIG_PATH + "/bot.properties")));
			} catch (FileNotFoundException ignored) {
				Properties p2 = new Properties();
				p.setProperty("configVersion", "0.0.1");
				p.setProperty("dataPath", "./usrdata/");
				p.setProperty("prefix", ">");
				p.store(new FileOutputStream(CONFIG_PATH + "/bot.properties"), null);
				
				throw new IOException(); //break into the last IOException catch block
			}
			DATA_PATH1 = p.getProperty("dataPath");
			DEFAULT_PREFIX1 = p.getProperty("prefix");
			if (!DATA_PATH1.endsWith("/")) DATA_PATH1 += "/";
			if(DEFAULT_PREFIX1.equals(""))
				DEFAULT_PREFIX1 = ">";
			
		} catch (IOException e) {
			e.printStackTrace();
			DATA_PATH1 = CONFIG_PATH + "/usrdata/"; //default
			DEFAULT_PREFIX1 = ">";
		}
		//create the directory if it doesn't
		//already exist.
		//noinspection ResultOfMethodCallIgnored
		new File(DATA_PATH1).mkdirs();
		DEFAULT_PREFIX = DEFAULT_PREFIX1;
		DATA_PATH = DATA_PATH1;
	}
	
	/**
	 * 
	 * Arguments (NOT case sensitive):
	 *      --IKnowWhatImDoingIDontWantToUpgrade - skip upgrade checks.
	 *      --IKnowWhatImDoingDontRegisterCoreCommands - do not register core commands.
	 *      --Stats - prints statistics about the bot and then exits.
	 *      --Version - print the version and then exit.
	 *      --Help - display help.
	 * 
	 * @param args command line arguments, first one is for the token, any
	 *             other arguments not listed in this methods JavaDoc will
	 *             be ignored.
	 */
	public static void main(@NotNull String[] args) {
		
		Logger.log("Starting untitled bot " + VERSION + ".");
		
		if(args.length > 0)  Logger.log("Checking arguments...");
		
		checkArgs(args);
		String token;
		try {
			token = args[0];
		} catch(ArrayIndexOutOfBoundsException ignored) {
			System.out.println("Please input your token (CTRL + V or CTRL + SHIFT + V on some terminals):");
			try {
				token = Arrays.toString(System.console().readPassword());
			} catch(NullPointerException ignored2) {
				System.out.println("WARN: defaulting to plaintext input!");
				token = new Scanner(System.in).nextLine();
			}
		}
		
		if(!noCoreCommands)
			CoreCommands.registerCoreCommands();
		
		try {
			//add JDA discord things
			jda = new JDABuilder(token).setDisabledCacheFlags(
					EnumSet.of(CacheFlag.EMOTE)
			).build();
			jda.addEventListener(new BotClass());
		} catch(LoginException e) {
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
				case "--version":
					System.out.println("untitled-bot version " + VERSION); System.exit(0);
				case "--iknowwhatimdoingdontregistercorecommands":
					noCoreCommands = true; break;
			}
		}
	}
	
}
