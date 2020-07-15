package dev.alexisok.untitledbot.modules.cron;

import net.dv8tion.jda.api.events.guild.member.GuildMemberLeaveEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;

/**
 * Listens for events and sends them if the server wants it.
 * 
 * @see ScheduleSend
 * @see Events
 * @author AlexIsOK
 * @since 1.0.0
 */
public class Sender extends ListenerAdapter {
    
    @Override
    public void onGuildMemberRemove(@Nonnull GuildMemberRemoveEvent e) {
        
    }
}
