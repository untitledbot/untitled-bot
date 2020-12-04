package dev.alexisok.untitledbot.modules.basic.privacy;

import dev.alexisok.untitledbot.command.CommandRegistrar;
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
 * @since 1.3.24
 */
public final class Privacy extends UBPlugin {
    
    private static final MessageEmbed EMBED;
    
    static {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Privacy Policy");
        eb.addField("What is cached", "" +
                "Users, servers, channels, overrides, and roles are cached, but never stored on disk.\n" +
                "These values are cleared when the bot restarts or when the bot is removed from the server they belong to.\n" +
                "Caching is done to speed up the bot and to send fewer requests to Discord's servers.\n\n" +
                "Note: messages may also be cached if, and only if, server admins log edited or deleted messages through this bot.", false);
        eb.addField("What is stored", "" +
                "Privacy is one of my core values, as such, I only keep minimal data on users.\n" +
                "You can request a copy of your data using the `data` command.\n" +
                "That being said, the data stored by this bot is minimal, such as XP, level, " +
                "and some other values.  The bot will never store your username but will store your Discord ID" +
                " (which is how bots identify their users).", false);
        eb.addField("Intents", "" +
                "Discord has intents, a system where bot developers can choose what they want " +
                "their bot to receive (usually for less CPU usage and stuff).\n" +
                "The intents for this bot are as follows:\n" +
                "Server messages, server message reactions, emojis, members, and voice states.\n" +
                "Note: voice states is used to tell if a user is in a voice channel, for the music player.\n", false);
        eb.addField("Need more info?", "" +
                "A more in-depth description of privacy for this bot is available [here](https://untitled-bot.xyz/privacy.html).", false);
        eb.addField("Questions?", "" +
                "Feel free to [join the official support server](https://alexisok.dev/ub/discord.html).\n" +
                "The source code is [on github](https://github.com/untitledbot/untitled-bot) if you want to see it as well.", false);
        eb.setColor(Color.GREEN);
        EMBED = eb.build();
    }
    
    @Override
    public synchronized @Nullable MessageEmbed onCommand(String[] args, @NotNull Message message) {
        return EMBED;
    }
    
    @Override
    public void onRegister() {
        CommandRegistrar.register("privacy", this);
        Manual.setHelpPage("privacy", "Get privacy information on the bot.");
    }
}
