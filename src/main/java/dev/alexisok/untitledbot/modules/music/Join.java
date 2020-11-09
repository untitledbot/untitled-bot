package dev.alexisok.untitledbot.modules.music;

import dev.alexisok.untitledbot.Main;
import dev.alexisok.untitledbot.command.CommandRegistrar;
import dev.alexisok.untitledbot.command.Manual;
import dev.alexisok.untitledbot.plugin.UBPlugin;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.VoiceChannel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * @author AlexIsOK
 * @since 1.3.23
 */
public final class Join extends UBPlugin {
    
    @Override
    public @Nullable MessageEmbed onCommand(String[] args, @NotNull Message message) {
        for(VoiceChannel vc : message.getGuild().getVoiceChannels()) {
            if(!Objects.requireNonNull(message.getGuild().getMemberById(Main.jda.getSelfUser().getId())).hasPermission(vc, Permission.VOICE_CONNECT))
                break;
            if(vc.getMembers().contains(message.getMember())) {
                MusicKernel.INSTANCE.join(vc);
                return null;
            }
        }
    }
    
    @Override
    public void onRegister() {
        CommandRegistrar.register("join", this);
        Manual.setHelpPage("join", "Join the voice channel.");
    }
}
