package dev.alexisok.untitledbot.modules.reactions;

import dev.alexisok.untitledbot.command.CommandRegistrar;
import dev.alexisok.untitledbot.command.Manual;
import dev.alexisok.untitledbot.plugin.UBPlugin;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
    
    @Override
    public @Nullable MessageEmbed onCommand(String[] args, @NotNull Message message) {
        
        //i encourage you to not ask questions and pretend you never saw this
        if(message.getGuild().getSelfMember().getPermissions().contains(Permission.MESSAGE_WRITE))
            message
                    .getChannel()
                    .sendMessage(PHASES[0]) //this is hurting my eyes and my head, but then again i wrote this on two hours of sleep
                    .queueAfter(0, NANOSECONDS, msg -> msg.editMessage(PHASES[1])
                        .queueAfter(1, SECONDS, msg2 -> msg2.editMessage(PHASES[2]) //at least it works...
                            .queueAfter(1, SECONDS, msg3 -> msg3.editMessage(PHASES[3])
                                .queueAfter(1, SECONDS, msg4 -> msg4.editMessage(PHASES[4]) //right?
                                    .queueAfter(1, SECONDS, msg5 -> msg5.editMessage(PHASES[5]) //.....right..?
                                        .queueAfter(1, SECONDS)))))); //maybe...
        
        //no embeds for you
        return null;
    }
    
    @Override
    public void onRegister() {
        CommandRegistrar.register("hide", this);
        Manual.setHelpPage("hide", "hide behind a wall");
    }
}
