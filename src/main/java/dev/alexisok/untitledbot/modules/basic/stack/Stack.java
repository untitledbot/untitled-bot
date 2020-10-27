package dev.alexisok.untitledbot.modules.basic.stack;

import dev.alexisok.untitledbot.command.CommandRegistrar;
import dev.alexisok.untitledbot.command.EmbedDefaults;
import dev.alexisok.untitledbot.command.Manual;
import dev.alexisok.untitledbot.plugin.UBPlugin;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

/**
 * @author AlexIsOK
 * @since 1.3.22
 */
public class Stack extends UBPlugin {
    
    @Override
    public @Nullable MessageEmbed onCommand(String[] args, @NotNull Message message) {
        EmbedBuilder eb = new EmbedBuilder();
        EmbedDefaults.setEmbedDefaults(eb, message);
        
        eb.setDescription(Arrays.toString(Arrays.stream(Thread.currentThread().getStackTrace()).toArray()));
        return eb.build();
    }

    @Override
    public void onRegister() {
        CommandRegistrar.register("stack", this);
        Manual.setHelpPage("stack", "Get the stack trace of the current thread.");
    }
}
