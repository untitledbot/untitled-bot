package dev.alexisok.untitledbot.modules.images.apiflipnote;

import dev.alexisok.untitledbot.command.CommandRegistrar;
import dev.alexisok.untitledbot.command.EmbedDefaults;
import dev.alexisok.untitledbot.command.Manual;
import dev.alexisok.untitledbot.plugin.UBPlugin;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jetbrains.annotations.NotNull;

/**
 * https://api.alexflipnote.dev/
 * 
 * https://api.alexflipnote.dev/amiajoke?image=
 * 
 * @author AlexIsOK
 * @since 1.3.23
 */
public final class AmIAJoke extends UBPlugin {
    
    @Override
    public synchronized @NotNull MessageEmbed onCommand(String[] args, @NotNull Message message) {
        EmbedBuilder eb = new EmbedBuilder();
        EmbedDefaults.setEmbedDefaults(eb, message);
        
        return DoImageThingFlip.generateImage("/amiajoke?image=%s", eb, message, args);
    }
    
    @Override
    public void onRegister() {
        CommandRegistrar.register("am-i-a-joke", this);
        Manual.setHelpPage("am-i-a-joke", "Generate an 'Am I A Joke' meme.\n" +
                "API: https://api.alexflipnote.dev/\n" +
                "Usage: `joke <image, @user, or blank for your avatar>`" +
                "\nYou can also use 1 to 20 `^` character(s) to get an image from X messages above.");
        CommandRegistrar.registerAlias("am-i-a-joke", "joke");
    }
}
