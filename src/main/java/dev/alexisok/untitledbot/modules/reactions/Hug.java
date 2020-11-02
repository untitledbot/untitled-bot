package dev.alexisok.untitledbot.modules.reactions;

import dev.alexisok.untitledbot.plugin.UBPlugin;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 
 * @author AlexIsOK
 * @since 1.3
 */
@Deprecated
public final class Hug extends UBPlugin {
    
    
    @Override
    public void onRegister() {
        
    }
    
    @Override
    public @Nullable MessageEmbed onCommand(@NotNull String[] args, @NotNull Message message) {
        
        message.getChannel().sendMessage("The hug message has been disabled.\n" +
                "If you still see this on a bot list, please let me know.").queue();
        
        return null;
    }
}
