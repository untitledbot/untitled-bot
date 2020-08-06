package dev.alexisok.untitledbot;

import dev.alexisok.untitledbot.command.CoreCommands;
import dev.alexisok.untitledbot.data.UserData;
import dev.alexisok.untitledbot.logging.Logger;
import dev.alexisok.untitledbot.modules.cron.Sender;
import dev.alexisok.untitledbot.modules.moderation.ModHook;
import dev.alexisok.untitledbot.modules.vault.Vault;
import dev.alexisok.untitledbot.plugin.PluginLoader;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import javax.security.auth.login.LoginException;
import java.io.*;
import java.nio.file.Paths;
import java.util.*;

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
public final class Main {
	
	public static final String VERSION = "1.3.14";
	public static final String CONFIG_PATH = Paths.get("").toAbsolutePath().toString();
	public static final String DATA_PATH;
	public static final String PREFIX;
	public static final String OWNER_ID;
	
	public static final boolean DEBUG;
	
	public static JDA jda;
	
	static {
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
	}
	
	private static void setDefaultProps(@NotNull Properties p) {
		p.setProperty("configVersion", "1.3.8");
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
		} catch(LoginException | InterruptedException e) {
			e.printStackTrace();
			Logger.critical("Could not login to Discord!", 1);
		}
		
		preStartChecks();
		
		CoreCommands.registerCoreCommands();
		CoreCommands.registerModules();
		
	}
	
	/**
	 * Do startup checks, most of these are to fill in missing
	 * guild directories to make sure that there won't be any errors later on.
	 * These checks are done before the commands and plugins are registered and
	 * after the bot is logged in to discord.
	 */
	@SuppressWarnings("ResultOfMethodCallIgnored")
	private static void preStartChecks() {
		
		for(Guild g : jda.getGuilds()) {
			File fg = new File(Main.DATA_PATH + g.getId());
			if(!fg.exists())
				fg.mkdir();
			
			//do not simply make a new file, that could cause issues
			UserData.checkUserExists(null, g.getId());
			for(Member m : g.getMembers()) {
				if(m.getUser().isBot())
					continue;
				UserData.checkUserExists(m.getId(), g.getId());
			}
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
