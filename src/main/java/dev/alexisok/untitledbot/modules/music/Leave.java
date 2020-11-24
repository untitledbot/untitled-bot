package dev.alexisok.untitledbot.modules.music;

import dev.alexisok.untitledbot.command.CommandRegistrar;
import dev.alexisok.untitledbot.command.EmbedDefaults;
import dev.alexisok.untitledbot.command.Manual;
import dev.alexisok.untitledbot.plugin.UBPlugin;
import net.dv8tion.jda.api.EmbedBuilder;
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
public class Leave extends UBPlugin {
    
    @Override
    public @Nullable MessageEmbed onCommand(String[] args, @NotNull Message message) {
        MusicKernel.INSTANCE.setLast(message.getGuild().getId(), message.getTextChannel());
        EmbedBuilder eb = new EmbedBuilder();
        EmbedDefaults.setEmbedDefaults(eb, message);
        for(VoiceChannel vc : message.getGuild().getVoiceChannels()) {
            if(vc.getMembers().contains(message.getMember())) {
                if(MusicKernel.INSTANCE.leave(vc)) {
                    eb.addField("Music Player", "I have left the voice channel " + vc.getName() + ".", false);
                    eb.setColor(Color.GREEN);
                } else {
                    eb.addField("Music Player", "I wasn't in a voice channel to leave.", false);
                    eb.setColor(Color.RED);
                }
                return eb.build();
            }
        }
        eb.addField("Music Player", "You must be in the same voice channel as me to do this!", false);
        eb.setColor(Color.RED);
        return eb.build();
    }
    
    @Override
    public void onRegister() {
        CommandRegistrar.register("leave", this);
        Manual.setHelpPage("leave", "Leave the voice channel.");
        CommandRegistrar.registerAlias("leave", "shoo");
    }
}
