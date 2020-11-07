package dev.alexisok.untitledbot.modules.music;

import dev.alexisok.untitledbot.command.CommandRegistrar;
import dev.alexisok.untitledbot.command.Manual;
import dev.alexisok.untitledbot.plugin.UBPlugin;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.VoiceChannel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
            if(vc.getMembers().contains(message.getMember())) {
                MusicKernel.INSTANCE.loadAndPlay(message.getTextChannel(), args[1], vc);
            }
        }
        return null;
    }
    
    @Override
    public void onRegister() {
        CommandRegistrar.register("play", this);
        Manual.setHelpPage("play", "Play a song from a URL or search YouTube.");
        CommandRegistrar.registerAlias("play");
    }
}
