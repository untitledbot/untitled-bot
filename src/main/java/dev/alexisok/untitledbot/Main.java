package dev.alexisok.untitledbot;

import dev.alexisok.untitledbot.command.CoreCommands;
import dev.alexisok.untitledbot.logging.Logger;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import javax.security.auth.login.LoginException;
import java.io.*;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Properties;
import java.util.Scanner;
import java.util.stream.Collectors;

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
	
	private static boolean noCoreCommands = false;
	private static boolean noModules = false;
	
	
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
	 * Arguments (NOT case sensitive):<br>
	 *      --IKnowWhatImDoingIDontWantToUpgrade - skip upgrade checks.<br>
	 *      --IKnowWhatImDoingDontRegisterCoreCommands - do not register core commands.<br>
	 *      --IKnowWhatImDoingDontRegisterAnyModules - do not register modules.<br>
	 *      --Version - print the version and then exit.<br>
	 *      --Help - display help.<br>
	 * 
	 * @param args command line arguments, first one is for the token, any
	 *             other arguments not listed in this methods JavaDoc will
	 *             be ignored.
	 */
	public static void main(@NotNull String[] args) {
		
		Logger.log("Starting untitled bot " + VERSION + ".");
		
		if(args.length > 0)  Logger.log("Checking arguments...");
		
		checkArgs(args.clone());
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
		if(!noModules)
			CoreCommands.registerModules();
		
		try {
			//add JDA discord things
			jda = new JDABuilder(token).build();
			jda.addEventListener(new BotClass());
			jda.awaitReady();
		} catch(LoginException | InterruptedException e) {
			e.printStackTrace();
			Logger.critical("Could not login to Discord!", 1);
		}
	}
	
	/**
	 * Check for upgrades.
	 * This will be skipped if the no-upgrade flag is used.
	 */
	private static void checkForUpgrades() {
		if(!checkForUpdates)
			return;
		
		final String UPDATE_URL = "https://api.github.com/repos/alexisok/untitled-bot/tags";
		
		String fullString;
		//TODO check for updates
		try(BufferedReader r = new BufferedReader(new InputStreamReader(new URL(UPDATE_URL).openStream()))) {
			fullString = r.lines().collect(Collectors.joining());
		} catch(IOException ignored) {
			Logger.critical("There was an error checking for updates!", 0, false);
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
				case "--instantbreak":
					Logger.critical("Instant break: activated!", 2);
				case "--version":
					System.out.println("untitled-bot version " + VERSION); System.exit(0);
				case "--iknowwhatimdoingdontregistercorecommands":
					noCoreCommands = true; break;
				case "--iknowwhatimdoingdontregisteranymodules":
					noModules = true; break;
			}
		}
	}
	
	/**
	 * Return the resulting properties location of a specific user
	 * given their user ID and guild ID.
	 * @param userID the discord snowflake of the user.
	 * @param guildID the discord snowflake of the guild.
	 * @return the parsed properties location.
	 */
	@Contract(pure = true)
	public static @NotNull String parsePropertiesLocation(String userID, String guildID) {
		return Main.DATA_PATH + guildID + "/" + userID + ".properties";
	}
	
}
