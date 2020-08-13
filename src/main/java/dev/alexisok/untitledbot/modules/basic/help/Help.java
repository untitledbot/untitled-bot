package dev.alexisok.untitledbot.modules.basic.help;

import dev.alexisok.untitledbot.command.CommandRegistrar;
import dev.alexisok.untitledbot.command.EmbedDefaults;
import dev.alexisok.untitledbot.command.Manual;
import dev.alexisok.untitledbot.plugin.UBPlugin;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.Objects;

/**
 * Help command.
 * 
 * This needs some tlc.
 * 
 * @author AlexIsOK
 * @since 1.3
 */
public final class Help extends UBPlugin {
    
    @Override
    public void onRegister() {
        CommandRegistrar.register("help", "core.help", this);
        CommandRegistrar.registerAlias("help", "man", "halp");
    }
    
    @Override
    public @NotNull MessageEmbed onCommand(String[] args, @NotNull Message message) {
        EmbedBuilder eb = new EmbedBuilder();
        EmbedDefaults.setEmbedDefaults(eb, message);
        try {
            String returnString = Manual.getHelpPages(args[1]);
            String embedStr = returnString == null
                                      ? "Could not find the help page, did you make a typo?"
                                      : returnString;
            eb.setColor(returnString == null ? Color.RED : Color.GREEN);
            eb.addField("Help pages", embedStr, false);
            return eb.build();
        } catch(ArrayIndexOutOfBoundsException ignored) {
            return Objects.requireNonNull(CommandRegistrar.runCommand("commands", args, message));
        }
    }
}
