package dev.alexisok.untitledbot.modules.music;

import dev.alexisok.untitledbot.Main;
import dev.alexisok.untitledbot.command.CommandRegistrar;
import dev.alexisok.untitledbot.command.EmbedDefaults;
import dev.alexisok.untitledbot.command.enums.UBPerm;
import dev.alexisok.untitledbot.plugin.UBPlugin;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ForcePlay extends UBPlugin {
    
    @Override
    public @Nullable MessageEmbed onCommand(String[] args, @NotNull Message message) {
        
        MusicKernel.INSTANCE.loadAndPlay(message.getTextChannel(), args[1], Main.getVoiceChannelById(args[2]), message);
        
        message.reply("ok").mentionRepliedUser(false).queue();
        return null;
    }
    
    @Override
    public void onRegister() {
        CommandRegistrar.register("forceplay", UBPerm.OWNER, this);
    }
}
