package dev.alexisok.untitledbot.modules.basic.cache;

import dev.alexisok.untitledbot.Main;
import dev.alexisok.untitledbot.command.CommandRegistrar;
import dev.alexisok.untitledbot.command.EmbedDefaults;
import dev.alexisok.untitledbot.modules.moderation.ModHook;
import dev.alexisok.untitledbot.plugin.UBPlugin;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author AlexIsOK
 * @since 1.3.22
 */
public final class CacheInfo extends UBPlugin {
    
    @Override
    public @NotNull MessageEmbed onCommand(String[] args, @NotNull Message message) {
        EmbedBuilder eb = new EmbedBuilder();
        EmbedDefaults.setEmbedDefaults(eb, message);
        
        eb.addField("Cache Info", String.format("%n" +
                "Cached members: %d%n" +
                "Cached users: %d%n" +
                "Cached servers: %d%n" +
                "Cached messages: %d%n",
                message.getGuild().getMemberCache().size(),
                Main.getUserCount(),
                Main.getGuildCount(),
                ModHook.getMessageCacheSize()
                ), false);
        return eb.build();
    }

    @Override
    public void onRegister() {
        CommandRegistrar.register("cache", this);
    }
}
