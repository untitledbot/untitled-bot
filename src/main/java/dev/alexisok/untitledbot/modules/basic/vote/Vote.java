package dev.alexisok.untitledbot.modules.basic.vote;

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
 * Implements the `vote` command.
 * 
 * @author AlexIsOK
 * @since 1.3.21
 */
public final class Vote extends UBPlugin {
    
    private static final String URL = "https://top.gg/bot/730135989863055472/vote";
    
    private static final String MESSAGE = String.format("You can [vote for untitled-bot on top.gg](%s).\n\n" +
                                                  "After that, you can use the `vr` or `vote-reward` command :)", URL);
    
    @NotNull
    @Override
    @Contract(pure = true)
    public MessageEmbed onCommand(@NotNull String[] args, @NotNull Message message) {
        EmbedBuilder eb = new EmbedBuilder();
        EmbedDefaults.setEmbedDefaults(eb, message);
        
        eb.addField("Vote for untitled-bot!", MESSAGE, false);
        eb.setColor(Color.GREEN);
        
        return eb.build();
    }
    
    @Override
    public void onRegister() {
//        CommandRegistrar.register("vote", this);
//        Manual.setHelpPage("vote", "Get the link to vote for the bot on Top.GG...");
    }
}
