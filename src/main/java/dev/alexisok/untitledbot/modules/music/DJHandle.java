package dev.alexisok.untitledbot.modules.music;

import dev.alexisok.untitledbot.command.CommandRegistrar;
import dev.alexisok.untitledbot.command.EmbedDefaults;
import dev.alexisok.untitledbot.command.Manual;
import dev.alexisok.untitledbot.command.UBPerm;
import dev.alexisok.untitledbot.plugin.UBPlugin;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.Arrays;
import java.util.List;

/**
 * @author AlexIsOK
 * @since 1.3.23
 */
public class DJHandle extends UBPlugin {
    
    @Override
    public @NotNull MessageEmbed onCommand(String[] args, @NotNull Message message) {

        EmbedBuilder eb = new EmbedBuilder();
        EmbedDefaults.setEmbedDefaults(eb, message);
        
        if(args.length == 1) {
            eb.addField("DJ", "Usage: `dj <role @ | role name | role ID | none>`", false);
            eb.setColor(Color.RED);
            return eb.build();
        }
        
        if(args[1].equals("none")) {
            MusicKernel.setDJRole(message.getGuild().getId(), null);
        }
        
        Role r = null;
        
        //role by id
        if(args[1].matches("[0-9]+"))
            r = message.getGuild().getRoleById(args[1]);
        
        //role by @mention (do not try to mess with @everyone)
        if(r == null && !message.mentionsEveryone())
            r = message.getMentionedRoles().size() == 1 ? message.getMentionedRoles().get(0) : null;
        
        //role by name
        if(r == null) {
            List<String> name = Arrays.asList(args);
            name.remove(0);
            String newName = Arrays.toString(name.toArray(new String[0]));
            List<Role> roles = message.getGuild().getRolesByName(newName, false);
            if(roles.size() != 0)
                r = roles.get(0);
        }
        
        if(r == null) {
            return eb.addField("DJ Role", "Could not find a role by that name / id / mention.", false).setColor(Color.RED).build();
        }
        
        MusicKernel.setDJRole(message.getGuild().getId(), r);
        
        eb.addField("DJ Role", "The DJ Role has been set to " + r.getAsMention() + ".  Any users with this role will be able to " +
                "use the `stop` and `skip` commands freely.\n", false);
        eb.setColor(Color.GREEN);
        return eb.build();
    }
    
    @Override
    public void onRegister() {
        CommandRegistrar.register("dj", UBPerm.ADMIN, this);
        Manual.setHelpPage("dj", "Set the DJ role in this server.\n" +
                "Usage: `dj <role @ | role name | role ID | none>`\n" +
                "Anyone with this role will be able to skip, stop, and add playlists.");
    }
}
