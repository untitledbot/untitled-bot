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
        MusicKernel.INSTANCE.setLast(message.getGuild().getId(), message.getTextChannel());
        EmbedBuilder eb = new EmbedBuilder();
        EmbedDefaults.setEmbedDefaults(eb, message);
        
        int min = 0;
        int max = 20;
        
        //page of queue
        if(args.length == 2 && args[1].matches("^[123456789]")) {
            max = 20 * Integer.parseInt(args[1]) - 1;
            min = max - 20;
        }
        
        StringBuilder queue = new StringBuilder();
        int i = 0;
        for(AudioTrack t : MusicKernel.INSTANCE.queue(message.getGuild())) {
            if(i++ < min)
                continue;
            String format = escapeDiscordMarkdown(String.format("%d: %s\n", i, t.getInfo().title));
            if((queue + format).length() > 2048)
                break;
            if(i > max)
                break;
            queue.append(format);
        }
        
        if(i == 0) {
            eb.setTitle("Queue");
            eb.setDescription("Doesn't look like anything is queued.");
            eb.setColor(Color.RED);
            return eb.build();
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
        Manual.setHelpPage("queue", "Get the queue for this server.\n" +
                "Usage: `%squeue [page; def 1]`");
        CommandRegistrar.registerAlias("queue", "queueueueueueueueueueue", "q", "kew");
    }
}
