package dev.alexisok.untitledbot.modules.basic.reverse;

import dev.alexisok.untitledbot.command.CommandRegistrar;
import dev.alexisok.untitledbot.command.EmbedDefaults;
import dev.alexisok.untitledbot.command.Manual;
import dev.alexisok.untitledbot.plugin.UBPlugin;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.Color;
import java.util.Arrays;

/**
 * Reverses a given input string
 * 
 * @author AlexIsOK
 * @since 1.3.21
 */
public final class Reverse extends UBPlugin {
    
    @NotNull
    @Override
    @Contract(pure = true)
    public MessageEmbed onCommand(@NotNull String[] args, @NotNull Message message) {
        EmbedBuilder eb = new EmbedBuilder();
        EmbedDefaults.setEmbedDefaults(eb, message);
        if(args.length == 1) {
            eb.addField("Reverse String", "i literally cannot send an empty message, jda will give me an error and " +
                                                  "it will fill up my logs and then i will be mildly upset for about ten " +
                                                  "seconds or so... please actually input a string into here please thanks.", false);
            eb.setColor(Color.RED);
            return eb.build();
        }
        eb.addField("Reverse String",
                new StringBuilder(String.join(" ", Arrays.copyOfRange(args, 1, args.length)))
                        .reverse()
                        .toString()
                        .replace("<@", "<")
                        .replace("@everyone", "@ everyone") // :)
                        .replace("@here", "@ here")
                , false);
        eb.setColor(Color.GREEN);
        return eb.build();
    }
    
    @Override
    public void onRegister() {
        CommandRegistrar.register("reverse", this);
        Manual.setHelpPage("reverse", ".gnirts a esreveR");
    }
}
