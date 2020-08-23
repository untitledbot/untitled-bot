package dev.alexisok.untitledbot.modules.music;

import dev.alexisok.untitledbot.command.CommandRegistrar;

/**
 * This registers all of the commands for the music portion of the bot.
 * 
 * @author AlexIsOK
 * @since 1.0.1
 */
public final class MusicCommands {
    
    public static void registerMusicCommands() {
        CommandRegistrar.register("play", "core.music.play", new Play());
    }
    
}
