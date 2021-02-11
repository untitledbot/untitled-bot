package dev.alexisok.untitledbot.modules.basic.tag;

import dev.alexisok.untitledbot.command.CommandRegistrar;
import dev.alexisok.untitledbot.command.EmbedDefaults;
import dev.alexisok.untitledbot.command.Manual;
import dev.alexisok.untitledbot.plugin.UBPlugin;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

/**
 * @author AlexIsOK
 * @since 1.3.27
 */
public class Tag extends UBPlugin {
    @Override
    public @Nullable MessageEmbed onCommand(String[] args, @NotNull Message message) {
        return null;
    }

    @Override
    public void onRegister() {
        CommandRegistrar.register("tag", this);
        Manual.setHelpPage("tag", "Make a custom text command.\n" +
                "Usage:\n" +
                "`tag create <name> <data>`\n" +
                "`tag <name>`\n" +
                "`t` is also an alias of `tag`.");
        CommandRegistrar.registerAlias("tag", "t");
    }
}
