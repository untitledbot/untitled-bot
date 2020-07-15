package dev.alexisok.untitledbot;

import dev.alexisok.untitledbot.command.CoreCommands;
import dev.alexisok.untitledbot.data.UserData;
import dev.alexisok.untitledbot.logging.Logger;
import dev.alexisok.untitledbot.modules.cron.Sender;
import dev.alexisok.untitledbot.modules.moderation.ModHook;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import javax.security.auth.login.LoginException;
import java.io.*;
import java.nio.file.Paths;
import java.util.Arrays;
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
	public static final String PREFIX;
	public static final String OWNER_ID;
	
	public static final boolean DEBUG;
	
	//bot status
	private static final String[] MESSAGE_OF_THE_DAY = {
			"Playing with time", "Playing with magic", "Doing nothing at all", "ta\u0358st\u0360e\u0335 t\u0489\u0335\u0360h\u034F\u035De\u0337 S\u036C\u0357\u0352\u0305\u0362\u0322\u034D\u0354\u0330\u0347\u0318\u0325\u0332\u0332\u0339\u033B\u0324\u0330U\u0346\u0311\u030D\u0357\u033E\u0350\u0366\u030A\u030F\u0310\u036D\u0346\u0309\u0366\u036A\u0346\u033F\u0313\u0364\u0321\u035F\u031B\u031B\u0358\u031C\u0330\u0331\u0318\u0345\u0323\u033C\u0349\u0318N\u0302\u0342\u030E\u0306\u0302\u0342\u0344\u0311\u0358\u0337\u0361\u0338\u0318\u0319\u032E\u032F\u0345\u0319\u0359\u0329\u0324\u0353",
		    "Watching your every move", "Drinking Java", "insert motd here"
	};
	
	private static boolean noCoreCommands = false;
	private static boolean noModules = false;
	
	
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
				p.setProperty("configVersion", "0.0.1");
				p.setProperty("dataPath", "./usrdata/");
				p.setProperty("prefix", ">");
				p.setProperty("ownerId", "000000000000000000");
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
	
	/**
	 * 
	 * Arguments (NOT case sensitive):<br>
	 *      --IKnowWhatImDoingDontRegisterCoreCommands - do not register core commands.<br>
	 *      --IKnowWhatImDoingDontRegisterAnyModules - do not register modules.<br>
	 *      --Version - print the version and then exit.<br>
	 *      --Help - display help.<br>
	 *      --
	 * 
	 * @param args command line arguments, first one is for the token, any
	 *             other arguments not listed in this methods JavaDoc will
	 *             be ignored.
	 */
	@SuppressWarnings("deprecation")
	public static void main(@NotNull String[] args) {
		
		checkArgs(args.clone());
		
		Logger.log("Starting untitled bot " + VERSION + ".");
		
		String token;
		
//		Logger.log("Loading plugins...");
//		PluginLoader.loadPlugins();
//		Logger.log("Plugin loading done.");
		
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
//					      .setActivity(Activity.of(Activity.ActivityType.CUSTOM_STATUS,"with time"))
						  .disableCache(CacheFlag.ACTIVITY)
					      .build();
			jda.addEventListener(new BotClass()); //main bot class
			jda.addEventListener(new ModHook());  //logging module
			jda.addEventListener(new Sender());   //event message sender module
			jda.awaitReady();
		} catch(LoginException | InterruptedException e) {
			e.printStackTrace();
			Logger.critical("Could not login to Discord!", 1);
		}
		
		preStartChecks();
		
		if(!noCoreCommands)
			CoreCommands.registerCoreCommands();
		if(!noModules)
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
			switch(s) {
				case "--instantbreak":
					Logger.critical("Instant break: activated!", 2);
				case "--version":
					System.out.println(VERSION);
					System.exit(0);
				case "--iknowwhatimdoingdontregistercorecommands":
					noCoreCommands = true;
					break;
				case "--iknowwhatimdoingdontregisteranymodules":
					noModules = true;
					break;
				default:
					Logger.log("Unrecognized argument " + s + ", skipping...");
					break;
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
	 * @param guildID the discord snowflake of the guild.
	 * @return the parsed properties location.
	 */
	@Contract(pure = true)
	public static @NotNull String parsePropertiesLocation(String userID, @NotNull String guildID) {
		if (userID == null) return DATA_PATH + guildID + ".properties";
		return DATA_PATH + guildID + "/" + userID + ".properties";
	}
	
}
