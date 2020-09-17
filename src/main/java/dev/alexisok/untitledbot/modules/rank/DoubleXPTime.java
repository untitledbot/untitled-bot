package dev.alexisok.untitledbot.modules.rank;

import dev.alexisok.untitledbot.Main;
import dev.alexisok.untitledbot.command.EmbedDefaults;
import dev.alexisok.untitledbot.logging.Logger;
import dev.alexisok.untitledbot.modules.vault.Vault;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;

import java.io.File;
import java.math.BigInteger;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 
 * Announce the double (or higher) XP time.  Also holds the boolean for double XP time.
 * 
 * @author AlexIsOK
 * @since 1.3
 */
public class DoubleXPTime {
    
    private static final String DISABLE_MESSAGE = "(Turn this message off with `rank-settings announce-xp-boost false`)";
    
    protected static int boostAmount = 1;
    
    protected static BigInteger totalXPFromBoost = new BigInteger("0");
    
    /**
     * Installs the broadcaster.
     */
    public static void installer() {
        TimerTask t = new TimerTask() {
            @Override
            public void run() {
                if(new File("xpboost.ub").exists()) {
                    Logger.log("Deleting xp boost file: " + new File("xpboost.ub").delete());
                    broadcast();
                }
            }
        };
        
        new Timer().schedule(t, 0, 100);
    }
    
    private static void broadcast() {
        EmbedBuilder eb = new EmbedBuilder();
        
        boostAmount = ThreadLocalRandom.current().nextInt(4, 16);
        
        eb.setTitle("untitled-bot");
        eb.addField("XP BOOST TIME", String.format("For the next hour, XP per message will be multiplied by %d!", boostAmount), false);
        eb.setFooter(DISABLE_MESSAGE);
        
        new Timer().schedule(
                new TimerTask() {
                    @Override
                    public void run() {
                        boostAmount = 1;
                        Logger.log("XP boost over!");
                        endDTBroadcast();
                    }
                }, 3600000
        ); //3600000 for hour
    
        thing(eb);
    }
    
    private static void thing(EmbedBuilder eb) {
        for(Guild g : Main.jda.getGuilds()) {
            try {
                
                //filter out guilds that have oped-out
                String shouldBroadcast = Vault.getUserDataLocal(null, g.getId(), "ranks-broadcast.boost");
                if(shouldBroadcast != null) {
                    if(shouldBroadcast.equalsIgnoreCase("false"))
                        continue;
                }
                
                //look for a bot channel
                boolean found = false;
                for(TextChannel tc : g.getTextChannels()) {
                    if (tc.canTalk() && tc.getName().contains("bot")) {
                        found = true;
                        tc.sendMessage(eb.build()).queue();
                        break;
                    }
                }
                
                //generic channel
                try {
                    if (!found) {
                        //default channel could be null
                        if (Objects.requireNonNull(g.getDefaultChannel()).canTalk())
                            g.getDefaultChannel().sendMessage(eb.build()).queue();
                    }
                } catch(NullPointerException ignored){} //in case default channel is null
            } catch(Throwable t) {
                t.printStackTrace();
            }
        }
    }
    
    private static void endDTBroadcast() {
        EmbedBuilder eb = new EmbedBuilder();
    
        eb.setTitle("untitled-bot");
        eb.addField("XP BOOST OVER", String.format("The XP boost is over!%n%s XP was obtained from all guilds " +
                                                           "during this boost!", totalXPFromBoost.toString()), false);
        eb.setFooter(DISABLE_MESSAGE);
        totalXPFromBoost = new BigInteger("0");
        
        thing(eb);
    }
    
}
