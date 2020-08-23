package dev.alexisok.untitledbot.modules.cron;

import dev.alexisok.untitledbot.command.CommandRegistrar;
import dev.alexisok.untitledbot.command.EmbedDefaults;
import dev.alexisok.untitledbot.command.Manual;
import dev.alexisok.untitledbot.plugin.UBPlugin;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Send messages on events (timer events in 1.1.0)
 * 
 * @author AlexIsOK
 * @since 1.0.0
 */
public final class ScheduleSend extends UBPlugin {
    
    @Override
    public @Nullable MessageEmbed onCommand(String[] args, @NotNull Message message) {
        EmbedBuilder eb = new EmbedBuilder();
        EmbedDefaults.setEmbedDefaults(eb, message);
        if(args.length <= 2) {
            eb.addField("Scheduler", "Usage: schedule <event> <message...>", false);
            
        }
        
        return null;//TODO
    }
    
    @Override
    public void onRegister() {
        CommandRegistrar.register("schedule", "core.schedule", this);
        Manual.setHelpPage("schedule", "Schedule a message to send to a user when an event is triggered.\n" +
                                               "Usage: schedule <event> <message...>\n" +
                                               "See the GitHub Wiki page for more information.");
    }
}
