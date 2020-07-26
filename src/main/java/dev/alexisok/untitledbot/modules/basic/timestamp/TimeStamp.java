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
        
        if(args.length != 2 || !args[1].matches("^[0-9]+")) {
            eb.addField("Timestamp", "Usage: `timestamp <snowflake>`", false);
            eb.setColor(Color.RED);
            return eb.build();
        }
    
        long time;
        
        try {
            //get the unix timestamp.
            time = (Long.parseLong(args[1]) >> 22) + 1420070400000L;
        } catch(Exception ignored) { //nfe, oobe, etc.
            eb.addField("Timestamp", "Usage: `timestamp <snowflake>`", false);
            eb.setColor(Color.RED);
            return eb.build();
        }
        
        eb.addField("", new Date(time).toString(), false);
        
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
        CommandRegistrar.setDefaultPermissionForNode("core.timestamp", true);
    }
}
