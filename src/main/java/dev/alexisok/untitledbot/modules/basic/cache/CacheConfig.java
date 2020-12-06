package dev.alexisok.untitledbot.modules.basic.cache;

import dev.alexisok.untitledbot.command.CommandRegistrar;
import dev.alexisok.untitledbot.command.Manual;
import dev.alexisok.untitledbot.command.enums.UBPerm;
import dev.alexisok.untitledbot.plugin.UBPlugin;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author AlexIsOK
 * @since
 */
public final class CacheConfig extends UBPlugin {
    @Override
    public @Nullable MessageEmbed onCommand(String[] args, @NotNull Message message) {
        return null;
    }

    @Override
    public void onRegister() {
        CommandRegistrar.register("cache-config", UBPerm.ADMIN, this);
        Manual.setHelpPage("cache-config", "Configure the cache for this server.\n" +
                "Usage: `cache-config <subcommand> <true/false>`\n" +
                "");
    }
}
