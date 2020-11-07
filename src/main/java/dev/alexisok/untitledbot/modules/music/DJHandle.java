package dev.alexisok.untitledbot.modules.music;

import dev.alexisok.untitledbot.command.CommandRegistrar;
import dev.alexisok.untitledbot.command.Manual;
import dev.alexisok.untitledbot.plugin.UBPlugin;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author AlexIsOK
 * @since 1.3.23
 */
public class DJHandle extends UBPlugin {
    
    @Override
    public @Nullable MessageEmbed onCommand(String[] args, @NotNull Message message) {
        return null;
    }

    @Override
    public void onRegister() {
        CommandRegistrar.register("dj", "admin", this);
        Manual.setHelpPage("dj", "Set the DJ role in this server.\n" +
                "Usage: `dj <role @ | role name | role ID>`\n" +
                "Anyone with this role will be able to skip, stop, and add playlists.");
    }
}
