package dev.alexisok.untitledbot.modules.basic.economy;

import dev.alexisok.untitledbot.command.MessageHook;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

/**
 * Economy class that hooks on to the other class.
 * 
 * @author AlexIsOK
 * @since 1.3
 */
public final class EconomyHook implements MessageHook {
    
    @Override
    public void onMessage(MessageReceivedEvent m) {
        if(!m.isFromGuild())
            return;
        
        UpdateMoney.update(m.getMember().getId(), m.getGuild().getId());
    }
    
    
    //unused...
    @Override
    public void onAnyEvent(GenericEvent e) {}
}
