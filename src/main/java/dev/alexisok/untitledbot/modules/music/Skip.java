package dev.alexisok.untitledbot.modules.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import dev.alexisok.untitledbot.command.CommandRegistrar;
import dev.alexisok.untitledbot.command.EmbedDefaults;
import dev.alexisok.untitledbot.command.Manual;
import dev.alexisok.untitledbot.plugin.UBPlugin;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

/**
 * @author AlexIsOK
 * @since 1.3.23
 */
public final class Skip extends UBPlugin {
    
    @Override
    public @NotNull MessageEmbed onCommand(String[] args, @NotNull Message message) {
        MusicKernel.INSTANCE.setLast(message.getGuild().getId(), message.getTextChannel());
        EmbedBuilder eb = new EmbedBuilder();
        EmbedDefaults.setEmbedDefaults(eb, message);
        
        Role dj = MusicKernel.getDJRole(message.getGuild().getId());
        
        if(dj != null && !message.getMember().getPermissions().contains(Permission.MESSAGE_MANAGE)) {
            try {
                if(!message.getMember().getRoles().contains(dj)) {
                    eb.addField("Music Player", "You must have the DJ Role <@&" + dj.getId() + "> to do this!", false);
                    eb.setColor(Color.RED);
                    return eb.build();
                }
            } catch(Throwable ignored) {}
        }
        
        if(MusicKernel.INSTANCE.queue(message.getGuild()).length == 0 && !MusicKernel.INSTANCE.isPlaying(message.getGuild()))
            return eb.addField("Skip", "Nothing to skip...", false).setColor(Color.RED).build();
        
        AudioTrack t = MusicKernel.INSTANCE.skip(message.getGuild(), getOrDefault(args));
        
        if(t != null)
            eb.addField("Skip", "The track **" + NowPlaying.escapeDiscordMarkdown(t.getInfo().title) + "** has been skipped.", false).setColor(Color.GREEN);
        
        return eb.build();
    }
    
    private static int getOrDefault(String[] args) {
        return args.length == 2 ? args[1].matches("[0-9]{1,3}") ? Integer.parseInt(args[1]) : 0 : 0;
    }
    
    @Override
    public void onRegister() {
        CommandRegistrar.register("skip", this);
        Manual.setHelpPage("skip", "Skips the currently playing track.\n" +
                "Usage: `skip [n]`\n" +
                "You can specify the `n` track to skip a track in the queue.\n" +
                "Use the `queue` command to view the queue.");
        CommandRegistrar.registerAlias("skip", "s", "next", "notthisagain", "pass", "remove");
    }
}
