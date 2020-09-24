package dev.alexisok.untitledbot.modules.starboard;

import dev.alexisok.untitledbot.command.CommandRegistrar;
import dev.alexisok.untitledbot.command.EmbedDefaults;
import dev.alexisok.untitledbot.modules.vault.Vault;
import dev.alexisok.untitledbot.plugin.UBPlugin;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

/**
 * Handles the `starboard` command.
 * 
 * @author AlexIsOK
 * @since 1.4.0
 */
public final class StarboardHandle extends UBPlugin {
    
    @Override
    public @NotNull MessageEmbed onCommand(@NotNull String[] args, @NotNull Message message) {
    
        EmbedBuilder eb = new EmbedBuilder();
        EmbedDefaults.setEmbedDefaults(eb, message);
        
        if(args.length == 1) {
            eb.addField("Starboard", "Please specify a sub command:\n" +
                                             "`starboard enable` to enable the starboard.\n" +
                                             "`starboard disable` to disable the starboard.\n" +
                                             "`starboard channel <channel #>` set the starboard channel.\n" +
                                             "`starboard threshold <number>` set the needed stars to add messages to the channel.", false);
            eb.setColor(Color.RED);
            return eb.build();
        }
        
        switch(args[1]) {
            case "enable":
                Vault.storeUserDataLocal(null, message.getGuild().getId(), "starboard", "true");
                eb.addField("Starboard", "The starboard has been enabled!  Set the channel using `starboard channel <channel #>` if you " +
                                                 "haven't already.", false);
                eb.setColor(Color.GREEN);
                return eb.build();
            case "disable":
                Vault.storeUserDataLocal(null, message.getGuild().getId(), "starboard", "false");
                eb.addField("Starboard", "The starboard has been disabled!  You can enable it again using `starboard enable`.", false);
                eb.setColor(Color.GREEN);
                return eb.build();
            case "channel":
                if(message.getMentionedChannels().size() != 1) {
                    eb.addField("Starboard", "Please specify a channel to send the messages to.\n" +
                                                     "Usage: `starboard channel <channel #>`", false);
                    eb.setColor(Color.RED);
                    return eb.build();
                }
                TextChannel tc = message.getMentionedChannels().get(0);
                
                if(tc == null) {
                    eb.addField("Starboard", "Hm... I can't seem to find that channel, please ")
                }
                
                if(!tc.canTalk()) {
                    eb.addField("Starboard", "Hmm... I don't think I can speak in <#" + tc.getId() + ">, is it the right channel?", false);
                    eb.setColor(Color.RED);
                    return eb.build();
                }
                eb.setColor(Color.GREEN);
                if(tc.isNSFW()) {
                    eb.addField("Warning", "Channel is NSFW, this isn't an error, just making sure you have the right channel.", false);
                    eb.setColor(Color.YELLOW);
                }
                Vault.storeUserDataLocal(null, message.getGuild().getId(), "starboard.channel", tc.getId());
                eb.addField("Starboard", "Messages that meet the amount of stars specified will be added to <#" + tc.getId() + ">!\n" +
                                                 "You can set the amount of stars required using `starboard threshold <number>`.", false);
                return eb.build();
        }
        
    }
    
    @Override
    public void onRegister() {
        CommandRegistrar.register("starboard", "admin", this);
    }
}
