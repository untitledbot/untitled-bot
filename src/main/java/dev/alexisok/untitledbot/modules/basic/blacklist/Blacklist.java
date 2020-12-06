package dev.alexisok.untitledbot.modules.basic.blacklist;

import dev.alexisok.untitledbot.command.CommandRegistrar;
import dev.alexisok.untitledbot.command.enums.UBPerm;
import dev.alexisok.untitledbot.plugin.UBPlugin;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static dev.alexisok.untitledbot.BotClass.BLACKLIST;
import static dev.alexisok.untitledbot.BotClass.addToBlacklist;

/**
 * @author AlexIsOK
 * @since 1.3.21
 */
public class Blacklist extends UBPlugin {
    
    @Override
    public @Nullable MessageEmbed onCommand(String[] args, @NotNull Message message) {
        if(!message.getAuthor().getId().equals("541763812676861952"))
            return null;
        try {
            String ID = message.getMentionedMembers().size() == 1 ? message.getMentionedMembers().get(0).getId() : args[1];
            
            if(ID.equals("541763812676861952")) {
                EmbedBuilder eb = new EmbedBuilder();
                eb.addField("no", "you cannot blacklist yourself lol", false);
                return eb.build();
            }
            
            if(BLACKLIST.contains(ID)) {
                BLACKLIST.remove(ID);
                EmbedBuilder eb = new EmbedBuilder();
                eb.addField("Blacklisted!", "This user has been removed from the blacklist :white_check_mark:", false);
                return eb.build();
            } else {
                addToBlacklist(ID);
                EmbedBuilder eb = new EmbedBuilder();
                eb.addField("Blacklisted!", "This user has been blacklisted :white_check_mark:", false);
                return eb.build();
            }
        } catch(Throwable ignored){}
        return null;
    }
    
    @Override
    public void onRegister() {
        CommandRegistrar.register("blacklist", UBPerm.OWNER, this);
    }
}
