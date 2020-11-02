package dev.alexisok.untitledbot.modules.apiuseless;

import dev.alexisok.untitledbot.command.CommandRegistrar;
import dev.alexisok.untitledbot.command.EmbedDefaults;
import dev.alexisok.untitledbot.command.Manual;
import dev.alexisok.untitledbot.plugin.UBPlugin;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jetbrains.annotations.NotNull;

/**
 * @author AlexIsOK
 * @since 1.3.23
 */
public final class Charcoal extends UBPlugin {
    @Override
    public synchronized @NotNull MessageEmbed onCommand(String[] args, @NotNull Message message) {
        EmbedBuilder eb = new EmbedBuilder();
        EmbedDefaults.setEmbedDefaults(eb, message);

        return DoImageThingUseless.generateImage("/charcoal?image=%s", eb, message, args);
    }

    @Override
    public void onRegister() {
        CommandRegistrar.register("charcoal", this);
        Manual.setHelpPage("charcoal", "Make an image look like charcoal\n" +
                "API: https://useless-api--vierofernando.repl.co/\n" +
                "Usage: `charcoal <image, @user, or blank for your avatar>`");
    }
}
