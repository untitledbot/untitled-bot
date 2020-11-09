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
        MusicKernel.INSTANCE.setLast(message.getGuild().getId(), message.getTextChannel());
        EmbedBuilder eb = new EmbedBuilder();
        EmbedDefaults.setEmbedDefaults(eb, message);
        
        if(MusicKernel.INSTANCE.isPaused(message.getGuild())) {
            MusicKernel.INSTANCE.pause(message.getGuild(), false);
            eb.addField("untitled-bot", "\u23EF The player has been unpaused.\n" +
                    "Use the `pause` command to pause it again.", false);
            eb.setColor(Color.GREEN);
            return eb.build();
        }
        
        MusicKernel.INSTANCE.pause(message.getGuild(), true);
        eb.addField("untitled-bot", "\u23EF The player has been paused.\n" +
                "Use the `resume` command to unpause it.", false);
        eb.setColor(Color.GREEN);
        return eb.build();
    }
    
    @Override
    public void onRegister() {
        CommandRegistrar.register("pause", this);
        Manual.setHelpPage("pause", "Pause the currently playing track.");
        CommandRegistrar.registerAlias("pause", "ps", "unpause", "resume", "r");
    }
}
