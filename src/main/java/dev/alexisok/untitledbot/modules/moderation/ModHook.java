package dev.alexisok.untitledbot.modules.moderation;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import dev.alexisok.untitledbot.Main;
import dev.alexisok.untitledbot.command.CommandRegistrar;
import dev.alexisok.untitledbot.command.Manual;
import dev.alexisok.untitledbot.command.enums.UBPerm;
import dev.alexisok.untitledbot.logging.Logger;
import dev.alexisok.untitledbot.modules.moderation.logging.*;
import dev.alexisok.untitledbot.util.vault.Vault;
import dev.alexisok.untitledbot.util.DateFormatUtil;
import net.dv8tion.jda.api.*;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.*;
import net.dv8tion.jda.api.events.guild.GuildBanEvent;
import net.dv8tion.jda.api.events.guild.GuildUnbanEvent;
import net.dv8tion.jda.api.events.guild.member.*;
import net.dv8tion.jda.api.events.guild.member.update.GuildMemberUpdateNicknameEvent;
import net.dv8tion.jda.api.events.guild.voice.*;
import net.dv8tion.jda.api.events.message.guild.GuildMessageDeleteEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageUpdateEvent;
import net.dv8tion.jda.api.events.role.*;
import net.dv8tion.jda.api.events.role.update.*;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.time.Instant;
import java.util.*;

import static dev.alexisok.untitledbot.modules.moderation.logging.LogTypes.MESSAGE_DELETE;
import static dev.alexisok.untitledbot.modules.moderation.logging.LogTypes.MESSAGE_UPDATE;

/**
 * Class that hooks on to the {@link dev.alexisok.untitledbot.BotClass#onGenericEvent(GenericEvent)}
 * method to get things like message delete or message change.
 * 
 * @author AlexIsOK
 * @since 0.0.1
 */
public final class ModHook extends ListenerAdapter {
    
    private static final HashMap<String, ArrayList<LogTypes>> LOG_CACHE = new HashMap<>();
    
    //message id, message
    private static final HashMap<String, Message> MESSAGE_CACHE = new HashMap<>();

    /**
     * Get the size of the message cache.
     * @return the size of the message cache
     */
    @Contract(pure = true)
    public static int getMessageCacheSize() {
        return MESSAGE_CACHE.size();
    }
    
    @Nullable
    @Contract(pure = true)
    public static Message getMessageByID(@NotNull String ID) {
        return MESSAGE_CACHE.get(ID);
    }

    static {
        CommandRegistrar.register("log-channel", UBPerm.ADMIN, new SetLogChannel());
        CommandRegistrar.register("add-log", UBPerm.ADMIN, new AddRemoveLogTypes());
        CommandRegistrar.register("remove-log", UBPerm.ADMIN, new AddRemoveLogTypes());
        //this is not admin because moderators might want their users to be able to see what they log.
        CommandRegistrar.register("get-log", new GetLogTypes());
        new ListLogTypes().onRegister();
        
        Manual.setHelpPage("log-channel",
                "Move the logging channel to a specific channel.\n" +
                        "Usage: log-channel <channelName | none>\n" +
                        "Type the word 'null' to disable logging for the server altogether.\n" +
                        "Logs are stored in the logging channel only, the bot does not store information" +
                        " about the user on disk.");
        Manual.setHelpPage("add-log",
                "Add a specific logging type to the log channel.\n" +
                        "Usage: add-log <type>\n" +
                        "To remove a log, use the remove-log command.\n" +
                        "A list of all logs can be found using the command `log-types`");
        Manual.setHelpPage("remove-log",
                "Remove a specific logging type to the log channel.\n" +
                        "Usage: remove-log <type>\n" +
                        "To add a log, use the add-log command.\n" +
                        "A list of all logs can be found using the command `log-types`");
        Manual.setHelpPage("get-log", "Get the logging types for this guild.\n" +
                                              "Usage: get-log\n");
        CommandRegistrar.registerAlias("get-log", "get-logs");
        
    }
    
    public static synchronized void resetCacheForGuild(@NotNull String guildID) {
        LOG_CACHE.remove(guildID);
    }
    
    /**
     * Check if the guild allows this type of logging
     * 
     * @param guildID the guild id
     * @param lt the log type
     * @return true if it does or false otherwise
     */
    private static synchronized boolean ch(String guildID, LogTypes lt) {
        try {
            if(LOG_CACHE.containsKey(guildID)) {
                return LOG_CACHE.get(guildID).contains(lt);
            }
            String channelID = Vault.getUserDataLocal(null, guildID, "log.channel");
            if(channelID == null || channelID.equals("null"))
                return false;
            
            TextChannel tc = null;
            
            try {
                tc = Objects.requireNonNull(Main.getJDAFromGuild(guildID)).getTextChannelById(channelID);
            } catch(NullPointerException ignored) {}
            
            if(tc == null) return false;
            
            String[] policies;
            
            try {
                policies = Vault.getUserDataLocal(null, guildID, "log.policies").split(",");
            } catch(NullPointerException | IndexOutOfBoundsException ignored) {} //if there are no policies.
            
            try {
                policies = Vault.getUserDataLocal(null, guildID, "log.policies").split(",");
            } catch(Exception ignored) {
                Logger.critical("Could not get the policies for " + guildID + "!");
                return false;
            }
            if(!LOG_CACHE.containsKey(guildID)) {
                LOG_CACHE.put(guildID, new ArrayList<>());
                for(String s : policies) {
                    LOG_CACHE.get(guildID).add(LogTypes.valueOf(s));
                }
            }
            
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
    
    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent e) {
        //webhook messages cause a lot of problems
        if(e.isWebhookMessage())
            return;
        if(!LOG_CACHE.containsKey(e.getGuild().getId()))
            return;
        if(LOG_CACHE.get(e.getGuild().getId()).contains(MESSAGE_DELETE) || LOG_CACHE.get(e.getGuild().getId()).contains(MESSAGE_UPDATE))
            MESSAGE_CACHE.put(e.getMessageId(), e.getMessage());
    }
    
    @Override
    public void onGuildMessageUpdate(@NotNull GuildMessageUpdateEvent e) {
        String guildID = e.getGuild().getId();
        if(!ch(guildID, LogTypes.MESSAGE_UPDATE))
            return;
        
        if(e.getMessage().getEmbeds().size() != 0)
            return;
        
        EmbedBuilder eb = new EmbedBuilder();
        
        eb.setTitle("Message updated.  Old content:");
        if(!MESSAGE_CACHE.containsKey(e.getMessageId())) {
            eb.setDescription(""); //nothing
        } else
            eb.setDescription(MESSAGE_CACHE.get(e.getMessageId()).getContentRaw());
        
        String[] content = new String[2];
        
        //to avoid npe
        Arrays.fill(content, "");
        
        if(e.getMessage().getContentRaw().length() > 1024) {
            Iterable<String> s = Splitter.fixedLength(1024).split(e.getMessage().getContentRaw());
            content = Iterables.toArray(s, String.class);
        } else {
            content[0] = e.getMessage().getContentRaw();
        }
        
        eb.addField("New content:", content[0], false);
        if(content[1].length() != 0)
            eb.addField("", content[1], false);
        
        eb.addField("Info:", String.format("" +
                "Original time sent: %s%n" +
                "Time edited: %s%n" +
                "Message ID: %s%n" +
                "Sent by: %s%n" +
                "Message channel: %s%n" +
                "[Link](%s)%n",
                e.getMessage().getTimeCreated(),
                e.getMessage().getTimeEdited(),
                e.getMessageId(),
                e.getAuthor().getAsMention(),
                e.getChannel().getAsMention(),
                e.getMessage().getJumpUrl()), false);
        MESSAGE_CACHE.put(e.getMessageId(), e.getMessage());
        eb.setColor(Color.YELLOW);
        eb.setTimestamp(Instant.now());
        lc(guildID).sendMessage(eb.build()).queue();
    }

    @Override
    public void onGuildMessageDelete(@NotNull GuildMessageDeleteEvent e) {
        String guildID = e.getGuild().getId();
        if(!ch(guildID, LogTypes.MESSAGE_UPDATE))
            return;
        
        Message deleted = MESSAGE_CACHE.get(e.getMessageId());
        
        EmbedBuilder eb = new EmbedBuilder();
        
        if(deleted == null) {
            eb.addField("Message deleted", "Old content not recoverable.", false);
            eb.setColor(Color.RED);
            eb.addField("Message info:", "" +
                    "Sent in: " + e.getChannel().getAsMention() + "\n" +
                    "ID: " + e.getMessageId(), false);
            
            lc(guildID).sendMessage(eb.build()).queue();
            return;
        }
        
        if(deleted.getEmbeds().size() != 0)
            return;
        
        eb.setTitle("Message deleted.  Old content:");
        eb.setDescription(deleted.getContentRaw());
        
        eb.addField("Info:", String.format("" +
                        "Original time sent: %s%n" +
                        "Time deleted: %s%n" +
                        "Message ID: %s%n" +
                        "Sent by: %s%n" +
                        "Message channel: %s%n",
                DateFormatUtil.format(deleted.getTimeCreated()),
                (new Date()),
                e.getMessageId(),
                deleted.getAuthor().getAsMention(),
                e.getChannel().getAsMention()), false);
        eb.setColor(Color.RED);
        eb.setTimestamp(Instant.now());
        lc(guildID).sendMessage(eb.build()).queue();
    }
    
//    //handle images and stuff
//        if(e.getMessage().getAttachments().size() != 0) {
//
//    }


    /**
     * Get the log channel from the guild ID
     * @param guildID the guild ID
     * @see net.dv8tion.jda.api.JDA#getTextChannelById(String)
     * @return the log channel or {@code null} if there is none.
     */
    private static TextChannel lc(String guildID) {
        
        String tcID = Vault.getUserDataLocal(null, guildID, "log.channel");
        
        return Main.getJDAFromGuild(guildID).getTextChannelById(tcID);
    }
    
    @Override
    public void onGuildMemberUpdateNickname(@NotNull GuildMemberUpdateNicknameEvent e) {
        
        String guildID = e.getGuild().getId();
        
        if(!ch(guildID, LogTypes.USER_NICKNAME_UPDATE))
            return;
        
        EmbedBuilder eb = new EmbedBuilder();
        
        eb.addField("Logger", "User nickname changed.\n" +
                                      "User: <@" + e.getMember().getId() + ">\n\n" +
                                      "Old nickname: " + (e.getOldNickname() == null ? "" : e.getOldNickname()) + "\n" +
                                      "New nickname: " + (e.getNewNickname() == null ? "" : e.getNewNickname()) + "\n", false);
        eb.setColor(Color.YELLOW);
        eb.setTimestamp(Instant.now());
        lc(guildID).sendMessage(eb.build()).queue();
    }
    
    @Override
    public void onGuildMemberRoleAdd(@NotNull GuildMemberRoleAddEvent e) {
        
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
        eb.setTimestamp(Instant.now());
        lc(guildID).sendMessage(eb.build()).queue();
    }
    
    @Override
    public void onGuildMemberRoleRemove(@NotNull GuildMemberRoleRemoveEvent e) {
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
        eb.setTimestamp(Instant.now());
        lc(guildID).sendMessage(eb.build()).queue();
    }
    
    @Override
    public void onRoleCreate(@NotNull RoleCreateEvent e) {
        String guildID = e.getGuild().getId();
        
        if(!ch(guildID, LogTypes.ROLE_ADD))
            return;
        
        EmbedBuilder eb = new EmbedBuilder();
        
        String roleCreated = e.getRole().getId();
        
        eb.addField("Logger", "Role created.\n" +
                                      "Role: <@&" + roleCreated + ">", false);
        eb.setColor(Color.GREEN);
        eb.setTimestamp(Instant.now());
        lc(guildID).sendMessage(eb.build()).queue();
    }
    
    @Override
    public void onRoleDelete(@NotNull RoleDeleteEvent e) {
        String guildID = e.getGuild().getId();
    
        if(!ch(guildID, LogTypes.ROLE_DELETE))
            return;
    
        EmbedBuilder eb = new EmbedBuilder();
    
        String roleCreated = e.getRole().getId();
    
        eb.addField("Logger", "Rrole deleted.\n" +
                                      "Role: <@&" + roleCreated + ">", false);
        eb.setColor(Color.RED);
        eb.setTimestamp(Instant.now());
        lc(guildID).sendMessage(eb.build()).queue();
    }
    
    @Override
    public void onRoleUpdateMentionable(@NotNull RoleUpdateMentionableEvent e) {
        String guildID = e.getGuild().getId();
        
        if(!ch(guildID, LogTypes.ROLE_UPDATE))
            return;
        
        EmbedBuilder eb = new EmbedBuilder();
        
        eb.addField("Logger", "Role updated.\n" +
                           "Role: <@&" + e.getRole().getId() + ">\n" +
                                      "Old mentionable state: " + e.getOldValue() + "\n" +
                                      "New mentionable state: " + e.getNewValue() + "\n", false);
        eb.setColor(Color.YELLOW);
        eb.setTimestamp(Instant.now());
        lc(guildID).sendMessage(eb.build()).queue();
    }
    
    @Override
    public void onRoleUpdateName(@NotNull RoleUpdateNameEvent e) {
        String guildID = e.getGuild().getId();
    
        if(!ch(guildID, LogTypes.ROLE_UPDATE))
            return;
    
        EmbedBuilder eb = new EmbedBuilder();
    
        eb.addField("Logger", "Role updated.\n" +
                                      "Role: <@&" + e.getRole().getId() + ">\n" +
                                      "Old name: " + e.getOldValue() + "\n" +
                                      "New name: " + e.getNewValue() + "\n", false);
        eb.setColor(Color.YELLOW);
        eb.setTimestamp(Instant.now());
        lc(guildID).sendMessage(eb.build()).queue();
    }

    @Override
    public void onGuildBan(@NotNull GuildBanEvent e) {
        String guildID = e.getGuild().getId();
        
        if(!ch(guildID, LogTypes.GUILD_MEMBER_BAN))
            return;
        
        EmbedBuilder eb = new EmbedBuilder();
        
        eb.addField("Logger", "Server member banned!\n" +
                "User: " + e.getUser().getAsMention() + ", " + e.getUser().getAsTag() + "\n" +
                "ID: " + e.getUser().getId() + "\n", false);
        eb.setTimestamp(Instant.now());
        eb.setColor(Color.RED);
        lc(guildID).sendMessage(eb.build()).queue();
    }
    
    
    @Override
    public void onGuildUnban(@NotNull GuildUnbanEvent e) {
        String guildID = e.getGuild().getId();
        
        if(!ch(guildID, LogTypes.GUILD_MEMBER_UNBAN))
            return;
        
        EmbedBuilder eb = new EmbedBuilder();
        
        eb.addField("Logger", "Server member un-banned!\n" +
                "User: " + e.getUser().getAsMention() + ", " + e.getUser().getAsTag() + "\n" +
                "ID: " + e.getUser().getId() + "\n", false);
        eb.setTimestamp(Instant.now());
        eb.setColor(Color.GREEN);
        
        lc(guildID).sendMessage(eb.build()).queue();
        
    }
    
    @Override
    public void onRoleUpdatePermissions(@NotNull RoleUpdatePermissionsEvent e) {
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
        
        eb.addField("Logger", "Role updated.\n" +
                                      "Role: <@&" + e.getRole().getId() + ">\n" +
                                      "**Old permissions**: " + String.join(",  ", oldValues) + "\n" +
                                      "**New permissions**: " + String.join(",  ", newValues), false);
        eb.setColor(Color.YELLOW);
        eb.setTimestamp(Instant.now());
        lc(guildID).sendMessage(eb.build()).queue();
    }
    
    @Override
    public void onGuildVoiceJoin(@NotNull GuildVoiceJoinEvent e) {
        String guildID = e.getGuild().getId();
        
        if(!ch(guildID, LogTypes.VOICE_CHANNEL_JOIN))
            return;
        
        EmbedBuilder eb = new EmbedBuilder();
        
        eb.addField("Logger", "User joined voice channel.\n" +
                                      "User: <@" + e.getMember().getId() + ">\n" +
                                      "Channel: " + e.getNewValue().getName(), false);
        eb.setColor(Color.GREEN);
        eb.setTimestamp(Instant.now());
        lc(guildID).sendMessage(eb.build()).queue();
    }
    
    @Override
    public void onGuildVoiceMove(@NotNull GuildVoiceMoveEvent e) {
        String guildID = e.getGuild().getId();
    
        if(!ch(guildID, LogTypes.VOICE_CHANNEL_SWITCH))
            return;
    
        EmbedBuilder eb = new EmbedBuilder();
    
        eb.addField("Logger", "User moved channels.\n" +
                                      "User: <@" + e.getMember().getId() + ">\n" +
                                      "Old channel: " + e.getOldValue().getName() + "\n" +
                                      "New channel: " + e.getNewValue().getName() + "\n", false);
        eb.setColor(Color.YELLOW);
        eb.setTimestamp(Instant.now());
        lc(guildID).sendMessage(eb.build()).queue();
    }
    
    @Override
    public void onGuildVoiceLeave(@NotNull GuildVoiceLeaveEvent e) {
        String guildID = e.getGuild().getId();
    
        if(!ch(guildID, LogTypes.VOICE_CHANNEL_LEAVE))
            return;
    
        EmbedBuilder eb = new EmbedBuilder();
    
        eb.addField("Logger", "User left voice channel.\n" +
                                      "User: <@" + e.getMember().getId() + ">\n" +
                                      "Channel: " + e.getChannelLeft().getName(), false);
        eb.setColor(Color.RED);
        eb.setTimestamp(Instant.now());
        lc(guildID).sendMessage(eb.build()).queue();
    }
    
    @Override
    public void onGuildMemberJoin(@NotNull GuildMemberJoinEvent e) {
        String guildID = e.getGuild().getId();
        
        if(!ch(guildID, LogTypes.GUILD_MEMBER_JOIN))
            return;
        
        EmbedBuilder eb = new EmbedBuilder();
        
        eb.addField("Logger", "User joined server.\n" +
                                      "User: <@" + e.getUser().getId() + ">\n" +
                                      "Account creation time: " + new Date((e.getUser().getIdLong() >> 22) + 1420070400000L), false);
        
        eb.setColor(Color.GREEN);
        eb.setTimestamp(Instant.now());
        lc(guildID).sendMessage(eb.build()).queue();
    }
    
    @Override
    public void onGuildMemberRemove(@NotNull GuildMemberRemoveEvent e) {
        String guildID = e.getGuild().getId();
        
        if(!ch(guildID, LogTypes.GUILD_MEMBER_LEAVE))
            return;
        
        EmbedBuilder eb = new EmbedBuilder();
        
        eb.addField("Logger", "User left server.\n" +
                                      "User: <@" + e.getUser().getId() + ">", false);
        
        eb.setColor(Color.RED);
        eb.setTimestamp(Instant.now());
        lc(guildID).sendMessage(eb.build()).queue();
    }
    
}
