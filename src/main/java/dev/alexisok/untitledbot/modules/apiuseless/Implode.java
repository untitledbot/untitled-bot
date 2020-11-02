package dev.alexisok.untitledbot.modules.apiuseless;

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
public final class Implode extends UBPlugin {
    
    @Override
    public synchronized @NotNull MessageEmbed onCommand(String[] args, @NotNull Message message) {
        EmbedBuilder eb = new EmbedBuilder();
        EmbedDefaults.setEmbedDefaults(eb, message);

        return DoImageThingUseless.generateImage("/implode?amount=0.8image=%s", eb, message, args);
    }

    @Override
    public void onRegister() {
        CommandRegistrar.register("implode", this);
        Manual.setHelpPage("implode", "Implode an image\n" +
                "API: https://useless-api--vierofernando.repl.co/\n" +
                "Usage: `implode <image, @user, or blank for your avatar>`");
        CommandRegistrar.registerAlias("implode", "greg");
    }
}
