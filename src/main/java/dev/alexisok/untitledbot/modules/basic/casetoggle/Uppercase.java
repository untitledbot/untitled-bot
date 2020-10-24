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
                                message.getChannel().sendMessage((t.getRetrievedHistory().get(0).getContentRaw().toUpperCase())).queue();
                            } catch(Throwable ignored){}
                        });
            } else {
                message.getChannel().sendMessage((String.join(" ", ArrayUtils.remove(args, 0)).toUpperCase())).queue();
            }
        } catch(Throwable ignored) {
            message.getChannel().sendMessage("Cannot mock the previous message as it either had no text or the text is in an embed.").queue();
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
