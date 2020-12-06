package dev.alexisok.untitledbot.modules.basic.version;

import dev.alexisok.untitledbot.Main;
import dev.alexisok.untitledbot.command.CommandRegistrar;
import dev.alexisok.untitledbot.command.EmbedDefaults;
import dev.alexisok.untitledbot.command.Manual;
import dev.alexisok.untitledbot.plugin.UBPlugin;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

/**
 * Get the version of untitled-bot
 * 
 * @author AlexIsOK
 * @since 1.3.25
 */
public final class Version extends UBPlugin {
    
    @Override
    public @NotNull MessageEmbed onCommand(String[] args, @NotNull Message message) {
        EmbedBuilder eb = new EmbedBuilder();
        EmbedDefaults.setEmbedDefaults(eb, message);
        eb.addField("untitled-bot", String.format("Running untitled-bot version %s.", Main.VERSION), false);
        return eb.setColor(Color.GREEN).build();
    }
    
    @Override
    public void onRegister() {
        CommandRegistrar.register("version", this);
        Manual.setHelpPage("version", "Get the version of untitled-bot.");
    }
}
