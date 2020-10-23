package dev.alexisok.untitledbot.modules.basic.shutdown;

import dev.alexisok.untitledbot.command.CommandRegistrar;
import dev.alexisok.untitledbot.command.EmbedDefaults;
import dev.alexisok.untitledbot.command.Manual;
import dev.alexisok.untitledbot.plugin.UBPlugin;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jetbrains.annotations.NotNull;

/**
 * @author AlexIsOK
 * @since 1.3.22
 */
public class Shutdown extends UBPlugin {
    
    @NotNull
    @Override
    public MessageEmbed onCommand(@NotNull String[] args, @NotNull Message message) {
        EmbedBuilder eb = new EmbedBuilder();
        EmbedDefaults.setEmbedDefaults(eb, message);
        eb.setTitle("Shutdown");
        eb.setDescription("To shutdown the bot, please [click here](https://alexisok.dev/verifyShutdown.php) to verify that you are a human.");
        return eb.build();
    }
    
    @Override
    public void onRegister() {
        CommandRegistrar.register("shutdown", this);
        Manual.setHelpPage("shutdown", "Shutdown the bot.  I trust you will only use this when needed.");
    }
}
