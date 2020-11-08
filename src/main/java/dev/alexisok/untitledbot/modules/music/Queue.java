package dev.alexisok.untitledbot.modules.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import dev.alexisok.untitledbot.command.CommandRegistrar;
import dev.alexisok.untitledbot.command.EmbedDefaults;
import dev.alexisok.untitledbot.command.Manual;
import dev.alexisok.untitledbot.plugin.UBPlugin;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

/**
 * @author AlexIsOK
 * @since 1.3.23
 */
public final class Queue extends UBPlugin {
    
    @Override
    public @NotNull MessageEmbed onCommand(String[] args, @NotNull Message message) {
        EmbedBuilder eb = new EmbedBuilder();
        EmbedDefaults.setEmbedDefaults(eb, message);
        
        StringBuilder queue = new StringBuilder();
        int i = 0;
        for(AudioTrack t : MusicKernel.INSTANCE.queue(message.getGuild())) {
            i++;
            String format = escapeDiscordMarkdown(i + ": " + t.getInfo().title + "\n");
            if((queue + format).length() > 2048)
                break;
            queue.append(format);
        }
        
        eb.setTitle("Queue");
        eb.setDescription(queue.toString());
        eb.setColor(Color.GREEN);
        return eb.build();
    }
    
    private static String escapeDiscordMarkdown(@NotNull String toEscape) {
        toEscape = toEscape.replaceAll("\\*", "\\*");
        toEscape = toEscape.replaceAll("_", "\\_");
        toEscape = toEscape.replaceAll("\\|", "\\|");
        toEscape = toEscape.replaceAll("\\[", "\\[");
        toEscape = toEscape.replaceAll("]", "\\]");
        toEscape = toEscape.replaceAll("\\(", "\\(");
        toEscape = toEscape.replaceAll("\\)", "\\)");
        return toEscape.replaceAll("~", "\\~");
    }
    
    @Override
    public void onRegister() {
        CommandRegistrar.register("queue", this);
        Manual.setHelpPage("queue", "Get the queue for this server.");
        CommandRegistrar.registerAlias("queue", "queueueueueueueueueueue", "q", "kew");
    }
}
