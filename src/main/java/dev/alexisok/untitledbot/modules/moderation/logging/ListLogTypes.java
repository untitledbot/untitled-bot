package dev.alexisok.untitledbot.modules.moderation.logging;

import dev.alexisok.untitledbot.command.CommandRegistrar;
import dev.alexisok.untitledbot.command.EmbedDefaults;
import dev.alexisok.untitledbot.command.Manual;
import dev.alexisok.untitledbot.plugin.UBPlugin;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

/**
 * List available log types.
 * 
 * @author AlexIsOK
 * @since 1.3
 */
public class ListLogTypes extends UBPlugin {
    
    @Override
    public @Nullable MessageEmbed onCommand(String[] args, @NotNull Message message) {
        EmbedBuilder eb = new EmbedBuilder();
        EmbedDefaults.setEmbedDefaults(eb, message);
        
        eb.addField("Log types", Arrays.toString(LogTypes.values())
                                         .replace("_", "-")
                                         .replace("[", "")
                                         .replace("]", "")
                                         .replace(",", "\n")
                                         .toLowerCase(), false);
        return eb.build();
    }
    
    @Override
    public void onRegister() {
        CommandRegistrar.register("list-log-types", this);
        Manual.setHelpPage("list-log-types", "List ALL the log types.\n" +
                                                     "Usage: `list-log-types`");
        CommandRegistrar.registerAlias("list-log-types", "log-types");
    }
}
