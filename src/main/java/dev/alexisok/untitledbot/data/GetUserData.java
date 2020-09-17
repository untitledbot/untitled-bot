package dev.alexisok.untitledbot.data;

import dev.alexisok.untitledbot.Main;
import dev.alexisok.untitledbot.command.CommandRegistrar;
import dev.alexisok.untitledbot.command.EmbedDefaults;
import dev.alexisok.untitledbot.modules.vault.Vault;
import dev.alexisok.untitledbot.plugin.UBPlugin;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.io.File;
import java.time.Instant;
import java.util.concurrent.Callable;

/**
 * Allow users to get their data.
 * All data is returned to users in a direct message.
 * 
 * The data is in text files and is sent to the users with one file per message.
 * They are rate-limited to getting their data to once/day.
 * 
 * @author AlexIsOK
 * @since 1.3
 */
public final class GetUserData extends UBPlugin {
    
    @Override
    public @NotNull MessageEmbed onCommand(String[] args, @NotNull Message message) {
    
        EmbedBuilder eb = new EmbedBuilder();
        EmbedDefaults.setEmbedDefaults(eb, message);
        
        eb.addField("Data", "Your data is being fetched.  It will be returned to you in a direct message.", false);
    
        Callable<Void> task = () -> {
            String userID = message.getAuthor().getId();
            
            if(isRateLimit(userID)) {
                message.getAuthor().openPrivateChannel().queue((channel) -> channel.sendMessage("Error: you have hit " +
                                                                                                        "the rate limit for getting " +
                                                                                                        "your data.  Please try again " +
                                                                                                        "in a day or so.").queue());
                return null;
            }
            
            for(Guild g : Main.jda.getGuilds()) {
                if(new File(Main.parsePropertiesLocation(userID, g.getId())).exists()) {
                    message.getAuthor().openPrivateChannel().queue((channel) -> channel.sendFile(new File(Main.parsePropertiesLocation(userID, g.getId())),
                            g.getId() + " " + g.getName() + ".txt").queue());
                }
            }
    
            if(new File(Main.parsePropertiesLocation(userID, null)).exists()) {
                message.getAuthor().openPrivateChannel().queue((channel) -> channel.sendFile(
                        new File(Main.parsePropertiesLocation(userID, null)), "globalData.txt"
                ).queue());
            }
            
            setRateLimiter(userID);
            
            return null;
        };
    
        try {
            task.call();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        eb.setColor(Color.GREEN);
        eb.addField("Data", "Check your DMs.", false);
        
        return eb.build();
    }
    
    /**
     * Registers the `data` command.
     */
    @Override
    public void onRegister() {
        CommandRegistrar.register("data", "core.data", this);
    }
    
    /**
     * Checks if the user is currently under rate-limit.
     * 
     * @param userID the ID of the user.
     * @return true if there is a rate limit, false otherwise.
     */
    private static boolean isRateLimit(String userID) {
        String epochOldString = Vault.getUserDataLocal(userID, null, "data.ratelimit");
        
        if(epochOldString == null) return false;
        
        long epochPrevious = Long.parseLong(epochOldString);
        long epochCurrent  = Instant.now().getEpochSecond();
        
        return epochCurrent - epochPrevious <= 86400;
    }
    
    /**
     * Set the rate limit of the user so they can't spam the command.
     * @param userID the discord ID of the user.
     */
    private static void setRateLimiter(String userID) {
        Vault.storeUserDataLocal(userID, null, "data.ratelimit", String.valueOf(Instant.now().getEpochSecond()));
    }
}
