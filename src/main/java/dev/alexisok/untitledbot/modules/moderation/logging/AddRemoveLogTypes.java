package dev.alexisok.untitledbot.modules.moderation.logging;

import dev.alexisok.untitledbot.command.Command;
import dev.alexisok.untitledbot.plugin.UBPlugin;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author AlexIsOK
 * @since 0.0.1
 */
public class AddRemoveLogTypes extends UBPlugin implements Command {
    
    @Override
    public @Nullable MessageEmbed onCommand(String @NotNull [] args, Message message) {
        if(args[0].equals("add-log")) {
            
        }
    }
    
    
    @Override
    public void onRegister() {
        
    }
}
