package dev.alexisok.untitledbot.modules.alexflipnote.api.filter;

import dev.alexisok.untitledbot.command.CommandRegistrar;
import dev.alexisok.untitledbot.command.EmbedDefaults;
import dev.alexisok.untitledbot.command.Manual;
import dev.alexisok.untitledbot.modules.alexflipnote.api.DoImageThingFlip;
import dev.alexisok.untitledbot.plugin.UBPlugin;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jetbrains.annotations.NotNull;

/**
 * @author AlexIsOK
 * @since 1.3.23
 */
public class JPEG extends UBPlugin {
    @Override
    public synchronized @NotNull MessageEmbed onCommand(String[] args, @NotNull Message message) {
        EmbedBuilder eb = new EmbedBuilder();
        EmbedDefaults.setEmbedDefaults(eb, message);

        return DoImageThingFlip.generateImage("/filter/jpegify?image=%s", eb, message, args);
    }

    @Override
    public void onRegister() {
        CommandRegistrar.register("jpeg", this);
        Manual.setHelpPage("jpeg", "Make an image higher quality\n" +
                "API: https://api.alexflipnote.dev/\n" +
                "Usage: `jpegify <image, @user, or blank for your avatar>`" +
                "\nYou can also use 1 to 20 `^` character(s) to get an image from X messages above.");
        CommandRegistrar.registerAlias("jpeg", "jpegify", "jpg");
    }
}
