package dev.alexisok.untitledbot.modules.rank;

import dev.alexisok.untitledbot.command.EmbedDefaults;
import dev.alexisok.untitledbot.modules.vault.Vault;
import dev.alexisok.untitledbot.plugin.UBPlugin;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.time.Instant;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Gets the users in the guild with the most XP.
 * 
 * @author AlexIsOK
 * @since 1.3
 */
public final class Top extends UBPlugin {
    
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
        
        eb.setColor(Color.GREEN);
        
        if(cap == 1) {
            eb.setColor(Color.YELLOW);
            eb.addField("Rank top" , "Erm... there only seems to be one user in this guild with a rank...", false);
        }
    
        LinkedHashMap<String, Long> topXP = new LinkedHashMap<>();
    
        for(Member m : message.getGuild().loadMembers().get()) {
            if(m.getUser().isBot())
                continue;
            
            long top = Ranks.totalXPFromAllLevels(m.getId(), message.getGuild().getId());
            if(top == 0)
                continue;
            
            topXP.put(m.getId(), top);
            
            if(topXP.size() >= 10)
                break;
        }
        
        eb.addField("Rank top", String.format("Fetching the top %d highest ranking users in this guild...", topXP.size()), false);
        
        topXP = sortHashMap(topXP);
        
        ArrayList<String> addStr = new ArrayList<>();
        
        int i = 0;
        for(Map.Entry<String, Long> a : topXP.entrySet()) {
            if(i >= 10)
                break;
            i++;
            addStr.add(String.format("<@%s> - %s XP (level %s)%n",
                    a.getKey(),
                    a.getValue(),
                    Vault.getUserDataLocal(a.getKey(), message.getGuild().getId(), "ranks-level")));
        }
        
        Collections.reverse(addStr);
        
        StringBuilder addStringReturn = new StringBuilder();
        
        for(String s : addStr) addStringReturn.append(s);
        
        setRateLimiter(message.getGuild().getId());
        
        
        eb.addField("===TOP RANKINGS===", addStringReturn.toString(), false);
        return eb.build();
    }
    
    private static @NotNull LinkedHashMap<String, Long> sortHashMap(@NotNull HashMap<String, Long> hm) {
        List<Map.Entry<String, Long>> list = new ArrayList<>(hm.entrySet());
        list.sort(Map.Entry.comparingByValue());
        
        return list.stream()
                            .collect(
                                Collectors.toMap(
                                    Map.Entry::getKey,
                                    Map.Entry::getValue,
                                    (a, b) -> b,
                                    LinkedHashMap::new
                            ));
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
        Vault.storeUserDataLocal(null, guildID, "top.ratelimit", String.valueOf(Instant.now().getEpochSecond()));
    }
}
