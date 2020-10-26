package dev.alexisok.untitledbot.command;

import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

/**
 * 
 * Plugins that use this interface are able to hook on to
 * the message received events directly.  This is used for
 * the Ranks module and can be used for many other plugins
 * as well.
 * 
 * @author AlexIsOK
 * @since 0.0.1
 */
public interface MessageHook {
    
    /**
     * Triggered when ANY message is sent that the bot can see.
     * @param m the mre
     */
    void onMessage(GuildMessageReceivedEvent m);
    
    /**
     * on literally any event i think
     * @param e the event
     */
    void onAnyEvent(GenericEvent e);
}
