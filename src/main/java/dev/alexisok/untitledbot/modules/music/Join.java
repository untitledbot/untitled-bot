package dev.alexisok.untitledbot.modules.music;

import dev.alexisok.untitledbot.Main;
import dev.alexisok.untitledbot.command.CommandRegistrar;
import dev.alexisok.untitledbot.command.EmbedDefaults;
import dev.alexisok.untitledbot.command.Manual;
import dev.alexisok.untitledbot.plugin.UBPlugin;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.VoiceChannel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.Objects;

/**
 * @author AlexIsOK
 * @since 1.3.23
 */
public final class Join extends UBPlugin {
    
    @Override
    public @NotNull MessageEmbed onCommand(String[] args, @NotNull Message message) {
        MusicKernel.INSTANCE.setLast(message.getGuild().getId(), message.getTextChannel());
        EmbedBuilder eb = new EmbedBuilder();
        EmbedDefaults.setEmbedDefaults(eb, message);
        for(VoiceChannel vc : message.getGuild().getVoiceChannels()) {
            if(!Objects.requireNonNull(message.getGuild().getMemberById(Main.jda.getSelfUser().getId())).hasPermission(vc, Permission.VOICE_CONNECT))
                continue;
            if(vc.getMembers().contains(message.getMember())) {
                if(!Objects.requireNonNull(message.getGuild().getMemberById(Main.jda.getSelfUser().getId())).hasPermission(vc, Permission.VOICE_CONNECT))
                    break;
                MusicKernel.INSTANCE.join(vc);
                eb.addField("Music Player", "I have joined the voice channel " + vc.getName() + ".\n" +
                        "" + (MusicKernel.INSTANCE.isPaused(message.getGuild()) ? "Note: the player is paused!" : ""), false);
                eb.setColor(Color.GREEN);
                return eb.build();
            }
        }
        eb.addField("Music Player", "I couldn't find a voice channel to join, do I have access to it?", false);
        eb.setColor(Color.RED);
        return eb.build();
    }
    
    @Override
    public void onRegister() {
        CommandRegistrar.register("join", this);
        Manual.setHelpPage("join", "Join the voice channel.");
    }
}
