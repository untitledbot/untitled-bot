package dev.alexisok.untitledbot.modules.alexflipnote.api;

import dev.alexisok.untitledbot.command.CommandRegistrar;
import dev.alexisok.untitledbot.command.EmbedDefaults;
import dev.alexisok.untitledbot.command.Manual;
import dev.alexisok.untitledbot.plugin.UBPlugin;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.Arrays;

/**
 * 
 * Max length is 61
 * 
 * @author AlexIsOK
 * @since 1.3.23
 */
public class Scroll extends UBPlugin {
    @Override
    public @NotNull MessageEmbed onCommand(String[] args, @NotNull Message message) {
        EmbedBuilder eb = new EmbedBuilder();
        EmbedDefaults.setEmbedDefaults(eb, message);

        if(args.length == 1) {
            eb.addField("Error", "Please provide text to find in the Scroll of Truth.", false);
            eb.setColor(Color.RED);
            return eb.build();
        }

        eb.setColor(Color.GREEN);
        args = Arrays.copyOfRange(args, 1, args.length);
        String text = String.join("%20", args).replace("\n", "");
        
        //the api explodes if /scroll's text is more than 61 characters so i'll do 60 just to be safe
        if(text.length() >= 60)
            text = text.substring(text.length() - 60);
        try {
            eb.setImage("https://api.alexflipnote.dev/scroll?text=" + text);
        } catch(IllegalArgumentException ignored) {
            eb.addField("Error", String.format("You literally broke the bot into %d pieces.", Long.MAX_VALUE), false);
        }
        return eb.build();
    }

    @Override
    public void onRegister() {
        CommandRegistrar.register("scroll", this);
        Manual.setHelpPage("scroll", "Generate a 'Scroll of Truth' meme.\n" +
                "API: https://api.alexflipnote.dev/\n" +
                "Usage: `scroll <text>`");
    }
}
