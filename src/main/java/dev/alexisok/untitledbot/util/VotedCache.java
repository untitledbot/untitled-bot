package dev.alexisok.untitledbot.util;

import dev.alexisok.untitledbot.BotClass;
import dev.alexisok.untitledbot.Main;
import dev.alexisok.untitledbot.command.CommandRegistrar;
import dev.alexisok.untitledbot.command.enums.UBPerm;
import dev.alexisok.untitledbot.util.hook.VoteHook;
import lombok.SneakyThrows;

import java.io.*;
import java.time.Instant;
import java.util.*;

/**
 * Caches whether or not a user has voted for the bot on top.gg.
 * 
 * @author AlexIsOK
 * @since 1.3.25
 */
public final class VotedCache implements VoteHook {
    
    private static final HashMap<Long, Long> VOTED = new HashMap<>();
    
    private static final List<Long> EXEMPT_GUILDS = new ArrayList<>();
    
    static {
        
        try {
            Properties p = new Properties();
            p.load(new FileReader("./exempt.properties"));
            p.forEach((o1, o2) -> EXEMPT_GUILDS.add(Long.valueOf(o1.toString())));
        } catch(IOException e) {
            e.printStackTrace();
        }
        
        TimerTask t = new TimerTask() {
            @Override
            public void run() {
                List<Long> toRemove = new ArrayList<>();
                VOTED.forEach((user, time) -> {
                    if(time <= Instant.now().getEpochSecond() - 41400L)
                        toRemove.add(user);
                });
                //to avoid concurrent errors
                toRemove.forEach(VOTED::remove);
            }
        };
        
        new Timer().schedule(t, 10000L, 60000L);
        
        CommandRegistrar.register("novote", UBPerm.OWNER, (args, message) -> {
            if(args.length == 1) {
                message.reply("https://cdn.discordapp.com/emojis/712506651520925698.png?v=1&size=64").mentionRepliedUser(false).queue();
                return null;
            }
            message.reply("adding guild " + args[1] + " to the list of vote-exempt guilds").mentionRepliedUser(false).queue();
            try {
                EXEMPT_GUILDS.add(Long.valueOf(args[1]));
                Properties p = new Properties();
                p.load(new FileInputStream("./exempt.properties"));
                p.setProperty(args[1], "0");
                p.store(new FileOutputStream("./exempt.properties"), "stored");
                message.reply("done").mentionRepliedUser(false).queue();
            } catch (Exception e) {
                message.reply("error " + e).mentionRepliedUser(false).queue();
            }
            return null;
        });
    }
    
    @SneakyThrows
    public static boolean hasVoted(long userID, long guildID) {
        if(EXEMPT_GUILDS.contains(guildID))
            return true;
        if(!VOTED.containsKey(userID))
            updateVotedCache(userID);
        
        return VOTED.containsKey(userID);
    }
    
    private static void updateVotedCache(long userID) {
        Main.API.hasVoted(String.valueOf(userID)).whenComplete((voted, whateverThisIs) -> {
            if(voted) {
                VOTED.put(userID, Instant.now().getEpochSecond());
            }
        });
    }
    
    @Override
    public void onVote(long userID) {
        VOTED.put(userID, Instant.now().getEpochSecond());
    }
    
    public static void init() {
        BotClass.registerVoteHook(new VotedCache());
    }
}
