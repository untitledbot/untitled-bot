package dev.alexisok.untitledbot.modules.election;

import dev.alexisok.untitledbot.command.CommandRegistrar;
import dev.alexisok.untitledbot.command.EmbedDefaults;
import dev.alexisok.untitledbot.command.Manual;
import dev.alexisok.untitledbot.plugin.UBPlugin;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import sun.nio.ch.FileKey;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * 
 * Allow servers to host elections.
 * 
 * @author AlexIsOK
 * @since 1.3.23
 */
public final class StartElection extends UBPlugin {
    
    static {
        new File("./elections/").mkdirs();
    }
    
    @Override
    public @NotNull MessageEmbed onCommand(String[] args, @NotNull Message message) {
        
        EmbedBuilder eb = new EmbedBuilder();
        EmbedDefaults.setEmbedDefaults(eb, message);
        
        if(args.length == 1) {
            eb.addField("Election", "Usage: `election <start/end>`.\n" +
                    "For more help: see `help election-config`.", false);
            eb.setColor(Color.RED);
            return eb.build();
        }
        
        if(args[1].equalsIgnoreCase("start")) {
            eb.addField("Election", "Started the election.  Use the `election-config` command to configure the election.", false);
            eb.setColor(Color.GREEN);
            return eb.build();
        } else if(args[1].equalsIgnoreCase("stop")) {
            eb.addField("Election", "The election has been stopped!  Use the `election-results` command to see the results.", false);
            eb.setColor(Color.GREEN);
            return eb.build();
        } else {
            eb.addField("Election", "Usage: `election <start/end>`", false);
            eb.setColor(Color.RED);
            return eb.build();
        }
    }
    
    @Override
    public void onRegister() {
        CommandRegistrar.register("election", "admin", this);
        Manual.setHelpPage("election", "Start an election for a server.\n" +
                "Usage: `election <start/stop>`\n" +
                "For more information and configuration, see `help election-config`.");
    }
    
}
