package dev.alexisok.untitledbot.modules.moderation.logging;

import dev.alexisok.untitledbot.command.EmbedDefaults;
import dev.alexisok.untitledbot.util.vault.Vault;
import dev.alexisok.untitledbot.plugin.UBPlugin;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

/**
 * Set the logging channels.
 * 
 * @author AlexIsOK
 * @since 0.0.1
 */
public final class SetLogChannel extends UBPlugin {
    
    @Override
    public @NotNull MessageEmbed onCommand(String[] args, @NotNull Message message) {
        EmbedBuilder eb = new EmbedBuilder();
        EmbedDefaults.setEmbedDefaults(eb, message);
        
        if(args.length == 1) {
            eb.setColor(Color.RED);
            eb.addField("Logging", "Usage: log-channel <text channel # | none>", false);
            
            return eb.build();
        }
        
        if(message.getMentionedChannels().size() == 0 && !args[1].equals("null")) {
            eb.addField("Logging", "Usage: log-channel <text channel # | none>", false);
            eb.setColor(Color.RED);
            return eb.build();
        }
        
        if(!args[1].equals("none")) {
            
            TextChannel tc = null;
            
            try {
                //to prevent using other channels outside of the guild
                tc = message.getGuild().getTextChannelById(message.getMentionedChannels().get(0).getId());
            } catch(Exception ignored) {}
            
            if(tc == null) {
                eb.addField("Logging", "Usage: log-channel <text channel # | none>", false);
                eb.setColor(Color.RED);
                return eb.build();
            }
            
            if(tc.isNSFW())
                eb.addField("Warning", "Channel is NSFW", false);
            
            
            //make sure the bot has access to the channel
            if (!tc.canTalk()) {
                eb.addField("Logging", "Cannot access that channel, do I have permission to send messages there?", false);
                eb.setColor(Color.RED);
                return eb.build();
            }
            
            Vault.storeUserDataLocal(null, message.getGuild().getId(), "log.channel", tc.getId());
    
            eb.setColor(Color.GREEN);
            eb.addField("Logging", "Logging channel has been set.", false);
        } else {
            Vault.storeUserDataLocal(null, message.getGuild().getId(), "log.channel", "null");
            
            eb.setColor(Color.GREEN);
            eb.addField("Logging", "Log channel has been disabled.", false);
        }
        return eb.build();
    }
    
}
