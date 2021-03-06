package dev.alexisok.untitledbot.modules.basic.casetoggle;

import dev.alexisok.untitledbot.BotClass;
import dev.alexisok.untitledbot.command.CommandRegistrar;
import dev.alexisok.untitledbot.command.EmbedDefaults;
import dev.alexisok.untitledbot.command.Manual;
import dev.alexisok.untitledbot.plugin.UBPlugin;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;

/**
 * Command `caps` and stuff
 * 
 * @author AlexIsOK
 * @since 1.3.21
 */
public final class Uppercase extends UBPlugin {
    
    @Override
    public @Nullable MessageEmbed onCommand(String[] args, @NotNull Message message) {
        try {
            if (args.length == 1) {
                message.getChannel().getHistoryBefore(message.getId(), 1)
                        .queue(t -> {
                            try {
                                if(message.mentionsEveryone()) {
                                    message.getChannel().sendMessage("Haha nerd nice try").queue(r -> BotClass.addToDeleteCache(message.getId(), r));
                                }
                                String c = t.getRetrievedHistory().get(0).getContentRaw().toUpperCase();
                                message.getChannel().sendMessage(
                                        new MessageBuilder()
                                                .setAllowedMentions(EnumSet.noneOf(Message.MentionType.class))
                                                .setContent(c)
                                                .build()
                                ).queue(r -> BotClass.addToDeleteCache(message.getId(), r));
                            } catch(Throwable ignored){}
                        });
            } else {
                message.getChannel().sendMessage(
                        new MessageBuilder()
                                .setContent(String.join(" ", ArrayUtils.remove(args, 0)).toUpperCase())
                                .setAllowedMentions(EnumSet.noneOf(Message.MentionType.class))
                                .build()
                ).queue(r -> BotClass.addToDeleteCache(message.getId(), r));
            }
        } catch(Throwable ignored) {
            message.getChannel().sendMessage("Couldn't do caps on the previous message.  Do I have the " +
                    "'Read Message History' permission?").queue(r -> BotClass.addToDeleteCache(message.getId(), r));
        }
        return null;
    }
    
    @Override
    public void onRegister() {
        CommandRegistrar.register("caps", this);
        Manual.setHelpPage("caps", "Convert a string to be ALL UPPERCASE LETTERS");
        CommandRegistrar.registerAlias("caps", "uppercase");
    }
}
