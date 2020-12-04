package dev.alexisok.untitledbot.modules.music;

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
 * Allows users to repeat the current track.
 * 
 * @author AlexIsOK
 * @since 1.3.24
 */
public final class Repeat extends UBPlugin {
    
    @Override
    public @NotNull MessageEmbed onCommand(String[] args, @NotNull Message message) {
        
        EmbedBuilder eb = new EmbedBuilder();
        EmbedDefaults.setEmbedDefaults(eb, message);
        
        if(!MusicKernel.INSTANCE.isPlaying(message.getGuild())) {
            eb.addField("Music Player", "There doesn't seem to be anything playing.....", false);
            eb.setColor(Color.RED);
            return eb.build();
        }
        
        boolean isRepeat = MusicKernel.INSTANCE.getRepeat(message.getGuild().getId());
        
        MusicKernel.INSTANCE.setRepeat(message.getGuild().getId(), !isRepeat);
        
        if(isRepeat) eb.addField("Music Player", "\uD83D\uDD01Music player will no longer repeat the current song.", false);
        else eb.addField("Music Player", "\uD83D\uDD01Music player will now repeat the current song.", false);
        
        eb.setColor(Color.GREEN);
        return eb.build();
    }
    
    @Override
    public void onRegister() {
        CommandRegistrar.register("repeat", this);
        Manual.setHelpPage("repeat", "Repeat the currently playing song.");
    }
}
