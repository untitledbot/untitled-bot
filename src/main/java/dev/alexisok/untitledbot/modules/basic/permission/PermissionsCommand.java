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

/**
 * @author AlexIsOK
 * @since 1.3.22
 */
public final class PermissionsCommand extends UBPlugin {
    
    @NotNull
    @Override
    public MessageEmbed onCommand(String[] args, @NotNull Message message) {
        EmbedBuilder eb = new EmbedBuilder();
        EmbedDefaults.setEmbedDefaults(eb, message);
        
        Member target = null;
        
        if(args.length != 1 && args[1].matches("[0-9]+"))
            target = message.getGuild().getMemberCache().getElementById(args[1]);
        
        try {
            if(target != null)
                target = message.getMentionedMembers(message.getGuild()).get(0);
        } catch(IndexOutOfBoundsException ignored) {}
        
        if(target == null)
            target = message.getMember();
        
        if(target == null)
            return eb.addField("uhm", "bot did an error whoops", false).build();
        
        StringBuilder perms = new StringBuilder();
        for(Permission p : target.getPermissions()) {
            perms.append(p.getName()).append("\n");
        }
        
        eb.setTitle("Permissions for " + target.getEffectiveName());
        eb.setDescription(perms.toString());
        eb.setColor(Color.GREEN);
        return eb.build();
        
    }
    
    @Override
    public void onRegister() {
        CommandRegistrar.register("permissions", this);
        Manual.setHelpPage("permission", "Get the permissions for a user.\n" +
                                                 "Usage: `permissions [@user]`");
        CommandRegistrar.registerAlias("permissions", "permission", "perms");
    }
}
