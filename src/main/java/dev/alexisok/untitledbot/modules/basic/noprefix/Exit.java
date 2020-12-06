package dev.alexisok.untitledbot.modules.basic.noprefix;

import dev.alexisok.untitledbot.BotClass;
import dev.alexisok.untitledbot.command.CommandRegistrar;
import dev.alexisok.untitledbot.command.EmbedDefaults;
import dev.alexisok.untitledbot.command.Manual;
import dev.alexisok.untitledbot.logging.Logger;
import dev.alexisok.untitledbot.plugin.UBPlugin;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

/**
 * Exit the no-prefix mode.
 * 
 * @author AlexIsOK
 * @since 1.4.0
 */
public final class Exit extends UBPlugin {
    
    @NotNull
    @Override
    @Contract(pure = true)
    public MessageEmbed onCommand(String[] args, @NotNull Message message) {
        EmbedBuilder eb = new EmbedBuilder();
        EmbedDefaults.setEmbedDefaults(eb, message);
        Logger.debug("Removing a user from the NoPrefix:TM: mode.");
        try {
            if (BotClass.removeFromNoPrefix(message.getAuthor().getId())) {
                eb.addField("NoPrefix:TM:", "You have been removed from the no-prefix mode.\n" +
                                                    "The prefix for this server is `" + BotClass.getPrefix(message.getGuild().getId(), null) + "`", false);
                eb.setColor(Color.GREEN);
            } else {
                eb.addField("NoPrefix:TM:", "You don't seem to be in the no-prefix mode as of now...", false);
                eb.setColor(Color.RED);
            }
        } catch(Throwable ignored) {
            BotClass.removeFromNoPrefix(message.getAuthor().getId());
            eb.addField("Error!", "There was an error.  You have been removed from no-prefix mode (i think)", false);
        }
        return eb.build();
    }
    
    @Override
    public void onRegister() {
        CommandRegistrar.register("exit", this);
        Manual.setHelpPage("exit", "Exit the no-prefix mode.");
    }
}
