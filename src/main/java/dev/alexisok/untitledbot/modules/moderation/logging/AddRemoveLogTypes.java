package dev.alexisok.untitledbot.modules.moderation.logging;

import dev.alexisok.untitledbot.command.Command;
import dev.alexisok.untitledbot.command.EmbedDefaults;
import dev.alexisok.untitledbot.modules.vault.Vault;
import dev.alexisok.untitledbot.plugin.UBPlugin;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.ArrayList;

/**
 * @author AlexIsOK
 * @since 0.0.1
 */
public class AddRemoveLogTypes extends UBPlugin implements Command {
    
    @Override
    public @Nullable MessageEmbed onCommand(String @NotNull [] args, Message message) {
        EmbedBuilder eb = new EmbedBuilder();
        EmbedDefaults.setEmbedDefaults(eb, message);
        
        long currentLogPolicy;
        
        try {
            currentLogPolicy = Long.parseLong(Vault.getUserDataLocal(null,
                    message.getGuild().getId(),
                    "log.policy"));
        } catch(NullPointerException ignored) {
            //in case the policy does not exist
            currentLogPolicy = 0;
        }
        if(args[0].equals("add-log")) {
            if(args.length == 1) {
                eb.setColor(Color.RED);
                eb.addField("Logging", "Usage: add-log <log types.....>", false);
                
                return eb.build();
            }
        } else if(args[0].equals("remove-log")) {
            if(args.length == 1) {
                eb.setColor(Color.RED);
                eb.addField("Logging", "Usage: remove-log <log types.....>", false);
                return eb.build();
            }
            
        } else {
            return null;
        }
    }
    
    
    @Override
    public void onRegister() {
        
    }
}
