package dev.alexisok.untitledbot.modules.music;

import dev.alexisok.untitledbot.command.CommandRegistrar;
import dev.alexisok.untitledbot.command.EmbedDefaults;
import dev.alexisok.untitledbot.command.Manual;
import dev.alexisok.untitledbot.plugin.UBPlugin;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Seek to a specific time in the video.
 * 
 * @author AlexIsOK
 * @since 1.3.23
 */
public class Seek extends UBPlugin {
    
    @Override
    public @Nullable MessageEmbed onCommand(String[] args, @NotNull Message message) {
        
        EmbedBuilder eb = new EmbedBuilder();
        EmbedDefaults.setEmbedDefaults(eb, message);
        
        long gotoTime = 0L;
        
        gotoTime += Long.parseLong(args[1].split(":")[0]) * 60L;
        if(args[1].contains(":"))
            gotoTime += Long.parseLong(args[1].split(":")[1]);
        else
            gotoTime /= 60;
        gotoTime *= 1000;
        
        MusicKernel.INSTANCE.seek(message.getGuild().getId(), gotoTime);
        
        eb.addField("Seek", String.format("Seeking the player to %d:%d...", gotoTime / 1000 / 60, gotoTime / 1000 / 60 / 60), false);
        return eb.build();
    }
    
    @Override
    public void onRegister() {
        CommandRegistrar.register("seek", this);
        Manual.setHelpPage("seek", """
                Seek to a specific time in a video.
                Usage: `seek <time>`
                Examples:
                `seek 1:25` (1 min 25 sec)
                `seek 600 (5 min)""");
        CommandRegistrar.registerAlias("seek", "goto", "sleek");
    }
}
