package dev.alexisok.untitledbot.modules.basic.ping;

import dev.alexisok.untitledbot.BotClass;
import dev.alexisok.untitledbot.plugin.UBPlugin;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author AlexIsOK
 * @since 1.3.25
 */
public final class Pong extends UBPlugin {
    
    @Override
    public @Nullable MessageEmbed onCommand(String[] args, @NotNull Message message) {
        message.reply("Ping! ...wait, that's my line!").queue(r -> BotClass.addToDeleteCache(message.getId(), r));
        return null;
    }
    
}
