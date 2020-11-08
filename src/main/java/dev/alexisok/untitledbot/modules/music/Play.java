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

/**
 * Plays music through the bot.
 * 
 * This might need some tlc with sharding and multi-threading.
 * 
 * @author AlexIsOK
 * @since 1.3.23
 */
public class Play extends UBPlugin {
    @Nullable
    @Override
    public MessageEmbed onCommand(String[] args, @NotNull Message message) {
        for(VoiceChannel vc : message.getGuild().getVoiceChannels()) {
            if(!message.getGuild().getMemberById(Main.jda.getSelfUser().getId()).hasPermission(vc, Permission.VOICE_CONNECT))
                break;
            if(vc.getMembers().contains(message.getMember())) {
                MusicKernel.INSTANCE.loadAndPlay(message.getTextChannel(), args[1], vc);
                return null;
            }
        }
        EmbedBuilder eb = new EmbedBuilder();
        EmbedDefaults.setEmbedDefaults(eb, message);
        eb.addField("Music Player", "Please join a voice channel to play music.\n" +
                "If you are already in a voice channel, make sure I have access to it.", false);
        eb.setColor(Color.RED);
        return eb.build();
    }
    
    @Override
    public void onRegister() {
        CommandRegistrar.register("play", this);
        Manual.setHelpPage("play", "Play a song from a YouTube URL (search coming soon!).");
        CommandRegistrar.registerAlias("play");
    }
}
