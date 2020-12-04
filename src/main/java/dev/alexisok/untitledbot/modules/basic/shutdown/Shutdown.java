package dev.alexisok.untitledbot.modules.basic.shutdown;

import dev.alexisok.untitledbot.BotClass;
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
 * @since 1.3.22
 */
public class Shutdown extends UBPlugin {
    
    @Nullable
    @Override
    public MessageEmbed onCommand(@NotNull String[] args, @NotNull Message message) {
        EmbedBuilder eb = new EmbedBuilder();
        EmbedDefaults.setEmbedDefaults(eb, message);
        if(!message.getAuthor().getId().equals("541763812676861952")) {
            eb.setTitle("Shutdown");
            eb.setDescription("To shutdown the bot, please [click here](https://alexisok.dev/verifyShutdown) to verify that you are a human.");
            return eb.build();
        } else {
            eb.addField("Running shutdown hooks.", "oops this is supposed to be the body not the title oops uhm anyways bye i guess", false);
            eb.setColor(Color.MAGENTA);
            message.getChannel().sendMessage(eb.build()).queue(r -> BotClass.onShutdown());
        }
        return null;
    }
    
    @Override
    public void onRegister() {
        CommandRegistrar.register("shutdown", this);
        Manual.setHelpPage("shutdown", "Shutdown the bot.  I trust you will only use this when needed.");
    }
}
