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
            .setDescription("To set the volume for the bot, please manually adjust the volume on my slider for Discord.\n\n" +
                    "The reason for this is that changing the volume from 100% causes heavy CPU usage on the server end.\n" +
                    "See [this link](https://github.com/sedmelluq/lavaplayer/issues/465) for more info.")
            .setColor(Color.RED)
            .setImage("https://media.discordapp.net/attachments/732614175523602552/774483063342104606/Screenshot_from_2020-11-06_20-16-12.png")
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
