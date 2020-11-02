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
 *  /calling?text=text
 * 
 * @author AlexIsOK
 * @since 1.3.23
 */
public final class TomCall extends UBPlugin {
    @Override
    public @NotNull MessageEmbed onCommand(String[] args, @NotNull Message message) {
        EmbedBuilder eb = new EmbedBuilder();
        EmbedDefaults.setEmbedDefaults(eb, message);
        
        if(args.length == 1) {
            eb.addField("Error", "Please provide text to be concerned about.", false);
            eb.setColor(Color.RED);
            return eb.build();
        }
        
        eb.setColor(Color.GREEN);
        args = Arrays.copyOfRange(args, 1, args.length);
        String text = String.join("%20", args).replace("\n", "");
        try {
            eb.setImage("https://api.alexflipnote.dev/calling?text=" + text);
        } catch(IllegalArgumentException ignored) {
            eb.addField("Error", String.format("You literally broke the bot into %d pieces.", Long.MAX_VALUE), false);
        }
        return eb.build();
    }

    @Override
    public void onRegister() {
        CommandRegistrar.register("call", this);
        Manual.setHelpPage("call", "Generate a 'Calling' meme.\n" +
                "API: https://api.alexflipnote.dev/\n" +
                "Usage: `call <text>`");
    }
}

