package dev.alexisok.untitledbot.modules.rank.xpcommands;

import dev.alexisok.untitledbot.command.CommandRegistrar;
import dev.alexisok.untitledbot.command.EmbedDefaults;
import dev.alexisok.untitledbot.command.Manual;
import dev.alexisok.untitledbot.modules.vault.Vault;
import dev.alexisok.untitledbot.plugin.UBPlugin;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Objects;

/**
 * Implements the `work` command.
 * 
 * @author AlexIsOK
 * @since 1.3.21
 */
public final class Work extends UBPlugin {
    
    private static final ArrayList<String> RESPONSES = new ArrayList<>();
    
    @NotNull
    @Override
    @Contract(pure = true)
    public MessageEmbed onCommand(String[] args, @NotNull Message message) {
        EmbedBuilder eb = new EmbedBuilder();
        EmbedDefaults.setEmbedDefaults(eb, message);
        
        String limitStr = Vault.getUserDataLocal(null, message.getGuild().getId(), "work.cooldown");
        
        long limit = 86400; //1 day
    
        if(limitStr != null && limitStr.matches("[0-9]+"))
            limit = Long.parseLong(limitStr);
        
        if(isRateLimit(message.getAuthor().getId(), message.getGuild().getId(), limit)){
            eb.addField("Work",
                    "You cannot work, as you are rate limited!  Perhaps a moderator could change the limit with the" +
                            " `config` command?",
                    false);
            eb.setColor(Color.RED);
            return eb.build();
        }
        
        
        long randFirst = 
        
        
        eb.addField("Work", );
        
    }
    
    @Override
    public void onRegister() {
        
        //use "UB$%d" for the place of the money earned.
        RESPONSES.add("You work as a developer for [untitled-bot](https://github.com/alexisok/untitled-bot) and make UB$%d.");
        RESPONSES.add("You make UB$%d making a Minecraft Lets Play series.");
        RESPONSES.add("You get your Discord bot verified and earn UB$%d from donations.");
        RESPONSES.add("You set your Discord profile picture to an anime girl and get UB$%d in donations.");
        RESPONSES.add("You mine bitcoin using your old laptop and make UB$%d.");
        RESPONSES.add("You play Untitled Goose Game and get UB$%d in YouTube revenue.");
        RESPONSES.add("???  You stumble across UB$%d when walking your dog!");
        RESPONSES.add("You illegally download UB$ onto your computer, getting UB$%d.  Disgusting.");
        RESPONSES.add("With only one option left, you use 100 percent of your power, gaining UB$%d in the process.");
        RESPONSES.add("You lick the pudding off of the lid like a sane person, gaining UB$%d because of it.");
        RESPONSES.add("You entered the [untitled-bot contest](https://youtu.be/M5V_IXMewl4), gaining UB$%d because of it.");
        RESPONSES.add("UB$%d appears in your pocket as if by MaGiC.");
        
        CommandRegistrar.register("work", this);
        Manual.setHelpPage("work", "Work to get untitled-bot currency (not a real currency).");
    }
    
    /**
     * Is the user being rate limited?  Maybe...
     * 
     * @param userID the ID of the user.
     * @param guildID the ID of the guild.
     * @param cooldownTime the time to wait between the command in seconds.
     * @return {@code true} if the user is being rate-limited, {@code false} otherwise.
     */
    private static boolean isRateLimit(String userID, String guildID, long cooldownTime) {
        String epochOldString = Vault.getUserDataLocal(userID, guildID, "work.cooldown");
        
        if(epochOldString == null) return false;
        
        long epochPrevious = Long.parseLong(epochOldString);
        long epochCurrent  = Instant.now().getEpochSecond();
        
        return epochCurrent - epochPrevious <= cooldownTime;
    }
    
    /**
     * Set the rate limit for the user.
     * This does not need the cooldown time as that is only
     * needed when checking for a rate limit.
     * 
     * @param userID the ID of the user.
     * @param guildID the ID of the guild.
     */
    private static void setRateLimiter(String userID, String guildID) {
        Vault.storeUserDataLocal(userID, guildID, "work.cooldown", String.valueOf(Instant.now().getEpochSecond()));
    }
    
    /**
     * Get the amount of time left for the rate limit in hours.
     * It is recommended to convert this using "%.2f" in a formatting method.
     * 
     * @param userID the ID of the user.
     * @param guildID the ID of the guild.
     * @param cooldownTime the time to wait between the command in seconds.
     * @see String#format(String, Object...)
     * @see java.io.PrintStream#printf(String, Object...)
     * @return the amount of hours left as a double.
     */
    private static double rateLimitTimeHours(String userID, String guildID, long cooldownTime) {
        return ((cooldownTime - (Instant.now().getEpochSecond() - rateLimitTime(userID, guildID))) / 60.0) / 60.0;
    }
    
    /**
     * Get the time for the user rate-limit in unix-epoch.
     * returns 0 if the data is null or empty.
     * @return the time left for rate-limit in seconds.
     */
    private static double rateLimitTime(String userID, String guildID) {
        String a = Vault.getUserDataLocal(userID, guildID, "work.cooldown");
        return a == null || a.isEmpty() ? 0.0 : Double.parseDouble(a) + 0.0;
    }
}
