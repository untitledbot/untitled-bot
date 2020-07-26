package dev.alexisok.untitledbot.modules.moderation;

import dev.alexisok.untitledbot.Main;
import dev.alexisok.untitledbot.command.CommandRegistrar;
import dev.alexisok.untitledbot.command.Manual;
import dev.alexisok.untitledbot.modules.moderation.logging.*;
import dev.alexisok.untitledbot.modules.moderation.modcommands.Ban;
import dev.alexisok.untitledbot.modules.moderation.modcommands.Pardon;
import dev.alexisok.untitledbot.modules.vault.Vault;
import net.dv8tion.jda.api.*;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.*;
import net.dv8tion.jda.api.events.guild.member.*;
import net.dv8tion.jda.api.events.guild.member.update.GuildMemberUpdateNicknameEvent;
import net.dv8tion.jda.api.events.guild.voice.*;
import net.dv8tion.jda.api.events.message.MessageUpdateEvent;
import net.dv8tion.jda.api.events.role.*;
import net.dv8tion.jda.api.events.role.update.*;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.Arrays;

/**
 * Class that hooks on to the {@link dev.alexisok.untitledbot.BotClass#onGenericEvent(GenericEvent)}
 * method to get things like message delete or message change.
 * 
 * @author AlexIsOK
 * @since 0.0.1
 */
public final class ModHook extends ListenerAdapter {
    
    @Override
    public void onReady(@NotNull ReadyEvent re) {
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
    
    /**
     * Check if the guild allows this type of logging
     * 
     * @param guildID the guild id
     * @param lt the log type
     * @return true if it does or false otherwise
     */
    private static boolean ch(String guildID, LogTypes lt) {
        if(Vault.getUserDataLocal(null, guildID, "log.channel") == null || 
           Vault.getUserDataLocal(null, guildID, "log.channel").equals("null"))
            return false;
        
        try {
            String[] policies = Vault.getUserDataLocal(null, guildID, "log.policies").split(",");
            for(String s : policies) {
                if(LogTypes.valueOf(s).equals(lt))
                    return true;
            }
            return false;
        } catch(NullPointerException | IllegalArgumentException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    
    /**
     * Get the log channel from the guild ID
     * @param guildID the guild ID
     * @see net.dv8tion.jda.api.JDA#getTextChannelById(String)
     * @return the log channel or {@code null} if there is none.
     */
    private static TextChannel lc(String guildID) {
        
        String tcID = Vault.getUserDataLocal(null, guildID, "log.channel");
        
        return Main.jda.getTextChannelById(tcID);
    }
    
    @Override
    public void onGuildMemberUpdateNickname(@Nonnull GuildMemberUpdateNicknameEvent e) {
        
        String guildID = e.getGuild().getId();
        
        if(!ch(guildID, LogTypes.USER_NICKNAME_UPDATE))
            return;
        
        EmbedBuilder eb = new EmbedBuilder();
        
        eb.addField("Logger", "User nickname changed.\n" +
                                      "User: <@" + e.getMember().getId() + ">\n\n" +
                                      "Old nickname: " + e.getOldNickname() + "\n" +
                                      "New nickname: " + e.getNewNickname() + "\n", false);
        eb.setColor(Color.YELLOW);
        lc(guildID).sendMessage(eb.build()).queue();
    }
    
    @Override
    public void onGuildMemberRoleAdd(@Nonnull GuildMemberRoleAddEvent e) {
        
        String guildID = e.getGuild().getId();
        
        if(!ch(guildID, LogTypes.USER_ROLE_UPDATE))
            return;
        
        EmbedBuilder eb = new EmbedBuilder();
        
        String[] roleIDs = new String[e.getRoles().size()];
        
        for(int i = 0; i < e.getRoles().size(); i++)
            roleIDs[i] = "<@&" + e.getRoles().get(i).getId() + ">";
        
        eb.addField("Logger", "User roles added.\n" +
                                      "Applied to user <@" + e.getMember().getId() + ">\n" +
                                      "Roles added:\n" +
                                      Arrays.toString(roleIDs), false);
        eb.setColor(Color.GREEN);
        lc(guildID).sendMessage(eb.build()).queue();
    }
    
    @Override
    public void onGuildMemberRoleRemove(@Nonnull GuildMemberRoleRemoveEvent e) {
        String guildID = e.getGuild().getId();
    
        if(!ch(guildID, LogTypes.USER_ROLE_UPDATE))
            return;
    
        EmbedBuilder eb = new EmbedBuilder();
    
        String[] roleIDs = new String[e.getRoles().size()];
    
        for(int i = 0; i < e.getRoles().size(); i++)
            roleIDs[i] = "<@&" + e.getRoles().get(i).getId() + ">";
    
        eb.addField("Logger", "User roles removed.\n" +
                                      "Applied to user <@" + e.getMember().getId() + ">\n" +
                                      "Roles removed:\n" +
                                      Arrays.toString(roleIDs), false);
        eb.setColor(Color.RED);
        lc(guildID).sendMessage(eb.build()).queue();
    }
    
    @Override
    public void onRoleCreate(@Nonnull RoleCreateEvent e) {
        String guildID = e.getGuild().getId();
        
        if(!ch(guildID, LogTypes.ROLE_ADD))
            return;
        
        EmbedBuilder eb = new EmbedBuilder();
        
        String roleCreated = e.getRole().getId();
        
        eb.addField("Logger", "Guild role created.\n" +
                                      "Role: <@&" + roleCreated + ">", false);
        eb.setColor(Color.GREEN);
        lc(guildID).sendMessage(eb.build()).queue();
    }
    
    @Override
    public void onRoleDelete(@Nonnull RoleDeleteEvent e) {
        String guildID = e.getGuild().getId();
    
        if(!ch(guildID, LogTypes.ROLE_DELETE))
            return;
    
        EmbedBuilder eb = new EmbedBuilder();
    
        String roleCreated = e.getRole().getId();
    
        eb.addField("Logger", "Guild role deleted.\n" +
                                      "Role: <@&" + roleCreated + ">", false);
        eb.setColor(Color.RED);
        lc(guildID).sendMessage(eb.build()).queue();
    }
    
    @Override
    public void onRoleUpdateMentionable(@Nonnull RoleUpdateMentionableEvent e) {
        String guildID = e.getGuild().getId();
        
        if(!ch(guildID, LogTypes.ROLE_UPDATE))
            return;
        
        EmbedBuilder eb = new EmbedBuilder();
        
        eb.addField("Logger", "Guild role updated.\n" +
                           "Role: <@&" + e.getRole().getId() + ">\n" +
                                      "Old mentionable state: " + e.getOldValue() + "\n" +
                                      "New mentionable state: " + e.getNewValue() + "\n", false);
        eb.setColor(Color.YELLOW);
        lc(guildID).sendMessage(eb.build()).queue();
    }
    
    @Override
    public void onRoleUpdateName(@Nonnull RoleUpdateNameEvent e) {
        String guildID = e.getGuild().getId();
    
        if(!ch(guildID, LogTypes.ROLE_UPDATE))
            return;
    
        EmbedBuilder eb = new EmbedBuilder();
    
        eb.addField("Logger", "Guild role updated.\n" +
                                      "Role: <@&" + e.getRole().getId() + ">\n" +
                                      "Old name: " + e.getOldValue() + "\n" +
                                      "New name: " + e.getNewValue() + "\n", false);
        eb.setColor(Color.YELLOW);
        lc(guildID).sendMessage(eb.build()).queue();
    }
    
    @Override
    public void onRoleUpdatePermissions(@Nonnull RoleUpdatePermissionsEvent e) {
        String guildID = e.getGuild().getId();
        
        if(!ch(guildID, LogTypes.ROLE_UPDATE))
            return;
        
        EmbedBuilder eb = new EmbedBuilder();
        
        String[] oldValues = new String[e.getOldPermissions().size()];
        String[] newValues = new String[e.getNewPermissions().size()];
        int i = 0;
    
        for(Permission p : e.getOldPermissions()) {
            oldValues[i] = p.getName();
            i++;
        }
        
        i = 0;
    
        for(Permission p : e.getNewPermissions()) {
            newValues[i] = p.getName();
            i++;
        }
        
        eb.addField("Logger", "Guild role updated.\n" +
                                      "Role: <@&" + e.getRole().getId() + ">\n" +
                                      "Old permissions: " + String.join(", ", oldValues) + "\n" +
                                      "New permissions: " + String.join(", ", newValues), false);
        eb.setColor(Color.YELLOW);
        lc(guildID).sendMessage(eb.build()).queue();
    }
    
    @Override
    public void onGuildVoiceJoin(@Nonnull GuildVoiceJoinEvent e) {
        String guildID = e.getGuild().getId();
        
        if(!ch(guildID, LogTypes.VOICE_CHANNEL_JOIN))
            return;
        
        EmbedBuilder eb = new EmbedBuilder();
        
        eb.addField("Logger", "User joined voice channel.\n" +
                                      "User: <@" + e.getMember().getId() + ">\n" +
                                      "Channel: " + e.getNewValue().getName(), false);
        eb.setColor(Color.GREEN);
        lc(guildID).sendMessage(eb.build()).queue();
    }
    
    @Override
    public void onGuildVoiceMove(@Nonnull GuildVoiceMoveEvent e) {
        String guildID = e.getGuild().getId();
    
        if(!ch(guildID, LogTypes.VOICE_CHANNEL_SWITCH))
            return;
    
        EmbedBuilder eb = new EmbedBuilder();
    
        eb.addField("Logger", "User moved channels.\n" +
                                      "User: <@" + e.getMember().getId() + ">\n" +
                                      "Old channel: " + e.getOldValue().getName() + "\n" +
                                      "New channel: " + e.getNewValue().getName() + "\n", false);
        eb.setColor(Color.YELLOW);
        lc(guildID).sendMessage(eb.build()).queue();
    }
    
    @Override
    public void onGuildVoiceLeave(@Nonnull GuildVoiceLeaveEvent e) {
        String guildID = e.getGuild().getId();
    
        if(!ch(guildID, LogTypes.VOICE_CHANNEL_LEAVE))
            return;
    
        EmbedBuilder eb = new EmbedBuilder();
    
        eb.addField("Logger", "User left voice channel.\n" +
                                      "User: <@" + e.getMember().getId() + ">\n" +
                                      "Channel: " + e.getChannelLeft().getName(), false);
        eb.setColor(Color.GREEN);
        lc(guildID).sendMessage(eb.build()).queue();
    }
    
    @Override
    public void onGuildMemberJoin(@Nonnull GuildMemberJoinEvent e) {
        String guildID = e.getGuild().getId();
        
        if(!ch(guildID, LogTypes.GUILD_MEMBER_JOIN))
            return;
        
        EmbedBuilder eb = new EmbedBuilder();
        
        eb.addField("Logger", "User joined guild.\n" +
                                      "User: <@" + e.getUser().getId() + ">\n", false);
        
        eb.setColor(Color.GREEN);
        lc(guildID).sendMessage(eb.build()).queue();
    }
    
    @Override
    public void onGuildMemberRemove(@Nonnull GuildMemberRemoveEvent e) {
        String guildID = e.getGuild().getId();
        
        if(!ch(guildID, LogTypes.GUILD_MEMBER_LEAVE))
            return;
        
        EmbedBuilder eb = new EmbedBuilder();
        
        eb.addField("Logger", "User left guild.\n" +
                                      "User: <@" + e.getUser().getId() + ">", false);
        
        eb.setColor(Color.RED);
        lc(guildID).sendMessage(eb.build()).queue();
    }
    
}