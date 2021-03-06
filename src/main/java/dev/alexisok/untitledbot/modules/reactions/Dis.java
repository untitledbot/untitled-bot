package dev.alexisok.untitledbot.modules.reactions;

import dev.alexisok.untitledbot.BotClass;
import dev.alexisok.untitledbot.command.CommandRegistrar;
import dev.alexisok.untitledbot.command.Manual;
import dev.alexisok.untitledbot.plugin.UBPlugin;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


/**
 * ಠ_ಠ
 * 
 * @author AlexIsOK
 * @since 1.3
 */
public final class Dis extends UBPlugin {
    
    private static final String FACE = "ಠ_ಠ";
    
    @Nullable
    @Override
    @Contract(pure = true)
    public MessageEmbed onCommand(@NotNull String[] args, @NotNull Message message) {
        try {
            message.getChannel().sendMessage(FACE).queue(r -> BotClass.addToDeleteCache(message.getId(), r));
        } catch (InsufficientPermissionException ignored) {}
        return null;
    }
    
    @Override
    public void onRegister() {
        CommandRegistrar.register("disappointed", this);
        Manual.setHelpPage("disappointed", "display ಠ_ಠ");
        CommandRegistrar.registerAlias("disappointed", "dis", "icantbelieveyouvedonethis");
    }
}
