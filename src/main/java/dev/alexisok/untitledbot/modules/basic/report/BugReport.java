package dev.alexisok.untitledbot.modules.basic.report;

import dev.alexisok.untitledbot.BotClass;
import dev.alexisok.untitledbot.Main;
import dev.alexisok.untitledbot.command.CommandRegistrar;
import dev.alexisok.untitledbot.command.EmbedDefaults;
import dev.alexisok.untitledbot.command.Manual;
import dev.alexisok.untitledbot.util.vault.Vault;
import dev.alexisok.untitledbot.plugin.UBPlugin;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.time.Instant;
import java.util.Objects;

/**
 * report bugs
 * this does not tell the user how long they have left
 * 
 * @author AlexIsOK
 * @since 1.3
 */
public final class BugReport extends UBPlugin {
    
    @Override
    public @NotNull MessageEmbed onCommand(String[] args, @NotNull Message message) {
        EmbedBuilder eb = new EmbedBuilder();
        
        if(args.length == 1) {
            eb.addField("Error", "Please provide... you know... *a bug to report*.", false);
            eb.setColor(Color.RED);
            return eb.build();
        }
        
        //this is backwards oops whatever
        if(!isRateLimit(message.getAuthor().getId())) {
            eb.addField("Error", "You are being rate limited, please try again later.", false);
            eb.setColor(Color.RED);
            return eb.build();
        }
        
        setRateLimiter(message.getAuthor().getId());
        eb.addField("Bug Report", message.getContentRaw(), false);
        eb.setAuthor(message.getAuthor().getId());
        
        Objects.requireNonNull(Main.getJDAFromGuild(730122694250725380L).getTextChannelById("747228587026940006")).sendMessage(eb.build()).queue(r -> BotClass.addToDeleteCache(message.getId(), r));
    
        EmbedDefaults.setEmbedDefaults(eb, message);
        
        eb.addField("The above content has been reported.", "Thank you~\nNote: abuse of this command will result in " +
                                                                    "banishment of this command.  This is your only warning.\n\n" +
                                                                    "I will not be able to reply through this command, so if you " +
                                                                    "want help with the bot, please [join the Discord server](https://discord.gg/vSWgQ9a).", false);
        
        return eb.build();
    }
    
    @Override
    public void onRegister() {
        CommandRegistrar.register("bug-report", this);
        Manual.setHelpPage("bug-report", "Report bugs directly to me (this will send your user ID as well, " +
                                                 "you *will* be blacklisted if you abuse this command).");
    }
    
    
    /**
     *
     * @param userID the ID of the user.
     * @return true if there is a rate limit, false otherwise.
     */
    private static boolean isRateLimit(String userID) {
        String epochOldString = Vault.getUserDataLocal(userID, null, "bugreport.report");
        
        if(epochOldString == null) return true;
        
        long epochPrevious = Long.parseLong(epochOldString);
        long epochCurrent  = Instant.now().getEpochSecond();
        
        return epochCurrent - epochPrevious >= 86400; //24 hours
    }
    
    private static void setRateLimiter(String userID) {
        Vault.storeUserDataLocal(userID, null, "bugreport.report", String.valueOf(Instant.now().getEpochSecond()));
    }
    
    private static double rateLimitTimeHours(String userID) {
        return ((21600.0 - (Instant.now().getEpochSecond() - rateLimitTime(userID))) / 60.0) / 60.0;
    }
    
    /**
     * Get the time for the user rate-limit in unix-epoch.
     * returns 0 if the data is null or empty.
     * @return the time left for rate-limit in seconds
     */
    private static double rateLimitTime(String userID) {
        String a = Vault.getUserDataLocal(userID, null, "bugreport.report");
        return a == null || a.isEmpty() ? 0.0 : Double.parseDouble(a) + 0.0;
    }
    
}
