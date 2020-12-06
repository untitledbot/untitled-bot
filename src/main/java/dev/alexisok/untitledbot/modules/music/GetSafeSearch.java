package dev.alexisok.untitledbot.modules.music;

import dev.alexisok.untitledbot.command.CommandRegistrar;
import dev.alexisok.untitledbot.command.enums.UBPerm;
import dev.alexisok.untitledbot.plugin.UBPlugin;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author AlexIsOK
 * @since 1.3.23
 */
public final class GetSafeSearch extends UBPlugin {
    
    @Override
    public @Nullable MessageEmbed onCommand(String[] args, @NotNull Message message) {
        message.getAuthor().openPrivateChannel().queue(r -> r.sendMessage("Current filter regex:\n" + Play.FILTER_REGEX).queue());
        return null;
    }
    
    @Override
    public void onRegister() {
        CommandRegistrar.register("getsafesearch", UBPerm.OWNER, this);
    }
}
