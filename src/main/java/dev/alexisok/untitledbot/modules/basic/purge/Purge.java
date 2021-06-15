package dev.alexisok.untitledbot.modules.basic.purge;

import dev.alexisok.untitledbot.Main;
import dev.alexisok.untitledbot.command.CommandRegistrar;
import dev.alexisok.untitledbot.command.EmbedDefaults;
import dev.alexisok.untitledbot.command.Manual;
import dev.alexisok.untitledbot.command.enums.UBPerm;
import dev.alexisok.untitledbot.plugin.UBPlugin;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.io.File;

/**
 * @author AlexIsOK
 * @since 1.3.24
 */
public final class Purge extends UBPlugin {
    
    @Override
    public @NotNull MessageEmbed onCommand(String[] args, @NotNull Message message) {
        EmbedBuilder eb = new EmbedBuilder();
        EmbedDefaults.setEmbedDefaults(eb, message);
        
        if(!message.getGuild().getOwnerId().equals(message.getAuthor().getId())) {
            eb.addField("Error", "This command can only be used by the owner of the server.", false);
            eb.setColor(Color.RED);
            return eb.build();
        }
        
        if(args.length == 1) {
            eb.addField("The purge command does not delete messages.", "Are you sure you want to do this?\n" +
                    "The bot will erase all of its user data (such as level, xp, and other configuration).\n" +
                    "Use `purge confirm` if you really want to go through with this.", false);
            eb.setColor(Color.RED);
            return eb.build();
        }
        
        if(args[1].equals("confirm")) {
            try {
                new File(Main.parsePropertiesLocation(null, message.getGuild().getId())).delete();
                String[] data = new File("./usrdata/" + message.getGuild().getId() + "/").list();
                for(String s : data) {
                    new File(new File("./usrdata/" + message.getGuild().getId() + "/").getPath(), s).delete();
                }
            } catch(Throwable ignored) {}
            
            eb.addField(":(", "If you want to add me back, [here's an invite link](https://discord.com/oauth2/authorize?client_id=730135989863055472&scope=bot&permissions=3460160).", false);
            eb.setColor(Color.GREEN);
            message.getChannel().sendMessage(eb.build()).queue(r -> r.getGuild().leave().queue());
            return null;
        }
        eb.addField("Purge", "Usage: `purge`", false);
        eb.setColor(Color.RED);
        return eb.build();
    }
    
    @Override
    public void onRegister() {
        CommandRegistrar.register("purge", UBPerm.ADMIN, this);
        Manual.setHelpPage("purge", "The purge command does NOT delete messages.\n" +
                "It is a server-owner only command that deletes all of the bot data and leaves.");
    }
}
