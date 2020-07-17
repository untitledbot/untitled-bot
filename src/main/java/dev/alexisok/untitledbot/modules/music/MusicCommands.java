package dev.alexisok.untitledbot.modules.music;

import dev.alexisok.untitledbot.command.CommandRegistrar;
import dev.alexisok.untitledbot.plugin.UBPlugin;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jetbrains.annotations.Nullable;

/**
 * This registers all of the commands for the music portion of the bot.
 * 
 * @author AlexIsOK
 * @since 1.0.1
 */
public class MusicCommands {
    
    public static void registerMusicCommands() {
        CommandRegistrar.register("play", "core.music.play", new Play());
    }
    
}
