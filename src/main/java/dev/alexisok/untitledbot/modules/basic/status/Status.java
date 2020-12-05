package dev.alexisok.untitledbot.modules.basic.status;

import dev.alexisok.untitledbot.BotClass;
import dev.alexisok.untitledbot.Main;
import dev.alexisok.untitledbot.command.CommandRegistrar;
import dev.alexisok.untitledbot.command.EmbedDefaults;
import dev.alexisok.untitledbot.command.Manual;
import dev.alexisok.untitledbot.modules.basic.uptime.Uptime;
import dev.alexisok.untitledbot.modules.music.MusicKernel;
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
    
    static {
        TimerTask t = new TimerTask() {
            @Override
            public void run() {
                updateStatsString();
            }
        };
        new Timer().schedule(t, 0, 180000); //3 minutes is 180000 ms
    }
    
    private static void updateStatsString() {
        Runtime.getRuntime().gc();
        
        
    }
    
    @Override
    public @NotNull MessageEmbed onCommand(String[] args, @NotNull Message message) {
        EmbedBuilder eb = new EmbedBuilder();
        EmbedDefaults.setEmbedDefaults(eb, message);
        
        long guilds = Main.getGuildCount();
        StringBuilder returnString = new StringBuilder("```\n");
        returnString.append(" Available memory: ").append(Runtime.getRuntime().freeMemory() / 1024 / 1024).append(" MB\n");
        returnString.append("     Total memory: ").append(Runtime.getRuntime().totalMemory() / 1024 / 1024).append(" MB\n");
        returnString.append("         Max ping: ").append(Main.getPingMax()).append(" ms\n");
        returnString.append("     Minimum ping: ").append(Main.getPingMin()).append(" ms\n");
        returnString.append("     Average ping: ").append(Main.getPingAverage()).append(" ms\n");
        returnString.append("  Commands issued: ").append(CommandRegistrar.getTotalCommands()).append("\n");
        returnString.append("   Total messages: ").append(BotClass.getMessagesSentTotal()).append("\n");
        returnString.append("          Servers: ").append(guilds).append("\n");
        returnString.append("  OpenJDK Version: ").append(System.getProperty("java.version")).append("\n");
        returnString.append("      JDA Version: 4.2.0_222\n");
        returnString.append("Commands (+alias): ").append(CommandRegistrar.registrarSize()).append("\n");
        returnString.append("    Music Players: ").append(MusicKernel.INSTANCE.getPlayers()).append("\n");
        returnString.append("            Shard: ").append(message.getJDA().getShardInfo().getShardId()).append("\n");
        returnString.append("     Total shards: ").append(Main.SHARD_COUNT).append("\n");
        returnString.append(" Servers on shard: ").append(message.getJDA().getGuilds().size()).append("\n");
        returnString.append("```");

        eb.setColor(Color.GREEN);
        eb.setTitle("Status");
        eb.setDescription(returnString);
        return eb.build();
    }
    
    @Override
    public void onRegister() {
        CommandRegistrar.register("status", this);
        CommandRegistrar.registerAlias("status", "stats");
        Manual.setHelpPage("status", "Get the status and other statistics about the bot.");
    }
}
