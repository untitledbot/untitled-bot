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
public final class Stop extends UBPlugin {

    @Override
    public @NotNull MessageEmbed onCommand(String[] args, @NotNull Message message) {
        
        EmbedBuilder eb = new EmbedBuilder();
        EmbedDefaults.setEmbedDefaults(eb, message);
        
        try {
            MusicKernel.INSTANCE.stop(message.getGuild());
        } catch(Throwable ignored) {
            eb.addField("Stop", "Could not stop the player.", false);
            eb.setColor(Color.RED);
            return eb.build();
        }
        
        eb.addField("Stop", "Player has been stopped and queue has been cleared.", false);
        eb.setColor(Color.GREEN);
        
        return eb.build();
    }

    @Override
    public void onRegister() {
        CommandRegistrar.register("stop", this);
        Manual.setHelpPage("stop", "Stop the currently playing song.");
        CommandRegistrar.registerAlias("stop", "die");
    }
}
