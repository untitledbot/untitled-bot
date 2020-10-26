package dev.alexisok.untitledbot.modules.basic.status;

import dev.alexisok.untitledbot.BotClass;
import dev.alexisok.untitledbot.Main;
import dev.alexisok.untitledbot.command.CommandRegistrar;
import dev.alexisok.untitledbot.command.EmbedDefaults;
import dev.alexisok.untitledbot.command.Manual;
import dev.alexisok.untitledbot.modules.basic.uptime.Uptime;
import dev.alexisok.untitledbot.modules.vault.Vault;
import dev.alexisok.untitledbot.plugin.UBPlugin;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.io.File;
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
                //not the best idea to put this here but might as well.
                //i've been seeing high memory usage, and i'm not sure if this is related or not.
                updateStatsString();
            }
        };
        new Timer().schedule(t, 0, 180000); //3 minutes is 180000 ms
    }
    
    private static void forceUpdate() {
        //not the best idea to put this here but might as well.
        //i've been seeing high memory usage, and i'm not sure if this is related or not.
        updateStatsString();
    }
    
    private static void updateStatsString() {
        Runtime.getRuntime().gc();
        long guilds = Main.jda.getGuilds().size();
        long roles  = Main.jda.getRoles().size();
        long users = Main.jda.getUsers().stream().filter(user -> !user.isBot()).count();
        long bots = Main.jda.getUsers().stream().filter(User::isBot).count();
        long texts  = Main.jda.getTextChannels().size();
        long voice  = Main.jda.getVoiceChannels().size();
        long usableSpace = new File(Main.DATA_PATH).getUsableSpace();
        long totalSpace  = new File(Main.DATA_PATH).getTotalSpace();
        String returnString = "```";
        returnString += "          Version: " + Main.VERSION + "\n";
        returnString += " Available memory: " + Runtime.getRuntime().freeMemory() / 1024 / 1024 + " MB\n";
        returnString += "     Total memory: " + Runtime.getRuntime().totalMemory() / 1024 / 1024 + " MB\n";
        returnString += "       Processors: " + Runtime.getRuntime().availableProcessors() + "\n";
        returnString += " Total disk space: " + (totalSpace / 1024 / 1024 / 1024) + " GB\n";
        returnString += "Usable disk space: " + (usableSpace / 1024 / 1024 / 1024) + " GB\n";
        returnString += "             Ping: " + Main.jda.getGatewayPing() + " ms\n";
        returnString += "           Uptime: " + Uptime.humanReadable() + "\n";
        returnString += "  Commands issued: " + CommandRegistrar.getTotalCommands() + "\n";
        returnString += "   Total messages: " + BotClass.getMessagesSentTotal() + "\n";
        returnString += "          Servers: " + guilds + "\n";
        returnString += "            Roles: " + roles +  "\n";
        returnString += "           Humans: " + users +  "\n";
        returnString += "             Bots: " + bots +   "\n";
        returnString += "    Text channels: " + texts +  "\n";
        returnString += "   Voice channels: " + voice +  "\n";
        returnString += "        UBPlugins: " + CommandRegistrar.registrarSize() + "\n";
        returnString += "```";
        returnString += "(note: this only updates once every three minutes).";
        
        stats = returnString;
    }
    
    @Override
    public @NotNull MessageEmbed onCommand(String[] args, @NotNull Message message) {
        EmbedBuilder eb = new EmbedBuilder();
        EmbedDefaults.setEmbedDefaults(eb, message);
        
        //force update if the owner is asking for the stats
        if(message.getAuthor().getId().equals(Main.OWNER_ID))
            forceUpdate();
        
        eb.setColor(Color.GREEN);
        eb.addField("Status", stats, true);
        return eb.build();
    }
    
    @Override
    public void onRegister() {
        CommandRegistrar.register("status", "core.stats", this);
        CommandRegistrar.registerAlias("status", "stats");
        Manual.setHelpPage("status", "Get the status and other statistics about the bot.");
    }
}
