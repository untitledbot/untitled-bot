package dev.alexisok.untitledbot;

import com.sedmelluq.discord.lavaplayer.jdaudp.NativeAudioSendFactory;
import dev.alexisok.untitledbot.command.CoreCommands;
import dev.alexisok.untitledbot.logging.Logger;
import dev.alexisok.untitledbot.modules.moderation.ModHook;
import dev.alexisok.untitledbot.modules.starboard.Starboard;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import org.discordbots.api.client.DiscordBotListAPI;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
 * 
 * @author AlexIsOK
 * @since 0.0.1
 */
public final class Main {
    
    public static final String VERSION = "1.4.0";
    public static final String CONFIG_PATH = Paths.get("").toAbsolutePath().toString();
    public static final String DATA_PATH;
    public static final String PREFIX;
    public static final String OWNER_ID = "541763812676861952";
    
    public static final int SHARD_COUNT = 4;
    
    public static final boolean DEBUG;
    
    public static JDA[] jda = new JDA[SHARD_COUNT];
    
    private static final String TOP_GG_TOKEN;
    
    public static DiscordBotListAPI API;
    
    public static final ArrayList<String> CONTRIBUTORS = new ArrayList<>();
    
    
    /**
     *
     * Arguments (NOT case sensitive):<br>
     *      --version - print the version and then exit.<br>
     *
     * @param args command line arguments, first one is for the token, any
     *             other arguments not listed in this methods JavaDoc will
     *             be ignored.
     */
    public static void main(@NotNull String[] args) throws InterruptedException {

        Logger.log("Starting untitled bot " + VERSION + ".");
        Logger.log("Starting in location " + System.getProperty("user.dir"));
        
        String token;
        
        try {
            token = args[0];
        } catch(ArrayIndexOutOfBoundsException ignored) {
            Logger.critical("Could not read a token from the first argument!");
            return;
        }
        
        try {
            for(int i = 0; i < SHARD_COUNT; i++) {
                jda[i] = JDABuilder
                        .create(GUILD_MESSAGE_REACTIONS, GUILD_MESSAGES, GUILD_EMOJIS, GUILD_MEMBERS, GUILD_VOICE_STATES, GUILD_BANS, DIRECT_MESSAGES)
                        .disableCache(ACTIVITY, CLIENT_STATUS) //bot does not need presence intents
                        .setToken(token)
                        .useSharding(i, SHARD_COUNT)
                        .enableCache(MEMBER_OVERRIDES, EMOTE)
                        .setMemberCachePolicy(MemberCachePolicy.ONLINE.and(OWNER).and(VOICE))
                        .addEventListeners(new ModHook(), new BotClass(), new Starboard())
                        .setAudioSendFactory(new NativeAudioSendFactory()) //mitigates packet loss according to JDA NAS.
                        .build();
                jda[i].getPresence().setPresence(OnlineStatus.ONLINE, Activity.of(Activity.ActivityType.DEFAULT, ">help | now with buttons!"));
                jda[i].awaitReady();
            }
        } catch(LoginException e) {
            e.printStackTrace();
            Logger.critical("Could not login to Discord!");
        }

        CoreCommands.registerCoreCommands();
        CoreCommands.registerModules();

    }
    
    /**
     * Get a specific {@link JDA} from a guild ID.
     * @param guildID the guild ID.
     * @return the JDA.
     */
    @NotNull
    @Contract(pure = true)
    public static JDA getJDAFromGuild(long guildID) {
        return jda[(int) ((guildID >> 22) % SHARD_COUNT)];
    }
    
    /**
     * Get a specific {@link JDA} from a guild ID.
     * @param guildID the guild ID.
     * @return the JDA.
     */
    @NotNull
    @Contract(pure = true)
    public static JDA getJDAFromGuild(@NotNull String guildID) {
        return getJDAFromGuild(Long.parseLong(guildID));
    }
    
    /**
     * Get a specific {@link JDA} from a guild.
     * @param g the guild
     * @return the JDA.
     */
    @NotNull
    @Contract(pure = true)
    public static JDA getJDAFromGuild(@NotNull Guild g) {
        return getJDAFromGuild(g.getIdLong());
    }
    
    /**
     * Gets the total amount of servers the bot is connected to.
     * @return the amount of servers.
     */
    @Contract(pure = true)
    public static int getGuildCount() {
        int count = 0;
        for(JDA j : jda) {
            count += j.getGuilds().size();
        }
        return count;
    }
    
    /**
     * Get the total {@link User} count across all shards.
     * @return the user count.
     */
    @Contract(pure = true)
    public static int getUserCount() {
        int count = 0;
        for(JDA j : jda) {
            count += j.getUsers().size();
        }
        return count;
    }
    
    /**
     * Resets the contributors list and reads from the file.
     */
    public static void resetContributors() {
        CONTRIBUTORS.clear();
        if(new File("contributors.properties").exists()) {
            Properties p = new Properties();
            try {
                p.load(new FileInputStream("contributors.properties"));
                p.forEach((o, o2) -> CONTRIBUTORS.add(o.toString()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    private static void setDefaultProps(@NotNull Properties p) {
        p.setProperty("configVersion", "1.3.8");
        p.setProperty("dataPath", "./usrdata/");
        p.setProperty("prefix", ">");
        p.setProperty("ownerId", "000000000000000000");
        p.setProperty("debugMode", "false");
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
    @NotNull
    @Contract(pure = true)
    public static String parsePropertiesLocation(String userID, String guildID) {
        if(userID == null && guildID == null)
            return DATA_PATH;
        if(userID == null) return DATA_PATH + guildID + ".properties";
        if(guildID == null) return DATA_PATH + userID + ".properties";
        return DATA_PATH + guildID + "/" + userID + ".properties";
    }
    
    /**
     * @return the average ping across all guilds.
     */
    @Contract(pure = true)
    public static int getPingAverage() {
        int total = 0;
        for(JDA j : jda) {
            total += j.getGatewayPing();
        }
        return total / SHARD_COUNT;
    }
    
    /**
     * @return the minimum ping of one of the guilds.
     */
    @Contract(pure = true)
    public static int getPingMin() {
        int[] counts = new int[SHARD_COUNT];
        for(int i = 0; i < jda.length; i++) {
            counts[i] = (int) jda[i].getGatewayPing();
        }
        Arrays.sort(counts);
        return counts[0];
    }
    
    /**
     * @return the maximum ping of one of the guilds.
     */
    @Contract(pure = true)
    public static int getPingMax() {
        int[] counts = new int[SHARD_COUNT];
        for(int i = 0; i < jda.length; i++) {
            counts[i] = (int) jda[i].getGatewayPing();
        }
        Arrays.sort(counts);
        return counts[SHARD_COUNT - 1];
    }
    
    /**
     * Gets a {@link Role} by ID from the Role cache.
     * 
     * Note: this iterates over all shards.
     * 
     * @param roleID the ID of the role.
     * @return the {@link Role} or {@code null} if it wasn't found.
     */
    @Nullable
    @Contract(pure = true)
    public static Role getRoleById(String roleID) {
        Role r = null;
        for(JDA j : jda) {
            r = j.getRoleById(roleID);
            if(r != null) //found role
                break;
        }
        return r;
    }
    
    /**
     * Gets a {@link VoiceChannel} by ID from the VC cache.
     *
     * Note: this iterates over all shards.
     *
     * @param vcID the ID of the VoiceChannel
     * @return the {@link VoiceChannel} or {@code null} if it wasn't found.
     */
    @Nullable
    @Contract(pure = true)
    public static VoiceChannel getVoiceChannelById(String vcID) {
        VoiceChannel r = null;
        for(JDA j : jda) {
            r = j.getVoiceChannelById(vcID);
            if(r != null) //found vc
                break;
        }
        return r;
    }
    
    /**
     * Gets a {@link TextChannel} by ID from the TC cache.
     *
     * Note: this iterates over all shards.
     *
     * @param tcID the ID of the TextChannel
     * @return the {@link TextChannel} or {@code null} if it wasn't found.
     */
    @Nullable
    @Contract(pure = true)
    public static TextChannel getTextChannelById(String tcID) {
        TextChannel r = null;
        for(JDA j : jda) {
            r = j.getTextChannelById(tcID);
            if(r != null) //found tc
                break;
        }
        return r;
    }
    
    /**
     * Gets a {@link User} by ID from the User cache.
     *
     * Note: this iterates over all shards.
     *
     * @param userID the ID of the User.
     * @return the {@link User} or {@code null} if it wasn't found.
     */
    @Nullable
    @Contract(pure = true)
    public static User getUserById(long userID) {
        User u = null;
        for(JDA j : jda) {
            u = j.getUserById(userID);
            if(u != null)
                break;
        }
        return u;
    }
    
    /**
     * Check {@link Main#SHARD_COUNT} to see the total amount of shards.
     * 
     * Note: shards start at 0.
     * 
     * Get a specific JDA by the shard ID.
     * @param i the ID of the shard.
     * @return the specific JDA.
     * @throws IllegalArgumentException if the range is out of bounds.
     */
    @NotNull
    @Contract(pure = true)
    public static JDA getJDAByShardId(int i) {
        if(i < 0 || i >= SHARD_COUNT)
            throw new IllegalArgumentException("The ID of the shard is not in the range of [0, " + SHARD_COUNT + ").");
        return jda[i];
    }
    
    
    static {
        String DEFAULT_PREFIX1;
        //temp for final string
        String DATA_PATH1;
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
            DEBUG1 = Boolean.parseBoolean(p.getProperty("debugMode"));
            if (!DATA_PATH1.endsWith("/")) DATA_PATH1 += "/";
            if(DEFAULT_PREFIX1.equals(""))
                DEFAULT_PREFIX1 = ">";
            
        } catch (IOException e) {
            e.printStackTrace();
            DATA_PATH1 = CONFIG_PATH + "/usrdata/"; //default
            DEFAULT_PREFIX1 = ">";
            DEBUG1 = false;
        }
        debug("Done with config stuff");
        //create the directory if it doesn't
        //already exist.
        //noinspection ResultOfMethodCallIgnored
        new File(DATA_PATH1).mkdirs();
        PREFIX = DEFAULT_PREFIX1;
        DATA_PATH = DATA_PATH1;
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
                        API.setStats(getGuildCount());
                        Logger.log("Done updating stats.");
                    }
                };
                
                //delay the first schedule by 20 seconds and update every 20 minutes
                new Timer().schedule(updateServerCountTask, 20000L, 1200000L);
            }
        });
        
        t.start();
        
        if(new File("contributors.properties").exists()) {
            Properties p = new Properties();
            try {
                p.load(new FileInputStream("contributors.properties"));
                p.forEach((o, o2) -> CONTRIBUTORS.add(o.toString()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        if(new File("blacklist.properties").exists()) {
            Properties p = new Properties();
            try {
                p.load(new FileInputStream("blacklist.properties"));
                p.forEach((o, o2) -> BotClass.addToBlacklist(Long.parseLong(o.toString())));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Get a specific Guild by its ID.
     * @param id the ID of the guild.
     */
    @Nullable
    @Contract(pure = true)
    public static Guild getGuildById(String id) {
        for(JDA j : jda) {
            Guild g = j.getGuildById(id);
            if(g != null)
                return g;
        }
        return null;
    }
}
