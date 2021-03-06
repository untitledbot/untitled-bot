package dev.alexisok.untitledbot.modules.basic.casetoggle;

import dev.alexisok.untitledbot.BotClass;
import dev.alexisok.untitledbot.command.CommandRegistrar;
import dev.alexisok.untitledbot.command.EmbedDefaults;
import dev.alexisok.untitledbot.command.Manual;
import dev.alexisok.untitledbot.logging.Logger;
import dev.alexisok.untitledbot.plugin.UBPlugin;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.apache.commons.collections4.bag.CollectionBag;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;

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
                                Message m = t.getRetrievedHistory().get(0);
                                String content = m.getContentRaw().toLowerCase();
                                if(m.mentionsEveryone() || content.contains("@everyone")
                                        || content.contains("@here")) {
                                    message.getChannel().sendMessage("Haha nerd nice try").queue(r -> BotClass.addToDeleteCache(message.getId(), r));
                                    return;
                                }
                                String c = t.getRetrievedHistory().get(0).getContentRaw().toLowerCase();
                                message.getChannel().sendMessage(
                                        new MessageBuilder()
                                        .setAllowedMentions(EnumSet.noneOf(Message.MentionType.class))
                                        .setContent(c)
                                        .build()
                                ).queue(r -> BotClass.addToDeleteCache(message.getId(), r));
                            } catch(Throwable ignored){}
                        });
            } else {
                String content = String.join(" ", ArrayUtils.remove(args, 0)).toLowerCase();
                if(content.contains("@everyone") || content.contains("@here")) {
                    message.getChannel().sendMessage("Haha nerd nice try").queue(r -> BotClass.addToDeleteCache(message.getId(), r));
                    return null;
                }
                message.getChannel().sendMessage(
                        new MessageBuilder()
                                .setAllowedMentions(EnumSet.noneOf(Message.MentionType.class))
                                .setContent(content)
                                .build()
                ).queue(r -> BotClass.addToDeleteCache(message.getId(), r));
            }
        } catch(Throwable ignored) {
            message.getChannel().sendMessage("Couldn't do lowercase on the previous message.  Do I have the " +
                    "'Read Message History' permission?").queue(r -> BotClass.addToDeleteCache(message.getId(), r));
        }
        return null;
    }
    
    @Override
    public void onRegister() {
        CommandRegistrar.register("lowercase", this);
        Manual.setHelpPage("lowercase", "make everything lowercase i think");
        CommandRegistrar.registerAlias("lowercase", "lc");
    }
}
