package dev.alexisok.untitledbot.modules.starboard;

import dev.alexisok.untitledbot.command.CommandRegistrar;
import dev.alexisok.untitledbot.command.EmbedDefaults;
import dev.alexisok.untitledbot.command.Manual;
import dev.alexisok.untitledbot.modules.vault.Vault;
import dev.alexisok.untitledbot.plugin.UBPlugin;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import org.jetbrains.annotations.NotNull;

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
                                             "`starboard threshold <number>` set the needed stars to add messages to the channel.\n" +
                                             "`starboard info` get information about the starboard configuration.", false);
            eb.setColor(Color.RED);
            return eb.build();
        }
        
        switch(args[1]) {
            case "enable":
                Vault.storeUserDataLocal(null, message.getGuild().getId(), "starboard", "true");
                eb.addField("Starboard", "The starboard has been enabled!  Set the channel using `starboard channel #channel` if you " +
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
                                                     "Usage: `starboard channel #channel`", false);
                    eb.setColor(Color.RED);
                    return eb.build();
                }
                
                //make it so that channels not in the specified guild cannot be used
                TextChannel tc = message.getGuild().getTextChannelById(message.getMentionedChannels().get(0).getId());
                
                if(tc == null) {
                    eb.addField("Starboard", "Hm... I can't seem to find that channel, please make sure I have access to it " +
                                                     "and can speak in it.", false);
                    eb.setColor(Color.RED);
                    return eb.build();
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
            case "threshold":
                if(args.length != 3) {
                    eb.addField("Starboard", "Please specify an amount of stars needed to send a message to the starboard.\n" +
                                                     "Usage: `starboard threshold <number>` where <number> is the amount of :star: emotes needed.", false);
                    eb.setColor(Color.RED);
                    return eb.build();
                }
                try {
                    int threshold = Integer.parseInt(args[2]);
                    if(threshold < 1 || threshold >= 500) {
                        eb.addField("Starboard", "Threshold must between 1 and 500 (inclusive)", false);
                        eb.setColor(Color.RED);
                        return eb.build();
                    }
                    Vault.storeUserDataLocal(null, message.getGuild().getId(), "starboard.threshold", args[2]);
                    eb.addField("Starboard", String.format("Threshold has been set to %s, so messages will need %s stars ( :star: ) reactions" +
                                                                   " to be added to the channel.", args[2], args[2]), false);
                    eb.setColor(Color.GREEN);
                    return eb.build();
                } catch(Throwable t) {
                    eb.addField("Starboard", "Usage: `starboard threshold <number>`", false);
                    eb.setColor(Color.RED);
                    return eb.build();
                }
            case "info":
                
                if(!Vault.getUserDataLocalOrDefault(null, message.getGuild().getId(), "starboard", "false").equals("true")) {
                    eb.addField("Starboard", "Starboard is not enabled in this server.", false);
                    eb.setColor(Color.GREEN);
                    return eb.build();
                }
                
                String threshold = Vault.getUserDataLocalOrDefault(null, message.getGuild().getId(), "starboard.threshold", "unset (10)");
                String channelID = Vault.getUserDataLocalOrDefault(null, message.getGuild().getId(), "starboard.channel", "unset");
                
                eb.addField("Starboard", "The starboard channel is <#" + channelID + "> and there are " + threshold + " :star: emotes required for messages to " +
                                                 "be added to the channel.", false);
                eb.setColor(Color.GREEN);
                return eb.build();
            default:
                eb.addField("Starboard", "Unknown sub-command " + args[1] + ".  Usage:\n" +
                                                 "`starboard enable` to enable the starboard.\n" +
                                                 "`starboard disable` to disable the starboard.\n" +
                                                 "`starboard channel <channel #>` set the starboard channel.\n" +
                                                 "`starboard threshold <number>` set the needed stars to add messages to the channel.\n" +
                                                 "`starboard info` get information about the starboard configuration.", false);
                eb.setColor(Color.RED);
                return eb.build();
                
        }
        
    }
    
    @Override
    public void onRegister() {
        CommandRegistrar.register("starboard", "admin", this);
        Manual.setHelpPage("starboard", "Configure settings for starboard.\n" +
                                                "A list of sub-commands can be found simply by using the `starboard` command.\n" +
                                                "\n" +
                                                "Messages that are reacted to with enough :star: emotes will be " +
                                                "added to a specified channel, sort of like a hall of fame for " +
                                                "Discord messages.\n" +
                                                "\n" +
                                                "Any user can add reactions, but admins can set the required amount " +
                                                "from 1 to 500, to fit small and large servers.\n" +
                                                "\n" +
                                                "This was inspired by the Starboard on Discord Bot List.");
    }
}
