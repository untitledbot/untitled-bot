package dev.alexisok.untitledbot.modules.rank.xpcommands;

import dev.alexisok.untitledbot.command.CommandRegistrar;
import dev.alexisok.untitledbot.command.EmbedDefaults;
import dev.alexisok.untitledbot.command.Manual;
import dev.alexisok.untitledbot.modules.rank.Ranks;
import dev.alexisok.untitledbot.modules.vault.Vault;
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
    
    private static final long DAILY_MIN = 400L;
    private static final long DAILY_MAX = 650L;
    
    @Override
    public void onRegister() {
        CommandRegistrar.register("daily", this);
        Manual.setHelpPage("daily",
                String.format("Get your daily XP reward from the bot.%nUsage: `daily`%nRange: %d-%d XP.", DAILY_MIN, DAILY_MAX));
    }
    
    @Override
    public @NotNull MessageEmbed onCommand(String[] args, @NotNull Message message) {
        EmbedBuilder eb = new EmbedBuilder();
        EmbedDefaults.setEmbedDefaults(eb, message);
        
        if(isRateLimit(message.getAuthor().getId(), message.getGuild().getId())) {
            eb.addField("Daily XP", "You have already claimed your daily reward!", false);
            eb.setColor(Color.RED);
            return eb.build();
        }
        
        long xpToBeGranted = ThreadLocalRandom.current().nextLong(DAILY_MIN, DAILY_MAX);
        
        eb.addField("Daily XP", String.format("You have been given %d XP for your daily reward!", xpToBeGranted), false);
        eb.setColor(Color.GREEN);
        
        setRateLimiter(message.getAuthor().getId(), message.getGuild().getId());
        
        Ranks.doLevelStuff(message, xpToBeGranted);
        
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
}
