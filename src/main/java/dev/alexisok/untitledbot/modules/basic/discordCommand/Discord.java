package dev.alexisok.untitledbot.modules.basic.discordCommand;

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
 * Responds to `discord` to get the invite link for the help server.
 * 
 * @author AlexIsOK
 * @since 1.3.21
 */
public final class Discord extends UBPlugin {
    
    
    @NotNull
    @Override
    @Contract(pure = true)
    public MessageEmbed onCommand(String[] args, @NotNull Message message) {
        EmbedBuilder eb = new EmbedBuilder();
        EmbedDefaults.setEmbedDefaults(eb, message);
    
        eb.addField("Discord Support Server",
                String.format("Hello!  If you need help with the bot, want to report a bug, " +
                                      "or just need someone to talk to, you can join [the untitled-bot " +
                                      "support server](%s) :)",
                        "https://alexisok.dev/ub/discord.html"), //DO NOT CHANGE TO XYZ SITE
                false);
        eb.setColor(Color.GREEN);
        return eb.build();
    }
    
    @Override
    public void onRegister() {
        CommandRegistrar.register("discord", this);
        Manual.setHelpPage("discord", "i mean i think it gets the discord support server but idk man why don't you try it?");
    }
}
