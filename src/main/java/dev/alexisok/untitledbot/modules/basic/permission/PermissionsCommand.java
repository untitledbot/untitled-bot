package dev.alexisok.untitledbot.modules.basic.permission;

import dev.alexisok.untitledbot.command.CommandRegistrar;
import dev.alexisok.untitledbot.command.EmbedDefaults;
import dev.alexisok.untitledbot.command.Manual;
import dev.alexisok.untitledbot.plugin.UBPlugin;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static net.dv8tion.jda.api.Permission.*;

/**
 * @author AlexIsOK
 * @since 1.3.22
 */
public final class PermissionsCommand extends UBPlugin {
    
    private static final String HAS_PERM = "<:tickyes:853817599997902860>";
    private static final String NOT_PERM = "<:tickno:853817696327434260>";
    
    @NotNull
    @Override
    public MessageEmbed onCommand(String[] args, @NotNull Message message) {
        EmbedBuilder eb = new EmbedBuilder();
        EmbedDefaults.setEmbedDefaults(eb, message);
        
        Member target = null;
        
        //if an id was provided
        if(args.length == 2 && args[1].matches("^[0-9]+")) {
            target = message.getGuild().getMemberById(args[1]);
        }
        
        //if a user was mentioned
        if(message.getMentionedMembers().size() == 1 && target == null) {
            target = message.getMentionedMembers().get(0);
        }
        
        //if no user was mentioned
        if(target == null)
            target = message.getMember();
        
        List<Permission> p = new ArrayList<>(target.getPermissions());
        
        eb.setTitle(String.format("Permissions for %s#%s", target.getUser().getName(), target.getUser().getDiscriminator()));
        
        eb.addField("General Permissions",
                String.format("%s%n%s%n%s%n%s%n%s%n%s%n%s%n%s%n%s%n%s%n%s%n%s%n%s%n",
                        hp(ADMINISTRATOR, p), hp(MANAGE_CHANNEL, p), hp(MANAGE_ROLES, p),
                        hp(MANAGE_EMOTES, p), hp(VIEW_AUDIT_LOGS, p), hp(VIEW_GUILD_INSIGHTS, p),
                        hp(MANAGE_WEBHOOKS, p), hp(MANAGE_SERVER, p), hp(CREATE_INSTANT_INVITE, p),
                        hp(NICKNAME_CHANGE, p), hp(NICKNAME_MANAGE, p), hp(KICK_MEMBERS, p), hp(BAN_MEMBERS, p)), true);
        
        eb.addField("Text Permissions",
                String.format("%s%n%s%n%s%n%s%n%s%n%s%n%s%n%s%n%s%n%s%n%s%n",
                        hp(MESSAGE_READ, p), hp(MESSAGE_WRITE, p), hp(MESSAGE_EMBED_LINKS, p),
                        hp(MESSAGE_ATTACH_FILES, p), hp(MESSAGE_ADD_REACTION, p), hp(MESSAGE_EXT_EMOJI, p),
                        hp(MESSAGE_MENTION_EVERYONE, p), hp(MESSAGE_MANAGE, p), hp(MESSAGE_HISTORY, p),
                        hp(MESSAGE_TTS, p), hp(USE_SLASH_COMMANDS, p)), true);
        
        eb.addField("Voice Permissions",
                String.format("%s%n%s%n%s%n%s%n%s%n%s%n%s%n%s%n%s%n",
                        hp(VIEW_CHANNEL, p), hp(VOICE_CONNECT, p), hp(VOICE_SPEAK, p),
                        hp(VOICE_STREAM, p), hp(VOICE_USE_VAD, p), hp(PRIORITY_SPEAKER, p),
                        hp(VOICE_MUTE_OTHERS, p), hp(VOICE_DEAF_OTHERS, p), hp(VOICE_MOVE_OTHERS, p)), true);
        
        return eb.build();
        
    }
    
    private static String hp(Permission permission, List<Permission> permissions) {
        String name = permission.getName();
        if(name.equals("Read Text Channels & See Voice Channels"))
            name = "See Voice Channels";
        return permissions.contains(permission)
                ? HAS_PERM + " " + name
                : NOT_PERM + " " + name;
    }
    
    @Override
    public void onRegister() {
        CommandRegistrar.register("permissions", this);
        Manual.setHelpPage("permission", "Get the permissions for a user.\n" +
                                                 "Usage: `permissions [@user | user_id]`");
        CommandRegistrar.registerAlias("permissions", "permission", "perms", "perm");
    }
}
