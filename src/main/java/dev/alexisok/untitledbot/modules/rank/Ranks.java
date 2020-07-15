package dev.alexisok.untitledbot.modules.rank;

import dev.alexisok.untitledbot.command.CommandRegistrar;
import dev.alexisok.untitledbot.command.EmbedDefaults;
import dev.alexisok.untitledbot.command.Manual;
import dev.alexisok.untitledbot.command.MessageHook;
import dev.alexisok.untitledbot.modules.vault.Vault;
import dev.alexisok.untitledbot.plugin.UBPlugin;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author AlexIsOK
 * @since 0.0.1
 */
public class Ranks extends UBPlugin implements MessageHook {
    
    //0th element is level one
    private static final long[] XP_REQUIRED_FOR_LEVEL_UP = new long[]
                                                                   {50, 100, 125, 150, 200, 250, 400, 500, 700, 900, 1000,
                                                                   1250, 1500, 2000, 2500, 3000, 3500, 4000, 5000, 6000,
                                                                   7000, 8000, 9000, 10000, 11000, 12000, 13000, 14000,
                                                                   15000, 16000, 17000, 18000, 19000, 20000, 25000, 30000,
                                                                   35000, 40000, 45000, 50000, 55000, 60000, 65000, 70000,
                                                                   80000, 90000, 100000, 150000, 200000, 400000, 800000,
                                                                   1000000, 2000000, 3000000, 5000000, 8000000, 10000000};
    
    @Override
    public void onStartup() {
        super.onStartup();
    }
    
    @Override
    public void onRegister() {
        CommandRegistrar.registerHook(this);
        CommandRegistrar.register("rank", "core.ranks", this);
        Manual.setHelpPage("rank", "Get your (or another user's) rank.\nUsage: `rank [user @ | user ID]`");
        Vault.addDefault("ranks-xp", "0");
        Vault.addDefault("ranks-level", "1");
    }
    
    @Override
    public MessageEmbed onCommand(String[] args, @NotNull Message message) {
        
        EmbedBuilder eb = new EmbedBuilder();
        EmbedDefaults.setEmbedDefaults(eb, message);
        
        if(!message.isFromGuild()) {
            eb.setColor(Color.RED);
            eb.addField("Error", "You must run this in a guild!", false);
            return eb.build();
        }
        
        String xp;
        String lv;
        
        boolean other = false;
        
        try {
            User target = message.getMentionedMembers().get(0).getUser();
            xp = Vault.getUserDataLocal(target.getId(), message.getGuild().getId(), "ranks-xp");
            lv = Vault.getUserDataLocal(target.getId(), message.getGuild().getId(), "ranks-level");
            other = true;
        } catch(Exception ignored) {
            xp = Vault.getUserDataLocal(message.getAuthor().getId(), message.getGuild().getId(), "ranks-xp");
            lv = Vault.getUserDataLocal(message.getAuthor().getId(), message.getGuild().getId(), "ranks-level");
        }
        
        
        
        if (lv == null || xp == null) {
            eb.setColor(Color.RED);
            eb.addField("Error", "Looks like the level or XP is null..... this is awkward.....", false);
            return eb.build();
        }
    
        eb.setColor(Color.GREEN);
        if(!other) {
            try {
                eb.addField("Ranking",
                        "\n" +
                                "Level: " + lv + "\n" +
                                "Exp:   " + xp + "/" + XP_REQUIRED_FOR_LEVEL_UP[Integer.parseInt(lv) - 1] + "\n",
                        false);
            } catch(ArrayIndexOutOfBoundsException ignored) {
                //THIS SHOULD ONLY BE CAUGHT IF THE USER IS THE HIGHEST LEVEL
                eb.addField("Ranking",
                        "\n" +
                                "Level: " + lv + "\n" +
                                "Exp:   " + xp + "\n",
                        false);
            }
        } else {
            eb.addField("Ranking",
                    "Stats for this user:\n" + 
                            "Level: " + lv + "\n" + 
                            "Exp:   " + xp + "/" + XP_REQUIRED_FOR_LEVEL_UP[Integer.parseInt(lv) - 1] + "\n",
                    false);
        }
        return eb.build();
    }
    
    @Override
    public void onMessage(MessageReceivedEvent mre) {
        if(!mre.isFromGuild())
            return;
        Message m = mre.getMessage();
        if(!m.isFromGuild() || m.getAuthor().isBot())
            return;
        
        //get the xp and level of the user
        
        String xpstr = Vault.getUserDataLocal(m.getAuthor().getId(), m.getGuild().getId(), "ranks-xp");
        String lvstr = Vault.getUserDataLocal(m.getAuthor().getId(), m.getGuild().getId(), "ranks-level");
    
        if (lvstr == null || xpstr == null) {
            xpstr = "0";
            lvstr = "1";
        }
        
        long currentXP = Long.parseLong(xpstr);
        int currentLv = Integer.parseInt(lvstr);
        
        if(currentLv >= XP_REQUIRED_FOR_LEVEL_UP.length - 1)
            return;
        
        long randAdd = ThreadLocalRandom.current().nextLong(1, 4);
        
        currentXP += randAdd;
        
        //check level up
        if(currentXP >= XP_REQUIRED_FOR_LEVEL_UP[currentLv - 1]) {
            currentXP -= XP_REQUIRED_FOR_LEVEL_UP[currentLv - 1];
            currentLv++;
            
            mre
                    .getChannel()
                    .sendMessage("Congrats <@" + m.getAuthor().getId() + ">!  You are now level " + currentLv + "!!!")
                    .queue();
        }
    
        Vault.storeUserDataLocal(m.getAuthor().getId(), m.getGuild().getId(), "ranks-xp", String.valueOf(currentXP));
        Vault.storeUserDataLocal(m.getAuthor().getId(), m.getGuild().getId(), "ranks-level", String.valueOf(currentLv));
    }
    
    //unused
    @Override
    public void onAnyEvent(GenericEvent e) {}
}
