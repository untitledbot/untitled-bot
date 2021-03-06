package dev.alexisok.untitledbot.modules.basic.time;

import dev.alexisok.untitledbot.BotClass;
import dev.alexisok.untitledbot.command.CommandRegistrar;
import dev.alexisok.untitledbot.command.Manual;
import dev.alexisok.untitledbot.command.enums.UBPerm;
import dev.alexisok.untitledbot.plugin.UBPlugin;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Times other commands.
 * Uses the `time` command.
 * 
 * @author AlexIsOK
 * @since 1.3.21
 */
public final class TimeCommand extends UBPlugin {
    
    @Nullable
    @Override
    @Contract(pure = true)
    public MessageEmbed onCommand(String[] args, @NotNull Message message) {
        long current = System.currentTimeMillis();
        
        
        long end = System.currentTimeMillis();
        
        try {
            message.getChannel().sendMessage(String.format("Command took %dms.", end - current)).queue(r -> BotClass.addToDeleteCache(message.getId(), r));
        } catch(Throwable ignored) {}
        
        return null;
        
    }
    
    @Override
    public void onRegister() {
        CommandRegistrar.register("time", UBPerm.OWNER, this);
        Manual.setHelpPage("time", "Time a command.\n" +
                                           "This will not bypass command permissions, you will still have to have the needed permissions " +
                                           "to run the command; however, errored commands will still be timed.\n" +
                                           "Usage: `time <command> <args...>`");
    }
}
