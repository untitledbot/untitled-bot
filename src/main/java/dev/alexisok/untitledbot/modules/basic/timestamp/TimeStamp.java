package dev.alexisok.untitledbot.modules.basic.timestamp;

import dev.alexisok.untitledbot.command.CommandRegistrar;
import dev.alexisok.untitledbot.command.EmbedDefaults;
import dev.alexisok.untitledbot.command.Manual;
import dev.alexisok.untitledbot.plugin.UBPlugin;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.Date;

/**
 * Get the date from a discord snowflake.
 * 
 * https://discord.com/developers/docs/reference#snowflakes
 * 
 * @author AlexIsOK
 * @since 1.3
 */
public class TimeStamp extends UBPlugin {
    
    @Override
    public @Nullable MessageEmbed onCommand(String[] args, Message message) {
        EmbedBuilder eb = new EmbedBuilder();
        EmbedDefaults.setEmbedDefaults(eb, message);
    
        long time;
        
        if(args.length == 1) {
            eb.addField("Timestamp", "Usage: `timestamp <snowflake ID | user @ | channel #>`" +
                                             "\n" +
                                             "To enable ID copying, see " +
                                             "[Discord's support page]" +
                                             "(https://support.discord.com/hc/en-us/articles/206346498).", false);
            eb.setColor(Color.RED);
            return eb.build();
        }
        
        try {
            
            if(message.getMentionedUsers().size() == 1)
                args[1] = message.getMentionedUsers().get(0).getId();
            else if(message.getMentionedChannels().size() == 1)
                args[1] = message.getMentionedChannels().get(0).getId();
            //does not support role mentions
            
            if(!args[1].matches("^[0-9]+"))
                throw new NumberFormatException();
            
            //get the unix timestamp.
            time = (Long.parseLong(args[1]) >> 22) + 1420070400000L;
        } catch(Exception ignored) { //nfe, oobe, etc.
            eb.addField("Timestamp", "Usage: `timestamp <snowflake ID | user @ | channel #>`" +
                                             "\n" +
                                             "To enable ID copying, see " +
                                             "[Discord's support page]" +
                                             "(https://support.discord.com/hc/en-us/articles/206346498).",
                    false);
            eb.setFooter("Formula: `(snowflake / 4194304) + 1420070400000` to get milliseconds since Jan. 1st, 1970 00:00");
            eb.setColor(Color.RED);
            return eb.build();
        }
        
        eb.addField("", new Date(time).toString(), false);
        eb.addBlankField(false);
        eb.setFooter("Formula: `(snowflake / 4194304) + 1420070400000` to get milliseconds since Jan. 1st, 1970 00:00");
        
        return eb.build();
    }
    
    @Override
    public void onRegister() {
        CommandRegistrar.register("timestamp", "core.timestamp", this);
        Manual.setHelpPage("timestamp", "Get the timestamp of a snowflake.\n" +
                                                "Usage: `timestamp <snowflake>`\n" +
                                                "To get snowflakes, enable developer mode in Discord appearance settings, then " +
                                                "right click on something to copy the ID.\n\n" +
                                                "See [the docs](https://discord.com/developers/docs/reference#snowflakes) for more information on snowflakes.");
    }
}
