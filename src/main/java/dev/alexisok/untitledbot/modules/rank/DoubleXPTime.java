package dev.alexisok.untitledbot.modules.rank;

import dev.alexisok.untitledbot.Main;
import dev.alexisok.untitledbot.logging.Logger;
import dev.alexisok.untitledbot.modules.vault.Vault;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;

import java.io.File;
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
    
    private static boolean doubleXpTime = false;
    
    protected static int amount = 1;
    
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
        doubleXpTime = true;
        EmbedBuilder eb = new EmbedBuilder();
        
        amount = ThreadLocalRandom.current().nextInt(2, 8);
        
        eb.setTitle("untitled-bot");
        eb.addField("XP BOOST TIME", String.format("For the next hour, XP per message will be multiplied by %d!", amount), false);
    
        new Timer().schedule(
                new TimerTask() {
                    @Override
                    public void run() {
                        doubleXpTime = false;
                        amount = 1;
                    }
                }, 3600000
        );
        
        for(Guild g : Main.jda.getGuilds()) {
            try {
                String shouldBroadcast = Vault.getUserDataLocal(null, g.getId(), "ranks-broadcast.boost");
                if(shouldBroadcast != null) {
                    if(shouldBroadcast.equalsIgnoreCase("false"))
                        continue;
                }
                boolean found = false;
                for(TextChannel tc : g.getTextChannels()) {
                    if (tc.canTalk() && tc.getName().contains("bot")) {
                        found = true;
                        tc.sendMessage(eb.build()).queue();
                        break;
                    }
                }
    
                if (!found) {
                    if (g.getDefaultChannel().canTalk())
                        g.getDefaultChannel().sendMessage(eb.build()).queue();
                }
            } catch(Throwable t) {
                t.printStackTrace();
            }
        }
    }
    
}
