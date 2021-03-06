package dev.alexisok.untitledbot.modules.rank.xpcommands;

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
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author AlexIsOK
 * @since 1.3
 */
public final class Daily extends UBPlugin {
    
    private static final long DAILY_MIN = 200L;
    private static final long DAILY_MAX = 500L;
    
    @Override
    public void onRegister() {
        CommandRegistrar.register("daily", this);
        Manual.setHelpPage("daily",
                String.format("Get your daily UB$ reward from the bot.%nUsage: `daily`%nRange: %d-%d UB$.", DAILY_MIN, DAILY_MAX));
    }
    
    @Override
    public @NotNull MessageEmbed onCommand(String[] args, @NotNull Message message) {
        EmbedBuilder eb = new EmbedBuilder();
        EmbedDefaults.setEmbedDefaults(eb, message);
        
        if(isRateLimit(message.getAuthor().getId(), message.getGuild().getId())) {
            eb.addField(
                    "Daily",
                    String.format("You have already claimed your daily reward!%nTry again in %.2f hours.",
                        rateLimitTimeHours(message.getAuthor().getId(), message.getGuild().getId())
                    ),
                    false
            );
            eb.setColor(Color.RED);
            return eb.build();
        }
        
        long xpToBeGranted = ThreadLocalRandom.current().nextLong(DAILY_MIN, DAILY_MAX);
        
        eb.addField("Daily UB$", String.format("You have been given UB$%d for your daily reward!", xpToBeGranted), false);
        eb.setColor(Color.GREEN);
        
        setRateLimiter(message.getAuthor().getId(), message.getGuild().getId());
        
        String cur = Vault.getUserDataLocal(message.getAuthor().getId(),
                message.getGuild().getId(),
                Shop.CURRENCY_VAULT_NAME);
    
        if(cur == null) cur = "0";
        
        long totalMoney = Long.parseLong(cur);
        
        Vault.storeUserDataLocal(message.getAuthor().getId(),
                message.getGuild().getId(),
                Shop.CURRENCY_VAULT_NAME,
                String.format("%d", + totalMoney + xpToBeGranted) 
        );
        
        return eb.build();
        
    }
    
    private static boolean isRateLimit(String userID, String guildID) {
        String epochOldString = Vault.getUserDataLocal(userID, guildID, "daily.cooldown");
        
        if(epochOldString == null) return false;
        
        long epochPrevious = Long.parseLong(epochOldString);
        long epochCurrent  = Instant.now().getEpochSecond();
        
        return epochCurrent - epochPrevious <= 86400;
    }
    
    private static void setRateLimiter(String userID, String guildID) {
        Vault.storeUserDataLocal(userID, guildID, "daily.cooldown", String.valueOf(Instant.now().getEpochSecond()));
    }
    
    
    private static double rateLimitTimeHours(String userID, String guildID) {
        return ((86400.0 - (Instant.now().getEpochSecond() - rateLimitTime(userID, guildID))) / 60.0) / 60.0;
    }
    
    /**
     * Get the time for the user rate-limit in unix-epoch.
     * returns 0 if the data is null or empty.
     * @return the time left for rate-limit in seconds
     */
    private static double rateLimitTime(String userID, String guildID) {
        String a = Vault.getUserDataLocal(userID, guildID, "daily.cooldown");
        return a == null || a.isEmpty() ? 0.0 : Double.parseDouble(a) + 0.0;
    }
}
