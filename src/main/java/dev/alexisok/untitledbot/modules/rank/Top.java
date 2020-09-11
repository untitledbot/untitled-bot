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
import org.jetbrains.annotations.Contract;
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
    public @Nullable MessageEmbed onCommand(String[] args, @NotNull Message message) {
        EmbedBuilder eb = new EmbedBuilder();
        EmbedDefaults.setEmbedDefaults(eb, message);
    
    
        if(isRateLimit(message.getGuild().getId())) {
            eb.setColor(Color.RED);
            eb.addField("Rate limited!", String.format("To reduce server load, this command can only be run every %d minutes.",
                    (int) (TIME_BETWEEN_COMMAND_IN_SECONDS / 60)),
                    false);
            return eb.build();
        }
        
        int amountToList = 10;
        boolean b = false; //i was going to name this something else but i spent five minutes just trying to figure out how to spell it and i couldn't so it named 'b' for now.
        if(args.length >= 2) {
            try {
                int tmp = Integer.parseInt(args[1]);
                if(tmp > 50)
                    b = true;
                amountToList = Math.min(tmp, 50);
            } catch(Exception ignored) {}
        }
        
        if(amountToList == 0) {
            setRateLimiter(message.getGuild().getId());
            eb.addField("Rank top", "Congratulations, you just got a leaderboard of ZERO PEOPLE.\n" +
                                            "Enjoy the five minute cooldown :)", false);
            eb.setColor(Color.PINK);
            return eb.build();
        } else if (amountToList < 0) {
            eb.addField("Rank top?", "you know what, i'm not even going to set a cooldown for this.\n" +
                                             "i'm not sure why you thought you could get a leaderboard of negative" +
                                             " people but it didn't work.  i think this bot is pretty much exploit proof :)", false);
            eb.setColor(new Color(154, 0, 255)); //purple i think
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
                
            } catch(Exception ignored) {}
        }
    
        eb2.addField("Rank top", "Fetching the top " + amountToList + " users in this guild...", false);
        
        topXP = sortHashMap(topXP);
        
        ArrayList<String> addStr = new ArrayList<>();
        
        int i = 0;
        for(Map.Entry<String, Long> a : topXP.entrySet()) {
            if(i >= amountToList)
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
        
        if(b) //b is the shortened variable in case you missed the last comment
            eb2.addField("Warning", "The list has been shortened to 50 members.", false);
        
        message.getChannel().sendMessage(eb2.build()).queue();
        
        return null;
    }
    
    @NotNull
    @Contract(pure = true)
    private static LinkedHashMap<String, Long> sortHashMap(@NotNull HashMap<String, Long> hm) {
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
