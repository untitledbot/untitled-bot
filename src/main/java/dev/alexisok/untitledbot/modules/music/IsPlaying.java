package dev.alexisok.untitledbot.modules.music;

import dev.alexisok.untitledbot.plugin.UBPlugin;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 
 * debug command, do not add to all commands
 * 
 * @author AlexIsOK
 * @since 1.3.23
 */
public final class IsPlaying extends UBPlugin {
    @Override
    public @Nullable MessageEmbed onCommand(String[] args, @NotNull Message message) {
        return new EmbedBuilder().setDescription("" + MusicKernel.INSTANCE.isPlaying(message.getGuild())).build();
    }
}
