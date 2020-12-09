package dev.alexisok.untitledbot.modules.basic.tag;

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

/**
 * @author AlexIsOK
 * @since 1.3.23
 */
public class Tag extends UBPlugin {
    @Override
    public @Nullable MessageEmbed onCommand(String[] args, @NotNull Message message) {
        EmbedBuilder eb = new EmbedBuilder();
        EmbedDefaults.setEmbedDefaults(eb, message);
        if(args.length == 1) {
            eb.addField("Tag", "Usage: `tag <tag>`", false);
            eb.setColor(Color.RED);
            return eb.build();
        }
        
        if(args[1].startsWith("#"))
            args[1] = args[1].replaceFirst("#", "");
        
        if(!args[1].matches("[0-9]{4}"))
            return eb.addField("Tag", "Usage: `tag <tag>`", false).setColor(Color.RED).build();
        
        
        return null;
    }

    @Override
    public void onRegister() {
        CommandRegistrar.register("tag", this);
        Manual.setHelpPage("tag", "Get the amount of users in the current server with a specific tag.\n" +
                "Usage: `tag <#0000>`");
        CommandRegistrar.registerAlias("tag", "tags", "discriminator");
    }
}
