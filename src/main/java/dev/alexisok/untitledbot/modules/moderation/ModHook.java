package dev.alexisok.untitledbot.modules.moderation;

import dev.alexisok.untitledbot.command.*;
import dev.alexisok.untitledbot.modules.moderation.logging.AddRemoveLogTypes;
import dev.alexisok.untitledbot.modules.moderation.logging.GetLogTypes;
import dev.alexisok.untitledbot.modules.moderation.logging.SetLogChannel;
import dev.alexisok.untitledbot.plugin.UBPlugin;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.Nullable;

/**
 * Class that hooks on to the {@link dev.alexisok.untitledbot.BotClass#onGenericEvent(GenericEvent)}
 * method to get things like message delete or message change.
 * 
 * @author AlexIsOK
 * @since 0.0.1
 */
public final class ModHook extends UBPlugin implements Command, MessageHook {
    
    @Override
    public @Nullable MessageEmbed onCommand(String[] args, Message message) {
        return null;
    }
    
    @Override
    public void onMessage(MessageReceivedEvent m) {}
    
    @Override
    public void onAnyEvent(GenericEvent e) {}
    
    @Override
    public void onRegister() {
        CommandRegistrar.register("log-channel", "admin", new SetLogChannel());
        CommandRegistrar.register("add-log", "admin", new AddRemoveLogTypes());
        CommandRegistrar.register("remove-log", "admin", new AddRemoveLogTypes());
        //this is not admin because moderators might want their users to be able to see what they log.
        CommandRegistrar.register("get-log", "logging.get", new GetLogTypes());
        
        Manual.setHelpPage("log-channel",
                "Move the logging channel to a specific channel.\n" +
                        "Usage: log-channel <channelName | null>\n" +
                        "Type the word 'null' to disable logging for the server altogether.\n" +
                        "Logs are stored in the logging channel only, the bot does not store information" +
                        " about the user on disk.");
        Manual.setHelpPage("add-log",
                "Add a specific logging type to the log channel.\n" +
                        "Usage: add-log <type>\n" +
                        "To remove a log, use the remove-log command.\n" +
                        "A list of all logs available is on the wiki https://github.com/alexisok/untitled-bot/wiki");
        Manual.setHelpPage("remove-log",
                "Remove a specific logging type to the log channel.\n" +
                        "Usage: remove-log <type>\n" +
                        "To add a log, use the add-log command.\n" +
                        "A list of all logs available is on the wiki https://github.com/alexisok/untitled-bot/wiki");
        Manual.setHelpPage("get-log", "Get the logging types for this guild.\n" +
                                              "Usage: get-log\n");
    }
}
