package dev.alexisok.untitledbot.modules.rank;

import dev.alexisok.untitledbot.command.EmbedDefaults;
import dev.alexisok.untitledbot.modules.vault.Vault;
import dev.alexisok.untitledbot.plugin.UBPlugin;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author AlexIsOK
 * @since 1.3
 */
public class Top extends UBPlugin {
    
    private static final long TIME_BETWEEN_COMMAND_IN_SECONDS = 300; //300 seconds is 5 minutes
    
    @Override
    public @Nullable MessageEmbed onCommand(String[] args, Message message) {
        EmbedBuilder eb = new EmbedBuilder();
        EmbedDefaults.setEmbedDefaults(eb, message);
        
        if(isRateLimit(message.getGuild().getId())) {
            eb.setColor(Color.RED);
            eb.addField("Rate limited!", "To reduce server load, this command can only be run every 5 minutes.", false);
            return eb.build();
        }
        
        int cap = Math.min(message.getGuild().getMemberCount(), 10);
        
        if(cap == 1) {
            eb.setColor(Color.YELLOW);
            eb.addField("Rank top" , "Erm... there only seems to be one user in this guild with a rank...", false);
        }
        
        eb.addField("Rank top", "Fetching the top " + cap + " highest ranking users in this guild...", false);
    
        HashMap<String, String[]> content = new HashMap<>();
        
        for(Member m : message.getGuild().getMembers()) {
            String level = Vault.getUserDataLocal(m.getId(), message.getGuild().getId(), "ranks-level");
            String xp = Vault.getUserDataLocal(m.getId(), message.getGuild().getId(), "ranks-level");
            
            
        }
        
        for(Map.Entry<String, Integer> s : level.entrySet()) {
            
        }
        
    }
    
    /**
     * 
     * @param guildID the ID of the guild.
     * @return true if there is a rate limit, false otherwise.
     */
    private static boolean isRateLimit(String guildID) {
        String epochOldString = Vault.getUserDataLocal(null, guildID, "top.ratelimit");
        
        if(epochOldString == null) return false;
        
        long epochPrevious = Long.parseLong(epochOldString);
        long epochCurrent  = Instant.now().getEpochSecond();
    
        return epochCurrent - epochPrevious <= TIME_BETWEEN_COMMAND_IN_SECONDS;
    }
    
    private static void setRateLimiter(String guildID) {
        
    }
}
