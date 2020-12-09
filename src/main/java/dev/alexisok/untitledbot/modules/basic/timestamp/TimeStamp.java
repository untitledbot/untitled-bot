package dev.alexisok.untitledbot.modules.basic.timestamp;

import dev.alexisok.untitledbot.BotClass;
import dev.alexisok.untitledbot.command.CommandRegistrar;
import dev.alexisok.untitledbot.command.EmbedDefaults;
import dev.alexisok.untitledbot.command.Manual;
import dev.alexisok.untitledbot.plugin.UBPlugin;
import dev.alexisok.untitledbot.util.DateFormatUtil;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jetbrains.annotations.NotNull;
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
public final class TimeStamp extends UBPlugin {
    
    @Nullable
    @Override
    public MessageEmbed onCommand(@NotNull String[] args, @NotNull Message message) {
        long time;
        
        if(args.length == 1) {
            message.getChannel().sendMessage("This server was created:\n" +
                                             "" + new Date(((message.getGuild().getIdLong() >> 22) + 1420070400000L)) + "\n" +
                                                          "Other timestamps: `timestamp [user @ | channel # | Discord snowflake]`").queue(r -> BotClass.addToDeleteCache(message.getId(), r));
            return null;
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
            message.getChannel().sendMessage("Usage: `timestamp [snowflake ID | user @ | channel #]`" +
                                             "\n" +
                                             "To enable ID copying, see " +
                                             "support.discord.com/hc/en-us/articles/206346498").queue(r -> BotClass.addToDeleteCache(message.getId(), r));
            return null;
        }
        
        message.getChannel().sendMessage("Provided timestamp was created:\n" + new Date(time)).queue(r -> BotClass.addToDeleteCache(message.getId(), r));
        return null;
    }
    
    @Override
    public void onRegister() {
        CommandRegistrar.register("timestamp", this);
        Manual.setHelpPage("timestamp", "Get the timestamp of a snowflake.\n" +
                                                "Usage: `timestamp [snowflake | user @ | channel #]`\n" +
                                                "To get snowflakes, enable developer mode in Discord appearance settings, then " +
                                                "right click on something to copy the ID.\n\n" +
                                                "See [the docs](https://discord.com/developers/docs/reference#snowflakes) for more information on snowflakes.");
    }
}
