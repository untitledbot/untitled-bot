package dev.alexisok.untitledbot.modules.music;

import dev.alexisok.untitledbot.command.CommandRegistrar;
import dev.alexisok.untitledbot.command.EmbedDefaults;
import dev.alexisok.untitledbot.command.Manual;
import dev.alexisok.untitledbot.plugin.UBPlugin;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
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
        EmbedBuilder eb = new EmbedBuilder();
        EmbedDefaults.setEmbedDefaults(eb, message);
        
        if(MusicKernel.INSTANCE.isPaused(message.getGuild())) {
            MusicKernel.INSTANCE.pause(message.getGuild(), false);
            eb.addField("Pause", "The player has been unpaused.", false);
            eb.setColor(Color.RED);
            return eb.build();
        }
        
        MusicKernel.INSTANCE.pause(message.getGuild(), true);
        eb.addField("Pause", "The player has been paused.", false);
        return eb.build();
    }
    
    @Override
    public void onRegister() {
        CommandRegistrar.register("pause", this);
        Manual.setHelpPage("pause", "Pause the currently playing track.");
        CommandRegistrar.registerAlias("ps");
    }
}
