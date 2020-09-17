package dev.alexisok.untitledbot.modules.cron;

import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;


/**
 * Listens for events and sends them if the server wants it.
 * 
 * @see ScheduleSend
 * @author AlexIsOK
 * @since 1.0.0
 */
public final class Sender extends ListenerAdapter {
    
    static {
//        new Cron().onRegister();
    }
    
    @Override
    public void onGuildMemberRemove(@NotNull GuildMemberRemoveEvent e) {
        
    }
    
    @Override
    public void onGuildMemberJoin(@Nonnull GuildMemberJoinEvent event) {
        
    }
}
