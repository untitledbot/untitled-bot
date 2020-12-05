package dev.alexisok.untitledbot.modules.basic.prefix;

import dev.alexisok.untitledbot.BotClass;
import dev.alexisok.untitledbot.command.CommandRegistrar;
import dev.alexisok.untitledbot.command.EmbedDefaults;
import dev.alexisok.untitledbot.command.Manual;
import dev.alexisok.untitledbot.command.UBPerm;
import dev.alexisok.untitledbot.modules.vault.Vault;
import dev.alexisok.untitledbot.plugin.UBPlugin;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

/**
 * Sets the prefix for the guild
 * 
 * @author AlexIsOK
 * @since 1.3
 */
public final class Prefix extends UBPlugin {
    @Override
    public void onRegister() {
        CommandRegistrar.register("set-prefix", UBPerm.MANAGE_MESSAGES, this);
        CommandRegistrar.registerAlias("set-prefix", "prefix");
        Manual.setHelpPage("set-prefix", "Set the prefix for the bot.\n" +
                                                 "Usage: `set-prefix <prefix>`");
    }
    
    @Override
    public @NotNull MessageEmbed onCommand(@NotNull String[] args, @NotNull Message message) {
        EmbedBuilder eb = new EmbedBuilder();
        EmbedDefaults.setEmbedDefaults(eb, message);
        
        if(args.length == 1) {
            eb.setColor(Color.RED);
            eb.addField("Prefix", "Usage: `set-prefix <prefix>`", false);
            
            return eb.build();
        }
        
        String prefix = args[1];
        
        if(prefix.length() > 5 || prefix.length() < 1) {
            eb.setColor(Color.RED);
            eb.addField("Prefix", "Prefix must be one to five characters in length.", false);
            
            return eb.build();
        }
        
        if(!prefix.matches("[\\x21-\\x7E]{1,5}"))
            return eb.addField("Prefix", "Prefix cannot include special (unicode) characters or spaces.", false).setColor(Color.RED).build();
        
        Vault.storeUserDataLocal(null, message.getGuild().getId(), "guild.prefix", prefix);
        
        eb.setColor(Color.GREEN);
        eb.addField("Prefix", String.format("Prefix changed to `%s`, you can use " +
                "commands through `%scommand` or `%s command`.  You can also mention the bot to get the server prefix.",
                prefix, prefix, prefix), false);
        BotClass.updateGuildPrefix(message.getGuild().getId(), prefix);
        return eb.build();
    }
}
