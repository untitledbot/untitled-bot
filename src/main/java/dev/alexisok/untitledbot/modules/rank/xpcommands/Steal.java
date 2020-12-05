package dev.alexisok.untitledbot.modules.rank.xpcommands;

import dev.alexisok.untitledbot.Main;
import dev.alexisok.untitledbot.command.CommandRegistrar;
import dev.alexisok.untitledbot.command.EmbedDefaults;
import dev.alexisok.untitledbot.command.Manual;
import dev.alexisok.untitledbot.modules.vault.Vault;
import dev.alexisok.untitledbot.plugin.UBPlugin;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.time.Instant;
import java.util.Arrays;
import java.util.Objects;
import java.util.Random;

/**
 * @author AlexIsOK
 * @since 1.3.21
 */
public final class Steal extends UBPlugin {
    
    @Override
    public @NotNull MessageEmbed onCommand(@NotNull String[] args, @NotNull Message message) {
        EmbedBuilder eb = new EmbedBuilder();
        EmbedDefaults.setEmbedDefaults(eb, message);
        
        int min = Integer.parseInt(Vault.getUserDataLocalOrDefault(null, message.getGuild().getId(), "steal.limit.minimum", "50"));
        int max = Integer.parseInt(Vault.getUserDataLocalOrDefault(null, message.getGuild().getId(), "steal.limit.maximum", "300"));
        int chance = Integer.parseInt(Vault.getUserDataLocalOrDefault(null, message.getGuild().getId(), "steal.chance", "50"));
        int timeout = Integer.parseInt(Vault.getUserDataLocalOrDefault(null, message.getGuild().getId(), "steal.cooldown", "86400"));
        
        if(isRateLimit(message.getAuthor().getId(), message.getGuild().getId(), timeout)) {
            eb.addField("Steal", String.format("You are being rate limited!\n" +
                                                       "Please try again in %.2f hours or have a moderator change the ratelimit with the `config` command.",
                    rateLimitTimeHours(message.getAuthor().getId(), message.getGuild().getId(), timeout)), false);
            eb.setColor(Color.RED);
            return eb.build();
        }
        
        User m;
        
        try {
            m = args[1].matches("[0-9]+")
                             ? Objects.requireNonNull(message.getJDA().getUserById(args[1]))
                             : message.getMentionedMembers().get(0).getUser();
        } catch(Throwable t) {
            eb.addField("Steal", "Usage: `steal <user @ | user ID>`", false);
            eb.setColor(Color.RED);
            return eb.build();
        }
        
        if(m.getId().equals(message.getAuthor().getId())) {
            eb.addField("Steal", "You tried to steal from yourself but ended up getting a full 8 hours of sleep instead.", false);
            eb.setColor(Color.YELLOW);
            return eb.build();
        }
        
        if(Long.parseLong(Vault.getUserDataLocalOrDefault(message.getAuthor().getId(), message.getGuild().getId(), Shop.CURRENCY_VAULT_NAME, "0")) < max) {
            eb.addField("Steal", "You must have at least UB$" + max + " to use this command.", false);
            eb.setColor(Color.RED);
            return eb.build();
        }
        
        if(Long.parseLong(Vault.getUserDataLocalOrDefault(m.getId(), message.getGuild().getId(), Shop.CURRENCY_VAULT_NAME, "0")) < max) {
            eb.addField("Steal", "The person you are trying to steal from must have at least UB$" + max + ".", false);
            eb.setColor(Color.RED);
            return eb.build();
        }
        
        if(max < min) {
            eb.addField("Steal", "The `steal` command was configured incorrectly.  Please re-configure the command using the " +
                                         "`config` command and make sure that the maximum is greater than or equal to the minimum.", false);
            eb.setColor(Color.RED);
            return eb.build();
        }
        
        boolean success = (new Random(message.getIdLong()).nextInt(100) + 1) <= chance;
        
        long amount = new Random().nextInt(max) + min;
        
        long   userMoney = Long.parseLong(Vault.getUserDataLocalOrDefault(message.getAuthor().getId(), message.getGuild().getId(), Shop.CURRENCY_VAULT_NAME, "0"));
        long targetMoney = Long.parseLong(Vault.getUserDataLocalOrDefault(m.getId(), message.getGuild().getId(), Shop.CURRENCY_VAULT_NAME, "0"));
        
        try {
            if(success) {
                Vault.storeUserDataLocal(message.getAuthor().getId(), message.getGuild().getId(), Shop.CURRENCY_VAULT_NAME, String.valueOf(userMoney + amount));
                Vault.storeUserDataLocal(m.getId(), message.getGuild().getId(), Shop.CURRENCY_VAULT_NAME, String.valueOf(targetMoney - amount));
                eb.addField("Steal", "You have stolen UB$" + amount + " from " + m.getName() + ".", false);
                eb.setColor(Color.GREEN);
            } else {
                Vault.storeUserDataLocal(message.getAuthor().getId(), message.getGuild().getId(), Shop.CURRENCY_VAULT_NAME, String.valueOf(userMoney - amount));
                Vault.storeUserDataLocal(m.getId(), message.getGuild().getId(), Shop.CURRENCY_VAULT_NAME, String.valueOf(targetMoney + amount));
                eb.addField("Steal", "You tried to steal UB$" + amount + " from " + m.getName() + ", but you lost it to them instead!", false);
                eb.setColor(Color.RED);
            }
            setRateLimiter(message.getAuthor().getId(), message.getGuild().getId());
        } catch(Throwable t) {
            eb.addField("Error", "There was a critical error using this command!!", false);
            eb.setColor(Color.RED);
            eb.addField("Please report this...", Arrays.toString(t.getStackTrace()), false);
        }
        return eb.build();
    }
    
    @Override
    public void onRegister() {
        CommandRegistrar.register("steal", this);
        Manual.setHelpPage("steal", "Attempt to steal UB$ from another user.\n" +
                                            "Usage: `steal <user @ | user ID>`\n");
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
        String epochOldString = Vault.getUserDataLocal(userID, guildID, "steal.cooldown");
        
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
        Vault.storeUserDataLocal(userID, guildID, "steal.cooldown", String.valueOf(Instant.now().getEpochSecond()));
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
        String a = Vault.getUserDataLocal(userID, guildID, "steal.cooldown");
        return a == null || a.isEmpty() ? 0.0 : Double.parseDouble(a) + 0.0;
    }
}
