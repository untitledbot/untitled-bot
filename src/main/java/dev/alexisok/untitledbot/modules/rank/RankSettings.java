package dev.alexisok.untitledbot.modules.rank;

import dev.alexisok.untitledbot.command.EmbedDefaults;
import dev.alexisok.untitledbot.modules.vault.Vault;
import dev.alexisok.untitledbot.plugin.UBPlugin;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

/**
 * Settings for ranks.
 * 
 * @author AlexIsOK
 * @since 1.3
 */
public final class RankSettings extends UBPlugin {
    
    @Override
    public @NotNull MessageEmbed onCommand(@NotNull String[] args, @NotNull Message message) {
        EmbedBuilder eb = new EmbedBuilder();
        EmbedDefaults.setEmbedDefaults(eb, message);
        
        if(args.length < 3) {
            eb.addField("Ranking", "Usage: `rank-settings <setting> <options...>`\n" +
                                           "See `help rank-settings` for more information.", false);
            eb.setColor(Color.RED);
            return eb.build();
        }
        
        if(args[1].equalsIgnoreCase("announce-level-up")) {
            args[2] = args[2].toLowerCase();
            switch(args[2]) {
                case "current": //current channel (when the user leveled up)
                    Vault.storeUserDataLocal(null, message.getGuild().getId(), "ranks-broadcast.rankup", "current");
                    eb.setColor(Color.GREEN);
                    eb.addField("Ranking",
                            "Level up messages will be sent to the channel where a user levels up.",
                            false);
                    return eb.build();
                case "channel": //specific channel
                    TextChannel setChannel;
                    try {
                        setChannel = message.getMentionedChannels().get(0);
                        if(!setChannel.canTalk() || !setChannel.getGuild().getId().equals(message.getGuild().getId()))
                            throw new Exception();
                        if(!message.getGuild().getTextChannels().contains(setChannel))
                            throw new Exception();
                    } catch(Exception ignored) {
                        eb.setColor(Color.RED);
                        eb.addField("Ranking", "Usage: `rank-settings announce-level-up channel <channel #>`\n" +
                                                       "Make sure the bot has access to send messages in the channel as well.", false);
                        return eb.build();
                    }
                    Vault.storeUserDataLocal(null, message.getGuild().getId(), "ranks-broadcast.rankup", "channel");
                    Vault.storeUserDataLocal(null, message.getGuild().getId(), "ranks-broadcast.rankup.channel", setChannel.getId());
                    eb.addField("Ranking", "Level up announcements have been set to channel <#"
                                                   + setChannel.getId()
                                                   + ">.", false);
                    eb.setColor(Color.GREEN);
                    return eb.build();
                case "none":
                    Vault.storeUserDataLocal(null, message.getGuild().getId(), "ranks-broadcast.rankup", "none");
                    eb.addField("Ranking", "Level up announcements have been disabled.", false);
                    eb.setColor(Color.GREEN);
                    return eb.build();
                default:
                    eb.addField("Ranking", "Usage: `rank-settings announce-level-up <current | channel | none>`\n\n" +
                                                   "`current` - the channel where the user levels up.\n" +
                                                   "`channel <channel #>` - a specific channel to send the level up message.\n" +
                                                   "`none` - do not do level up messages (does NOT stop the rank module).\n", false);
                    eb.setColor(Color.RED);
                    return eb.build();
            }
        }
        
        eb.setColor(Color.RED);
        eb.addField("Ranking", "The only available option as of now is to change level up messages.\n" +
                "Usage: `rank-settings announce-level-up <current | channel | none>`\n\n" +
                "`current` - the channel where the user levels up.\n" +
                "`channel <channel #>` - a specific channel to send the level up message.\n" +
                "`none` - do not do level up messages (does NOT stop the rank module, only disables level up messages).\n", false);
        
        return eb.build();
    }
}
