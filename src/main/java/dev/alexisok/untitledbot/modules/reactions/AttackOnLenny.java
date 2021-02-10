package dev.alexisok.untitledbot.modules.reactions;

import dev.alexisok.untitledbot.BotClass;
import dev.alexisok.untitledbot.command.CommandRegistrar;
import dev.alexisok.untitledbot.command.Manual;
import dev.alexisok.untitledbot.plugin.UBPlugin;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.*;

/**
 * ┬┴┬┴┤/╲/( ͡° ͡° ͜ʖ ͡° ͡°)/\╱\
 * (∩ ͡° ͜ʖ ͡°)⊃━炎炎炎炎炎炎炎炎
 * @author AlexIsOK
 * @since 1.3
 */
public final class AttackOnLenny extends UBPlugin {
    
    private static final ArrayList<String> WATCHING = new ArrayList<>();
    
    @Override
    public @Nullable MessageEmbed onCommand(String[] args, @NotNull Message message) {
        if(!WATCHING.contains(message.getGuild().getId())) {
            WATCHING.add(message.getGuild().getId());
        } else {
            message.getChannel().sendMessage("You are already watching an epic battle!!!").queue(r -> BotClass.addToDeleteCache(message.getId(), r));
            return null;
        }
        message.getChannel().sendMessage("HAHA I AM SUPER POWERFUL SPIDER LENNY CANNOT BE DEFEATED\n" +
                "\u252C\u2534\u252C\u2534\u2524/\u2572/( \u0361\u00B0 \u0361\u00B0 \u035C\u0296 \u0361\u00B0 \u0361\u00B0)/\\\u2571\\").queueAfter(0, MILLISECONDS, con1 -> {
                    con1.editMessage("NOT IF I HAVE ANYTHING TO SAY ABOUT IT\n" +
                            "(\u2229 \u0361\u00B0 \u035C\u0296 \u0361\u00B0)\u2283\u2501\u708E\u708E\u708E\u708E\u708E\u708E\u708E\u708E").queueAfter(2500, MILLISECONDS, con2 -> {
                                con2.editMessage("(\u2229 \u0361\u00B0 \u035C\u0296 \u0361\u00B0)\u2283\u2501\u708E\u708E\u708E\u708E\u708E\u708E\u708E\u708E /\u2572/( \u0361\u00B0 \u0361\u00B0 \u035C\u0296 \u0361\u00B0 \u0361\u00B0)/\\\u2571\\").queueAfter(2500, MILLISECONDS, con3 -> {
                                    con3.editMessage("\\*\\*BANG SMASH EXPLOSION SOUND EFFECTS\\*\\*").queueAfter(3000, MILLISECONDS, con4 -> {
                                        con4.editMessage("(\u0361\u00B0 \u035C\u0296 \u0361\u00B0) evil man has been defeated.  Use the `"
                                                + BotClass.getPrefixNice(con4.getGuild().getId()) + "aol` command to watch again.")
                                                .queueAfter(2000, MILLISECONDS, con5 -> {
                                            WATCHING.remove(con5.getGuild().getId());
                                        });
                                    });
                                });
                    });
        });
        return null;
    }
    
    @Override
    public void onRegister() {
        CommandRegistrar.register("attack-on-lenny", this);
        CommandRegistrar.registerAlias("attack-on-lenny", "aol");
        Manual.setHelpPage("attack-on-lenny", "┬┴┬┴┤/╲/( ͡° ͡° ͜ʖ ͡° ͡°)/\\╱\\");
    }
}
