package dev.alexisok.untitledbot.modules.basic.update;

import dev.alexisok.untitledbot.command.CommandRegistrar;
import dev.alexisok.untitledbot.command.EmbedDefaults;
import dev.alexisok.untitledbot.command.enums.UBPerm;
import dev.alexisok.untitledbot.plugin.UBPlugin;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

/**
 * Describes a self-updater for the bot
 * 
 * @author AlexIsOK
 * @since 1.3.25
 */
public class Update extends UBPlugin {
    
    @Override
    public @Nullable MessageEmbed onCommand(String[] args, @NotNull Message message) {
        
        if(true)
            return null;
        
        if(message.getAttachments().size() != 1) {
            return null;
        }
        
        File f = new File(args[1]);
        
        message.getChannel().sendMessage("Downloading to " + f.getAbsolutePath() + "...").queue();
        
        message.getAttachments().get(0).downloadToFile()
                .complete(f);
        
        message.getChannel().sendMessage("Done downloading!").queue();
        
        return null;
    }
    
    @Override
    public void onRegister() {
        CommandRegistrar.register("update", UBPerm.OWNER, this);
    }
}
