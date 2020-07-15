package dev.alexisok.untitledbot.modules.moderation.modcommands;

import dev.alexisok.untitledbot.Main;
import dev.alexisok.untitledbot.command.CommandRegistrar;
import dev.alexisok.untitledbot.command.EmbedDefaults;
import dev.alexisok.untitledbot.command.Manual;
import dev.alexisok.untitledbot.plugin.UBPlugin;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

/**
 * Banish command
 * 
 * @see Pardon
 * @author AlexIsOK
 * @since 0.0.1
 */
public class Ban extends UBPlugin {
    
    @Override
    public @Nullable MessageEmbed onCommand(String[] args, Message message) {
        EmbedBuilder eb = new EmbedBuilder();
        EmbedDefaults.setEmbedDefaults(eb, message);
        
        if(args.length < 3) {
            eb.addField("Moderation", "Usage: banish <user @ | user ID> <reason>", false);
            eb.setColor(Color.RED);
            return eb.build();
        }
    
        Member victim = message.getMentionedMembers().get(0);
        
        if(victim.hasPermission(Permission.ADMINISTRATOR)) {
            eb.addField("Moderation", "User <@" + victim.getId() + "> has administrator immunity!  Are they hacking?", false);
            eb.setColor(Color.RED);
            
            return eb.build();
        }
        
        //TODO check for different guild thing
        
        try {
            victim.ban(0, args[2]).queue();
            eb.setColor(Color.GREEN);
            eb.addField("Moderation", "The ban hammer has spoken!  User " + victim.getEffectiveName() + " has been banished!", false);
            return eb.build();
        } catch(IllegalArgumentException ignored) {
            eb.addField("Moderation", "An IllegalArgumentException was thrown...", false);
            eb.setColor(Color.RED);
            return eb.build();
        }
    }
    
    @Override
    public void onRegister() {
        CommandRegistrar.register("ban", "admin", this);
        CommandRegistrar.registerAlias("ban", "banish", "obliterate", "-rm-r-f-slash-asterisk--no-preserve-root");
        Manual.setHelpPage("ban", "Banish a user.\n" +
                                          "Usage: ban <user ID | user @> <reason>\n");
        CommandRegistrar.registerAliasManual("ban", "banish", "obliterate", "-rm-r-f-slash-asterisk--no-preserve-root");
    }
}
