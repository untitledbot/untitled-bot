package dev.alexisok.untitledbot.modules.music;

import dev.alexisok.untitledbot.Main;
import dev.alexisok.untitledbot.command.CommandRegistrar;
import dev.alexisok.untitledbot.command.EmbedDefaults;
import dev.alexisok.untitledbot.command.Manual;
import dev.alexisok.untitledbot.plugin.UBPlugin;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.VoiceChannel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

/**
 * @author AlexIsOK
 * @since 1.3.23
 */
public class Pause extends UBPlugin {
    
    @Override
    public @Nullable MessageEmbed onCommand(String[] args, @NotNull Message message) {
        MusicKernel.INSTANCE.setLast(message.getGuild().getId(), message.getTextChannel());
        EmbedBuilder eb = new EmbedBuilder();
        EmbedDefaults.setEmbedDefaults(eb, message);
        
        boolean user = false;
        
        for(VoiceChannel vc : message.getGuild().getVoiceChannels()) {
            if(vc.getMembers().contains(message.getMember())) {
                //if the requester and this bot are in the same voice channel
                if(vc.getMembers().contains(message.getMember()) && vc.getMembers().contains(message.getGuild().getMemberById(Main.jda.getSelfUser().getId())))
                    user = true;
            }
        }
        
        if(!user) {
            eb.addField("Music Player", "You must be in the same voice channel as me to use this command!", false);
            eb.setColor(Color.RED);
            return eb.build();
        }
        
        if(!MusicKernel.INSTANCE.isPlaying(message.getGuild())) {
            eb.addField("Music Player", "I'm not playing anything pauseable", false);
            eb.setColor(Color.RED);
            return eb.build();
        }
        
        if(MusicKernel.INSTANCE.isPaused(message.getGuild())) {
            MusicKernel.INSTANCE.pause(message.getGuild(), false);
            eb.addField("Music Player", "\u25B6 The player has been unpaused.\n" +
                    "Use the `pause` command to pause it again.", false);
            eb.setColor(Color.GREEN);
            return eb.build();
        }
        
        MusicKernel.INSTANCE.pause(message.getGuild(), true);
        eb.addField("Music Bot", "\u23F8 The player has been paused.\n" +
                "Use the `resume` command to unpause it.", false);
        eb.setColor(Color.GREEN);
        return eb.build();
    }
    
    @Override
    public void onRegister() {
        CommandRegistrar.register("pause", this);
        Manual.setHelpPage("pause", "Pause the currently playing track.");
        CommandRegistrar.registerAlias("pause", "ps", "unpause", "resume", "r", "pa");
    }
}
