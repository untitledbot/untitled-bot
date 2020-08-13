package dev.alexisok.untitledbot.modules.moderation.logging;

import dev.alexisok.untitledbot.command.Command;
import dev.alexisok.untitledbot.command.EmbedDefaults;
import dev.alexisok.untitledbot.modules.vault.Vault;
import dev.alexisok.untitledbot.plugin.UBPlugin;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

/**
 * Return a list of current log types to the user.
 *
 * @see AddRemoveLogTypes
 * @see SetLogChannel
 * @see dev.alexisok.untitledbot.modules.moderation.ModHook
 * @see LogTypes
 * @author AlexIsOK
 * @since 0.0.1
 */
public final class GetLogTypes extends UBPlugin implements Command {
    
    @Override
    public @NotNull MessageEmbed onCommand(String[] args, @NotNull Message message) {
        //args will be ignored...
    
        EmbedBuilder eb = new EmbedBuilder();
        EmbedDefaults.setEmbedDefaults(eb, message);
        
        String s = Vault.getUserDataLocal(null, message.getGuild().getId(), "log.policies");
    
        if(s == null || s.length() == 0) {
            eb.addField("Logging", "This server doesn't appear to log anything through this bot, though " +
                                           "other bots could be logging things.", false);
            eb.setColor(Color.YELLOW);
            
            return eb.build();
        }
        
        eb.setColor(Color.GREEN);
        eb.addField(
                "Logging",
                "Current policies for this guild:\n" + s
                                                              .replace(",", "\n")
                                                              .replace("_", " ")
                                                              .toLowerCase(),
                false);
        
        return eb.build();
    }
}
