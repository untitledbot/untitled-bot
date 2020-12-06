package dev.alexisok.untitledbot.modules.basic.superstats;

import dev.alexisok.untitledbot.command.CommandRegistrar;
import dev.alexisok.untitledbot.command.enums.UBPerm;
import dev.alexisok.untitledbot.plugin.UBPlugin;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author AlexIsOK
 * @since 1.3.24
 */
public final class SuperStats extends UBPlugin {
    
    @Override
    public @Nullable MessageEmbed onCommand(String[] args, @NotNull Message message) {
        EmbedBuilder eb = new EmbedBuilder();
        return null;
    }
    
    @Override
    public void onRegister() {
        CommandRegistrar.register("superstats", UBPerm.OWNER, this);
    }
    
}
