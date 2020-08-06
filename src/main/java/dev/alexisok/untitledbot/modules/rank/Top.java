package dev.alexisok.untitledbot.modules.rank;

import dev.alexisok.untitledbot.Main;
import dev.alexisok.untitledbot.command.EmbedDefaults;
import dev.alexisok.untitledbot.logging.Logger;
import dev.alexisok.untitledbot.modules.vault.Vault;
import dev.alexisok.untitledbot.plugin.UBPlugin;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.io.File;
import java.io.IOException;
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
    
    private static final long TIME_BETWEEN_COMMAND_IN_SECONDS = 300;
    
    @Override
    public @Nullable MessageEmbed onCommand(String[] args, Message message) {
        EmbedBuilder eb = new EmbedBuilder();
        EmbedDefaults.setEmbedDefaults(eb, message);
        
        if(isRateLimit(message.getGuild().getId())) {
            eb.setColor(Color.RED);
            eb.addField("Rate limited!", String.format("To reduce server load, this command can only be run every %d minutes.",
                    (int) (TIME_BETWEEN_COMMAND_IN_SECONDS / 60)), 
                    false);
            return eb.build();
        }
        
        LinkedHashMap<String, Long> topXP = new LinkedHashMap<>(new LinkedHashMap<>());
    
        EmbedBuilder eb2 = new EmbedBuilder();
        EmbedDefaults.setEmbedDefaults(eb2, message);
    
        for(File s : new File(Main.DATA_PATH + "/" + message.getGuild().getId() + "/").listFiles()) {
            try {
                
                long top = Ranks.totalXPFromAllLevels(s.getName().replace(".properties", ""), message.getGuild().getId());
                if (top == 0)
                    continue;
    
                topXP.put(s.getName().replace(".properties", ""), top);
                
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
    
        eb2.addField("Rank top", "Fetching the top highest ranking users in this guild...", false);
        
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
        
        StringBuilder addStringReturn = new StringBuilder();
        
        for(String s : addStr) addStringReturn.append(s);
        
        setRateLimiter(message.getGuild().getId());
        
        eb2.setColor(Color.GREEN);
        
        eb2.addField("===TOP RANKINGS===", addStringReturn.toString(), false);
        
        message.getChannel().sendMessage(eb2.build()).queue();
        
        return null;
    }
    
    private static @NotNull LinkedHashMap<String, Long> sortHashMap(@NotNull HashMap<String, Long> hm) {
        List<Map.Entry<String, Long>> list = new ArrayList<>(hm.entrySet());
        list.sort(Map.Entry.comparingByValue());
        Collections.reverse(list);
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
