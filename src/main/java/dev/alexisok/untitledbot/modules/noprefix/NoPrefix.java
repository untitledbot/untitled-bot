package dev.alexisok.untitledbot.modules.noprefix;

import dev.alexisok.untitledbot.BotClass;
import dev.alexisok.untitledbot.command.CommandRegistrar;
import dev.alexisok.untitledbot.command.EmbedDefaults;
import dev.alexisok.untitledbot.command.Manual;
import dev.alexisok.untitledbot.plugin.UBPlugin;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

/**
 * Allows the user to enter the NoPrefix:TM: mode.
 * 
 * Note: NoPrefix isn't really trademarked.
 * 
 * @author AlexIsOK
 * @since 1.4.0
 */
public final class NoPrefix extends UBPlugin {
    
    @NotNull
    @Override
    @Contract(pure = true)
    public MessageEmbed onCommand(@NotNull String[] args, @NotNull Message message) {
        EmbedBuilder eb = new EmbedBuilder();
        EmbedDefaults.setEmbedDefaults(eb, message);
        
        if(BotClass.addToNoPrefix(message.getAuthor().getId())) {
            eb.addField("NoPrefix:TM:", "You were added to the no-prefix mode!\nType `exit` at any time to leave this mode :)", false);
            eb.setColor(Color.GREEN);
        } else {
            eb.addField("NoPrefix:TM:", "You seem to already be in the no-prefix mode...\nType `exit` to exit this mode.", false);
            eb.setColor(Color.RED);
        }
        return eb.build();
        
    }
    
    @Override
    public void onRegister() {
        CommandRegistrar.register("no-prefix", this);
        Manual.setHelpPage("no-prefix", "Enter the no-prefix mode to get access to bot commands without " +
                                                "having to use the prefix.  Type `exit` at anytime to get out of this mode.");
    }
}
