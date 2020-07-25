package dev.alexisok.untitledbot.modules.basic.status;

import dev.alexisok.untitledbot.BotClass;
import dev.alexisok.untitledbot.Main;
import dev.alexisok.untitledbot.command.CommandRegistrar;
import dev.alexisok.untitledbot.command.EmbedDefaults;
import dev.alexisok.untitledbot.plugin.UBPlugin;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.lang.management.ManagementFactory;

/**
 * Get a bunch of information on the bot and the servers it is in.
 * 
 * @author AlexIsOK
 * @since 1.3
 */
public final class Status extends UBPlugin {
    
    @Override
    public @NotNull MessageEmbed onCommand(String[] args, Message message) {
        EmbedBuilder eb = new EmbedBuilder();
        EmbedDefaults.setEmbedDefaults(eb, message);
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
        returnString += "       Categories: " + Main.jda.getCategories().size() + "\n";
        returnString += "           Emotes: " + Main.jda.getEmotes().size() + "\n";
        returnString += "    Shard current: " + message.getJDA().getShardInfo().getShardId() + "\n";
        returnString += "      Shard total: " + Main.jda.getShardInfo().getShardTotal() + "\n";
        returnString += "          Plugins: " + CommandRegistrar.registrarSize() + "\n";
    
        returnString += "```";
    
        eb.setColor(Color.GREEN);
        eb.addField("Status", returnString, true);
        return eb.build();
    }
    
    @Override
    public void onRegister() {
        CommandRegistrar.register("status", "core.stats", this);
    }
}
