package dev.alexisok.untitledbot.modules.moderation.modcommands;

import dev.alexisok.untitledbot.command.CommandRegistrar;
import dev.alexisok.untitledbot.command.EmbedDefaults;
import dev.alexisok.untitledbot.command.Manual;
import dev.alexisok.untitledbot.plugin.UBPlugin;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jetbrains.annotations.Nullable;

/**
 * un-ban a user
 * 
 * @see Ban
 * @author AlexIsOK
 * @since 0.0.1
 */
public class Pardon extends UBPlugin {
    
    @Override
    public @Nullable MessageEmbed onCommand(String[] args, Message message) {
        EmbedBuilder eb = new EmbedBuilder();
        EmbedDefaults.setEmbedDefaults(eb, message);
        
        if(args.length == 1) {
            eb.addField("Moderation", "Usage: pardon", false);
            //TODO this is for 1.1.0
        }
        return null;
    }
    
    @Override
    public void onRegister() {
        CommandRegistrar.register("pardon", "admin", this);
        CommandRegistrar.registerAlias("pardon", "un-ban");
        Manual.setHelpPage("pardon", "Pardon a user from banishment.");
    }
}
