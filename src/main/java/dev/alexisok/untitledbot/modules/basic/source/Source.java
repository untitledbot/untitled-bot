package dev.alexisok.untitledbot.modules.basic.source;

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

import java.awt.*;

/**
 * Link to the source code
 * 
 * @author AlexIsOK
 * @since 1.4.0
 */
public final class Source extends UBPlugin {
    
    @NotNull
    @Override
    @Contract(pure = true)
    public MessageEmbed onCommand(String[] args, @NotNull Message message) {
        EmbedBuilder eb = new EmbedBuilder();
        EmbedDefaults.setEmbedDefaults(eb, message);
        
        eb.addField("Source", "Source code for the bot:\n" +
                                      "https://github.com/untitledbot/untitled-bot", false);
        eb.setColor(Color.GREEN);
        return eb.build();
    }
    
    @Override
    public void onRegister() {
        CommandRegistrar.register("source", this);
        Manual.setHelpPage("source", "Get the link to the source code of the bot.");
        CommandRegistrar.registerAlias("source", "github", "git");
    }
}
