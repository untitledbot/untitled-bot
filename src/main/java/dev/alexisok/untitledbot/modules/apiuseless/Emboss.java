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
public class Emboss extends UBPlugin {
    
    @Override
    public synchronized @NotNull MessageEmbed onCommand(String[] args, @NotNull Message message) {
        EmbedBuilder eb = new EmbedBuilder();
        EmbedDefaults.setEmbedDefaults(eb, message);
        
        return DoImageThingUseless.generateImage("/emboss?image=%s", eb, message, args);
    }
    
    @Override
    public void onRegister() {
        CommandRegistrar.register("emboss", this);
        Manual.setHelpPage("emboss", "Emboss an image.\n" +
                "API: https://useless-api--vierofernando.repl.co/\n" +
                "Usage: `emboss <image, @user, or blank for your avatar>`" +
                "\nYou can also use 1 to 20 `^` character(s) to get an image from X messages above.");
    }
}
