package dev.alexisok.untitledbot.modules.music;

import dev.alexisok.untitledbot.command.CommandRegistrar;
import dev.alexisok.untitledbot.plugin.UBPlugin;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

/**
 * @author AlexIsOK
 * @since 1.3.23
 */
public final class Volume extends UBPlugin {
    
    private static final MessageEmbed EMBED = new EmbedBuilder()
            .setTitle("Volume")
            .setDescription("To set the volume for the bot, please manually adjust the volume on my slider for Discord.")
            .setColor(Color.RED)
            .build();
    
    @Override
    public @NotNull MessageEmbed onCommand(String[] args, @NotNull Message message) {
        return EMBED;
    }
    
    @Override
    public void onRegister() {
        CommandRegistrar.register("volume", this);
        CommandRegistrar.registerAlias("volume", "vol", "v");
    }
}
