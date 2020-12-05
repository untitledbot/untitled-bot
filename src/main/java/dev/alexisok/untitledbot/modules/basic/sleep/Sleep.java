package dev.alexisok.untitledbot.modules.basic.sleep;

import dev.alexisok.untitledbot.command.CommandRegistrar;
import dev.alexisok.untitledbot.command.UBPerm;
import dev.alexisok.untitledbot.plugin.UBPlugin;
import lombok.SneakyThrows;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author AlexIsOK
 * @since 1.3.25
 */
public final class Sleep extends UBPlugin {
    
    @Override
    @SneakyThrows
    public @Nullable MessageEmbed onCommand(String[] args, @NotNull Message message) {
        message.getChannel().sendMessage("good night").queue();
        Thread.sleep(10000);
        message.getChannel().sendMessage("good morning!").queue();
        return null;
    }
    
    @Override
    public void onRegister() {
        CommandRegistrar.register("sleep", UBPerm.OWNER, this);
    }
}
