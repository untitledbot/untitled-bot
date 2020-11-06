package dev.alexisok.untitledbot.modules.basic.roleCount;

import dev.alexisok.untitledbot.Main;
import dev.alexisok.untitledbot.command.CommandRegistrar;
import dev.alexisok.untitledbot.command.EmbedDefaults;
import dev.alexisok.untitledbot.command.Manual;
import dev.alexisok.untitledbot.logging.Logger;
import dev.alexisok.untitledbot.plugin.UBPlugin;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.Arrays;

/**
 * Get a list of roles in the server or the amount of members with a role
 * 
 * @author AlexIsOK
 * @since 1.3.22
 */
public final class RoleCount extends UBPlugin {
    
    @NotNull
    @Override
    public MessageEmbed onCommand(String[] args, @NotNull Message message) {
        EmbedBuilder eb = new EmbedBuilder();
        EmbedDefaults.setEmbedDefaults(eb, message);
        
        if(args.length == 1) {
            //1 because @everyone counts as a role
            if(message.getGuild().getRoleCache().size() == 1)
                return eb
                        .addField("Role Count", "I couldn't find any roles in this server...", false)
                        .setColor(Color.RED)
                        .build();
            
            StringBuilder data = new StringBuilder();
            
            for(Role r : message.getGuild().getRoleCache()) {
                if(r.isPublicRole())
                    continue;
                CharSequence format = String.format("<@&%s> %s%n", r.getId(), r.getId());
                if(data.length() + format.length() > 2048)
                    break;
                data.append(format);
            }
            
            eb.setTitle("Roles in this server:");
            eb.setDescription(data.toString());
            eb.setColor(Color.GREEN);
            return eb.build();
        }
        
        Role r = null;
        
        if(args[1].matches("[0-9]+")) {
            r = message.getGuild().getRoleById(args[1]);
        }
        
        if(r == null) {
            try {
                String[] argsTmp = args.clone(); //make sure to clone
                String search = String.join(" ", Arrays.stream(argsTmp)
                        .skip(1)
                        .toArray(String[]::new));
                Logger.debug("Searching for role name " + search);
                r = message.getGuild().getRolesByName(search, true).get(0);
            } catch(IndexOutOfBoundsException e) {
                if(Main.DEBUG)
                    e.printStackTrace();
            } //if the role couldn't be found
        }
        
        if(r == null) {
            eb.addField("Error", "I couldn't find a role by that name or ID, check the spelling and try again.", false);
            eb.setColor(Color.RED);
            return eb.build();
        }
        
        int membersWithRole = message.getGuild().getMembersWithRoles(r).size();

        eb.addField("Role Count", String.format("I count %d member%s with the role %s.",
                membersWithRole,
                membersWithRole == 1 ? "" : "s",
                r.getName()), false);
        eb.setColor(Color.GREEN);
        return eb.build();
    }
    
    @Override
    public void onRegister() {
        CommandRegistrar.register("roles", this);
        Manual.setHelpPage("roles", "Get the roles in a server or the amount of members with a role.\n" +
                "Usage: `roles [role ID | role name]`\n" +
                "Does not accept role @ for obvious reasons.");
    }
}
