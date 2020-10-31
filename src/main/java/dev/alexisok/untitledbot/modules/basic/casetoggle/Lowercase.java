package dev.alexisok.untitledbot.modules.basic.casetoggle;

import dev.alexisok.untitledbot.command.CommandRegistrar;
import dev.alexisok.untitledbot.command.EmbedDefaults;
import dev.alexisok.untitledbot.command.Manual;
import dev.alexisok.untitledbot.plugin.UBPlugin;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author AlexIsOK
 * @since 1.3.21
 */
public final class Lowercase extends UBPlugin {
    
    @Override
    public @Nullable MessageEmbed onCommand(String[] args, @NotNull Message message) {
        try {
            if (args.length == 1) {
                message.getChannel().getHistoryBefore(message.getId(), 1)
                        .queue(t -> {
                            try {
                                if(message.mentionsEveryone() || message.getContentRaw().contains("@everyone")
                                        || message.getContentRaw().contains("@here")) {
                                    message.getChannel().sendMessage("Haha nerd nice try").queue();
                                }
                                message.getChannel().sendMessage((t.getRetrievedHistory().get(0).getContentRaw().toLowerCase())).queue();
                            } catch(Throwable ignored){}
                        });
            } else {
                message.getChannel().sendMessage((String.join(" ", ArrayUtils.remove(args, 0)).toLowerCase())).queue();
            }
        } catch(Throwable ignored) {
            message.getChannel().sendMessage("Cannot do lowercase on the previous message.").queue();
        }
        return null;
    }
    
    @Override
    public void onRegister() {
        CommandRegistrar.register("lowercase", this);
        Manual.setHelpPage("lowercase", "make everything lowercase i think");
    }
}
