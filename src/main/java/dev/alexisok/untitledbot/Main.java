package dev.alexisok.untitledbot;

import dev.alexisok.untitledbot.command.CoreCommands;
import dev.alexisok.untitledbot.logging.Logger;
import dev.alexisok.untitledbot.modules.cron.Sender;
import dev.alexisok.untitledbot.modules.moderation.ModHook;
import dev.alexisok.untitledbot.modules.vault.Vault;
import dev.alexisok.untitledbot.plugin.PluginLoader;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.discordbots.api.client.DiscordBotListAPI;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import javax.security.auth.login.LoginException;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;

/**
 * 
 * Main class, contains the main method.
 * This class is what sets {@link BotClass} as a bot.
 * 
 * For more information on developing, use the wiki
 * posted on the GitHub page.
 * 
 * Note: this program is one-shard, meaning that one shard handles EVERYTHING.
 * It was set up this way, but will be changed in the future.
 * 
 * @author AlexIsOK
 * @since 0.0.1
 */
public final class Main {
	
	public static final String VERSION = "1.3.20";
	public static final String CONFIG_PATH = Paths.get("").toAbsolutePath().toString();
	public static final String DATA_PATH;
	public static final String PREFIX;
	public static final String OWNER_ID;
	public static final String STATS_DIR = String.format("%s/stats", Paths.get("").toAbsolutePath().toString());
	public static final String DAY_FOR_STATS = new Date().toString();
	
	public static final DiscordBotListAPI API;
	
	private static final String SECRETS_FILE = CONFIG_PATH + "/secrets.properties";
	
	private static final TimerTask UPDATE_COUNT;
	
	public static final boolean DEBUG;
	
	public static JDA[] jdas;
	
	static {
		DiscordBotListAPI API1;
		Runtime.getRuntime().addShutdownHook(new ShutdownHook());
		String DEFAULT_PREFIX1;
		//temp for final string
		String DATA_PATH1;
		String OWNER_ID1;
		boolean DEBUG1;
		try {
			
			Properties p = new Properties();
			try {
				p.load(new FileInputStream(new File(CONFIG_PATH + "/bot.properties")));
			} catch (FileNotFoundException ignored) {
				setDefaultProps(p);
				p.store(new FileOutputStream(CONFIG_PATH + "/bot.properties"), null);
				throw new IOException(); //break into the last IOException catch block
			}
			DATA_PATH1 = p.getProperty("dataPath");
			DEFAULT_PREFIX1 = p.getProperty("prefix");
			OWNER_ID1 = p.getProperty("ownerId");
			DEBUG1 = Boolean.parseBoolean(p.getProperty("debugMode"));
			if (!DATA_PATH1.endsWith("/")) DATA_PATH1 += "/";
			if(DEFAULT_PREFIX1.equals(""))
				DEFAULT_PREFIX1 = ">";
			
		} catch (IOException e) {
			e.printStackTrace();
			DATA_PATH1 = CONFIG_PATH + "/usrdata/"; //default
			DEFAULT_PREFIX1 = ">";
			OWNER_ID1 = "0";
			DEBUG1 = false;
		}
		//create the directory if it doesn't
		//already exist.
		//noinspection ResultOfMethodCallIgnored
		new File(DATA_PATH1).mkdirs();
		PREFIX = DEFAULT_PREFIX1;
		DATA_PATH = DATA_PATH1;
		OWNER_ID = OWNER_ID1;
		DEBUG = DEBUG1;
		
		//register RAM statistics
		
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				try {
					Files.write(Paths.get(String.format("%s/%s", STATS_DIR, DAY_FOR_STATS)),
							ramStats().getBytes(),
							StandardOpenOption.APPEND);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};
		
		new Timer().schedule(task, 1000, 5000);
		try {new File(String.format("%s/%s", STATS_DIR, DAY_FOR_STATS)).createNewFile();} catch (IOException e) {e.printStackTrace();}
		
		UPDATE_COUNT = new TimerTask() {
			@Override
			public void run() {
				API.setStats(Main.jda.getGuilds().size());
			}
		};
		
		Properties a = new Properties();
		try {
			a.load(new FileReader(SECRETS_FILE));
			
			String APIToken = a.getProperty("top.gg.token");
			
			if(APIToken.isEmpty())
				throw new IOException();
			
			API1 = new DiscordBotListAPI.Builder()
					      .token(APIToken)
					      .botId("730135989863055472") // :)
					      .build();
		} catch (IOException e) {
			e.printStackTrace();
			API1 = null;
		}
		API = API1;
	}
	
	/**
	 * Get the stats of ram for the timer task in the static block
	 * will be run every 5 seconds
	 * @return the ram stats
	 */
	@NotNull
	private static String ramStats() {
		String returnString = String.format("%n%s%n", new Date().toString());
		returnString += String.format("Free: %d%n", Runtime.getRuntime().freeMemory() / 1024 / 1024);
		returnString += String.format("Total: %d%n", Runtime.getRuntime().totalMemory() / 1024 / 1024);
		
		return returnString;
	}
	
	private static void setDefaultProps(@NotNull Properties p) {
		p.setProperty("configVersion", "1.3.20");
		p.setProperty("dataPath", "./usrdata/");
		p.setProperty("prefix", ">");
		p.setProperty("ownerId", "000000000000000000");
		p.setProperty("debugMode", "false");
	}
	
	/**
	 * 
	 * Arguments (NOT case sensitive):<br>
	 *      --version - print the version and then exit.<br>
	 * 
	 * @param args command line arguments, first one is for the token, any
	 *             other arguments not listed in this methods JavaDoc will
	 *             be ignored.
	 */
	@SuppressWarnings("deprecation")
	public static void main(@NotNull String[] args) {
	    
		checkArgs(args.clone());
		
		Logger.log("Starting untitled bot " + VERSION + ".");
		Logger.log("Starting in location " + System.getProperty("user.dir"));
		
		String token;
		
		Logger.log("Loading plugins...");
		PluginLoader.loadPlugins();
		Logger.log("Plugin loading done.");
		
		Logger.log("Installing VAULT scheduler...");
		Vault.operationScheduler();
		Logger.log("Installed.");
		
		try {
			token = args[0];
		} catch(ArrayIndexOutOfBoundsException ignored) {
			System.out.println("Please input your token (CTRL + V or CTRL + SHIFT + V on some terminals):");
			try {
				token = Arrays.toString(System.console().readPassword());
			} catch(NullPointerException ignored2) {
				System.out.println("WARNING: defaulting to plaintext input!");
				token = new Scanner(System.in).nextLine();
			}
		}
		
		try {
			
			//add JDA discord things
			//this is deprecated but using the new version causes
			//errors that prevent this from compiling.
			jda = new JDABuilder(token)
						  .disableCache(CacheFlag.ACTIVITY)
						  .setMemberCachePolicy(MemberCachePolicy.ALL)
					      .build();
			jda.getPresence().setPresence(
					OnlineStatus.ONLINE,
					Activity.of(Activity.ActivityType.DEFAULT,
							">help"));
			jda.addEventListener(new BotClass()); //main bot class
			jda.addEventListener(new ModHook());  //logging module
			jda.addEventListener(new Sender());   //event message sender module
			jda.awaitReady();
			
			//start top.gg api
			if(API != null) {
				Logger.log("Installing top.gg scheduler...");
				//wait 5 seconds for the first and run every 20 minutes (~72 times/day)
				new Timer().schedule(UPDATE_COUNT, 5000, 1200000);
				Logger.log("top.gg scheduler installed.");
			} else {
				Logger.critical("API was null, meaning that the token was not found or there was an error reading secrets.properties.", -1, false);
				Logger.critical("This is not something to worry about if you are self-hosting.", -1, false);
			}
		} catch(LoginException | InterruptedException e) {
			e.printStackTrace();
			Logger.critical("Could not login to Discord!", 1);
		}
		
		CoreCommands.registerCoreCommands();
		CoreCommands.registerModules();
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
			if ("--version".equals(s)) {
				System.out.println("v" + VERSION);
				System.exit(0);
			}
		}
	}
	
	/**
	 * Return the resulting properties location of a specific user
	 * given their user ID and guild ID.  Having a bunch of other things relying on this
	 * is good as it is much easier to change it.  If you are getting the properties locaation
	 * of the user, then go through here.
	 * 
	 * 
	 * @param userID the discord snowflake of the user.  Can be {@code null} for global config.
	 * @param guildID the discord snowflake of the guild.  Can be {@code null} for user config.
	 * @return the parsed properties location.
	 */
	@Contract(pure = true)
	public static @NotNull String parsePropertiesLocation(String userID, String guildID) {
		if(userID == null && guildID == null)
			return DATA_PATH;
		if(userID == null) return DATA_PATH + guildID + ".properties";
		if(guildID == null) return DATA_PATH + userID + ".properties";
		return DATA_PATH + guildID + "/" + userID + ".properties";
	}
	
}
