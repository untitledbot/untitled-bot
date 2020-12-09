package dev.alexisok.untitledbot.modules.reactions;

import dev.alexisok.untitledbot.command.CommandRegistrar;
import dev.alexisok.untitledbot.command.Manual;
import dev.alexisok.untitledbot.plugin.UBPlugin;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

import static java.util.concurrent.TimeUnit.*;

/**
 * Animated reaction that hides behind a wall
 * 
 * @author AlexIsOK
 * @since 1.3
 */
public final class Hide extends UBPlugin {
    
    private static final String[] PHASES = {
            "\u252C\u2534\u252C\u2534\u252C\u2534\u252C\u2534\u252C\u2534\u252C\u2534( \u00B0 \u035C\u0296 \u00B0)",
            "\u252C\u2534\u252C\u2534\u252C\u2534\u252C\u2534\u252C\u2534\u252C\u2534\u00B0 \u035C\u0296 \u00B0)",
            "\u252C\u2534\u252C\u2534\u252C\u2534\u252C\u2534\u252C\u2534\u252C\u2534\u035C\u0296 \u00B0)",
            "\u252C\u2534\u252C\u2534\u252C\u2534\u252C\u2534\u252C\u2534\u252C\u2534 \u00B0)",
            "\u252C\u2534\u252C\u2534\u252C\u2534\u252C\u2534\u252C\u2534\u252C\u2534\u00B0)",
            "\u252C\u2534\u252C\u2534\u252C\u2534\u252C\u2534\u252C\u2534\u252C\u2534",
    };
    
    private static final ArrayList<String> WATCHING = new ArrayList<>();
    
    @Override
    public @Nullable MessageEmbed onCommand(String[] args, @NotNull Message message) {
        
        if(WATCHING.contains(message.getGuild().getId())) {
            return null;
        }
        
        //i encourage you to not ask questions and pretend you never saw this
        if(message.getGuild().getSelfMember().getPermissions().contains(Permission.MESSAGE_WRITE)) {
            WATCHING.add(message.getGuild().getId());
            message.getChannel()
                   .sendMessage(PHASES[0]) //this is hurting my eyes and my head, but then again i wrote this on two hours of sleep
                   .queueAfter(0, NANOSECONDS, msg -> msg.editMessage(PHASES[1])
                           .queueAfter(2, SECONDS, msg2 -> msg2.editMessage(PHASES[2]) //at least it works...
                                   .queueAfter(2, SECONDS, msg3 -> msg3.editMessage(PHASES[3])
                                           .queueAfter(2, SECONDS, msg4 -> msg4.editMessage(PHASES[4]) //right?
                                                   .queueAfter(2, SECONDS, msg5 -> msg5.editMessage(PHASES[5]) //.....right..?
                                                           .queueAfter(2, SECONDS, end -> WATCHING.remove(end.getGuild().getId()))))))); //maybe...
            
        }
        
        return null;
    }
    
    @Override
    public void onRegister() {
        CommandRegistrar.register("hide", this);
        Manual.setHelpPage("hide", "hide behind a wall");
    }
}
