package dev.alexisok.untitledbot.modules.alexflipnote.api.filter;

import dev.alexisok.untitledbot.command.CommandRegistrar;
import dev.alexisok.untitledbot.command.EmbedDefaults;
import dev.alexisok.untitledbot.command.Manual;
import dev.alexisok.untitledbot.modules.alexflipnote.api.DoImageThing;
import dev.alexisok.untitledbot.plugin.UBPlugin;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jetbrains.annotations.NotNull;

/**
 * @author AlexIsOK
 * @since 1.3.23
 */
public final class Sepia extends UBPlugin {
    @Override
    public synchronized @NotNull MessageEmbed onCommand(String[] args, @NotNull Message message) {
        EmbedBuilder eb = new EmbedBuilder();
        EmbedDefaults.setEmbedDefaults(eb, message);

        return DoImageThing.generateImage("/filter/sepia?image=%s", eb, message, args);
    }

    @Override
    public void onRegister() {
        CommandRegistrar.register("sepia", this);
        Manual.setHelpPage("sepia", "Run an image through a sepia filter.\n" +
                "API: https://api.alexflipnote.dev/\n" +
                "Usage: `sepia <image, @user, or blank for your avatar>`");
    }
}
