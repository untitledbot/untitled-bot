package dev.alexisok.untitledbot.modules.basic.status;

import dev.alexisok.untitledbot.BotClass;
import dev.alexisok.untitledbot.Main;
import dev.alexisok.untitledbot.command.CommandRegistrar;
import dev.alexisok.untitledbot.command.EmbedDefaults;
import dev.alexisok.untitledbot.command.Manual;
import dev.alexisok.untitledbot.plugin.UBPlugin;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.lang.management.ManagementFactory;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Get a bunch of information on the bot and the servers it is in.
 * 
 * @author AlexIsOK
 * @since 1.3
 */
public final class Status extends UBPlugin {
    
    private static String stats = "";
    
    static {
        TimerTask t = new TimerTask() {
            @Override
            public void run() {
                String returnString = "```";
                returnString += "          Version: " + Main.VERSION + "\n";
                returnString += "       JDA status: " + Main.jda.getStatus() + "\n";
                returnString += " Available memory: " + Runtime.getRuntime().freeMemory() / 1024 / 1024 + " MB\n";
                returnString += "     Total memory: " + Runtime.getRuntime().totalMemory() / 1024 / 1024 + " MB\n";
                returnString += "       Processors: " + Runtime.getRuntime().availableProcessors() + "\n";
                returnString += "           Uptime: " + ManagementFactory.getRuntimeMXBean().getUptime() + " ms\n";
                returnString += "  Commands issued: " + CommandRegistrar.getTotalCommands() + "\n";
                returnString += "   Total messages: " + BotClass.getMessagesSentTotal() + "\n";
                returnString += "           Guilds: " + Main.jda.getGuilds().size() + "\n";
                returnString += "            Roles: " + Main.jda.getRoles().size() + "\n";
                returnString += "            Users: " + Main.jda.getUsers().size() + "\n";
                returnString += "    Text channels: " + Main.jda.getTextChannels().size() + "\n";
                returnString += "   Voice channels: " + Main.jda.getVoiceChannels().size() + "\n";
                returnString += "          Plugins: " + CommandRegistrar.registrarSize() + "\n";
    
                returnString += "```";
                
                returnString += "(note: this only updates once every three minutes).";
    
                stats = returnString;
            }
        };
        new Timer().schedule(t, 0, 180000); //3 minutes is 180000 ms
    }
    
    @Override
    public @NotNull MessageEmbed onCommand(String[] args, @NotNull Message message) {
        EmbedBuilder eb = new EmbedBuilder();
        EmbedDefaults.setEmbedDefaults(eb, message);
    
        eb.setColor(Color.GREEN);
        eb.addField("Status", stats, true);
        return eb.build();
    }
    
    @Override
    public void onRegister() {
        CommandRegistrar.register("status", "core.stats", this);
        Manual.setHelpPage("status", "Get the status and other statistics about the bot.");
    }
}
