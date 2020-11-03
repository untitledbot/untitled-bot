package dev.alexisok.untitledbot;

import dev.alexisok.untitledbot.command.CoreCommands;
import dev.alexisok.untitledbot.logging.Logger;
import dev.alexisok.untitledbot.modules.moderation.ModHook;
import dev.alexisok.untitledbot.modules.starboard.Starboard;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import org.discordbots.api.client.DiscordBotListAPI;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import javax.security.auth.login.LoginException;
import java.io.*;
import java.nio.file.Paths;
import java.util.*;

import static dev.alexisok.untitledbot.logging.Logger.debug;
import static net.dv8tion.jda.api.requests.GatewayIntent.*;
import static net.dv8tion.jda.api.utils.MemberCachePolicy.OWNER;
import static net.dv8tion.jda.api.utils.MemberCachePolicy.VOICE;
import static net.dv8tion.jda.api.utils.cache.CacheFlag.*;

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
    
    public static final String VERSION = "1.3.23";
    public static final String CONFIG_PATH = Paths.get("").toAbsolutePath().toString();
    public static final String DATA_PATH;
    public static final String PREFIX;
    public static final String OWNER_ID;
    
    public static final boolean DEBUG;
    
    public static JDA jda;
    
    private static final String TOP_GG_TOKEN;
    
    public static DiscordBotListAPI API;
    
    static {
        debug("Installing shutdown hook");
        Runtime.getRuntime().addShutdownHook(new ShutdownHook());
        String DEFAULT_PREFIX1;
        //temp for final string
        String DATA_PATH1;
        String OWNER_ID1;
        boolean DEBUG1;
        debug("Starting to do the config stuffs");
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
        debug("Done with config stuff");
        //create the directory if it doesn't
        //already exist.
        //noinspection ResultOfMethodCallIgnored
        new File(DATA_PATH1).mkdirs();
        PREFIX = DEFAULT_PREFIX1;
        DATA_PATH = DATA_PATH1;
        OWNER_ID = OWNER_ID1;
        DEBUG = DEBUG1;
        
        Properties secrets = new Properties();
        String secretsTemp = "none";
        try {
            secrets.load(new FileReader(CONFIG_PATH + "/secrets.properties"));
            secretsTemp = secrets.getProperty("top.gg.token", "none");
            if(secretsTemp == null) {
                secretsTemp = "none";
            }
        } catch (IOException e) {
            Logger.log("Could not load the Top.GG secrets file!");
            Logger.log("If you are self-hosting, this is normal.");
        }
        
        TOP_GG_TOKEN = secretsTemp;
        
        Thread t = new Thread(() -> {
            
            if(!TOP_GG_TOKEN.equals("none")) {
                API = new DiscordBotListAPI.Builder()
                    .token(TOP_GG_TOKEN)
                    .botId("730135989863055472")
                    .build();
                
                TimerTask updateServerCountTask = new TimerTask() {
                    @Override
                    public void run() {
                        Logger.log("Updating Top.GG stats...");
                        API.setStats(jda.getGuilds().size());
                        Logger.log("Done updating stats.");
                    }
                };
                
                //delay the first schedule by 20 seconds and update every 20 minutes
                new Timer().schedule(updateServerCountTask, 20000L, 1200000L);
            }
        });
    
        t.start();
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
    public static void main(@NotNull String[] args) {
        
        Logger.log("Starting untitled bot " + VERSION + ".");
        Logger.log("Starting in location " + System.getProperty("user.dir"));
        
        String token;
        
        try {
            token = args[0];
        } catch(ArrayIndexOutOfBoundsException ignored) {
            Logger.critical("Could not read a token from the first argument!", 5, true);
            return;
        }
        
        try {
            //message reactions may be used for a future release
            jda = JDABuilder.create(GUILD_MESSAGE_REACTIONS, GUILD_MESSAGES, GUILD_EMOJIS, GUILD_MEMBERS)
                    .setToken(token)
                    .enableCache(MEMBER_OVERRIDES, EMOTE)
                    .setMemberCachePolicy(MemberCachePolicy.ONLINE.and(OWNER).and(VOICE))
                    .addEventListeners(new ModHook(), new BotClass(), new Starboard())
                    .build();
            jda.getPresence().setPresence(OnlineStatus.ONLINE, Activity.of(Activity.ActivityType.DEFAULT, ">help"));
            
        } catch(LoginException e) {
            e.printStackTrace();
            Logger.critical("Could not login to Discord!", 1);
        }
        
        CoreCommands.registerCoreCommands();
        CoreCommands.registerModules();
        
    }
    
    /**
     * Return the resulting properties location of a specific user
     * given their user ID and guild ID.  Having a bunch of other things relying on this
     * is good as it is much easier to change it.  If you are getting the properties location
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
