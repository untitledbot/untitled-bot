package dev.alexisok.untitledbot.modules.basic.perms;

import dev.alexisok.untitledbot.command.CommandRegistrar;
import dev.alexisok.untitledbot.command.EmbedDefaults;
import dev.alexisok.untitledbot.command.Manual;
import dev.alexisok.untitledbot.modules.vault.Vault;
import dev.alexisok.untitledbot.plugin.UBPlugin;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.Objects;

/**
 * the permissions command is very important.  It can be skipped for more security,
 * but you won't be able to modify command permissions.
 * 
 * Usage: `setperms <user ID|user @|role ID|role @|guild> <permission> <true|false>`
 * 
 * @author AlexIsOK
 * @since 1.3
 */
public final class Permissions extends UBPlugin {
    @Override
    public void onRegister() {
        CommandRegistrar.register("setperms", "admin", this);
        Manual.setHelpPage("setperms", "Set the permissions of a user, role, or the entire guild.\nUsage: " +
                                               "setperms <user ID|user @|role ID|role @|guild> <permission> <true|false>");
        CommandRegistrar.registerAlias("setperms", "permissions", "perms", "perm", "pr");
    }
    
    @Override
    public @NotNull MessageEmbed onCommand(String[] args, Message message) {
        EmbedBuilder eb = new EmbedBuilder();
        EmbedDefaults.setEmbedDefaults(eb, message);
        //pre command checks
        if(message.getAuthor().isBot()) {
            eb.setColor(Color.RED);
            eb.addField("Permissions", "Bot users are not allowed to execute this command.", false);
            return eb.build();
        }
        if(!Objects.requireNonNull(message.getMember()).hasPermission(Permission.ADMINISTRATOR)) {
            eb.setColor(Color.RED);
            eb.addField("Permissions", "You must be an administrator on the server to execute this command.", false);
            return eb.build();
        }
        try {
            //make sure that the permission is valid.
            if(!args[2].matches("^[a-z]([a-z][.]?)+[a-z]$")) {
                eb.setColor(Color.RED);
                eb.addField("Permissions", "Please enter a valid command permission.", false);
                return eb.build();
            }
            if(message.getMentionedMembers().size() == 1) {
                Member mentionedMember = message.getMentionedMembers().get(0);
                String permission = args[2];
                boolean allow = args[3].equalsIgnoreCase("true");
            
                Vault.storeUserDataLocal(
                        mentionedMember.getId(),
                        message.getGuild().getId(),
                        permission,
                        allow ? "true" : "false"
                );
                eb.setColor(Color.GREEN);
                eb.addField("Permissions", "Permissions updated.", false);
                return eb.build();
            } else if(message.getMentionedRoles().size() == 1) {
                String ID = message.getMentionedRoles().get(0).getId();
                return getMessageEmbed(args, message, eb, ID);
            } else if (args[1].matches("^[0-9]+$")) {
                String memberID = args[1];
                return getMessageEmbed(args, message, eb, memberID);
            } else if (args[1].equals("guild")) {
                String permission = args[2];
                boolean allow = args[3].equalsIgnoreCase("true");
            
                Vault.storeUserDataLocal(
                        null,
                        message.getGuild().getId(),
                        permission,
                        allow ? "true" : "false"
                );
                eb.setColor(Color.GREEN);
                eb.addField("Permissions", "Permissions updated.", false);
                return eb.build();
            } else {
                eb.setColor(Color.RED);
                eb.addField("Permissions",
                        "Usage: `setperms **<user ID|user @|role ID|role @|guild>** <permission> <true|false>`",
                        false);
                return eb.build();
            }
        } catch(ArrayIndexOutOfBoundsException ignored) {
            eb.setColor(Color.RED);
            eb.addField("Permissions",
                    "Usage: `setperms <user ID|user @|role ID|role @|guild> <permission> <true|false>`",
                    false);
            return eb.build();
        }
    }
    
    /**
     * Get the message embed thing that does stuff.
     * @param args the arguments for the command
     * @param message the message itself
     * @param eb the embed builder
     * @param ID the ID of the user or role
     * @return the new embed thing
     */
    @NotNull
    private static MessageEmbed getMessageEmbed(String @NotNull [] args, @NotNull Message message, @NotNull EmbedBuilder eb, String ID) {
        String permission = args[2];
        boolean allow = args[3].equalsIgnoreCase("true");
        
        Vault.storeUserDataLocal(
                ID,
                message.getGuild().getId(),
                permission,
                allow ? "true" : "false"
        );
        eb.setColor(Color.GREEN);
        eb.addField("Permissions", "Permissions updated.", false);
        return eb.build();
    }
}
