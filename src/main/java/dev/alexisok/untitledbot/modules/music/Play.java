package dev.alexisok.untitledbot.modules.music;

import dev.alexisok.untitledbot.command.EmbedDefaults;
import dev.alexisok.untitledbot.command.Manual;
import dev.alexisok.untitledbot.plugin.UBPlugin;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.intellij.lang.annotations.RegExp;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

/**
 * Play command.
 * 
 * @see MusicCommands
 * @author AlexIsOK
 * @since 1.0.1 (made) 1.3.22 (fully implemented)
 */
public final class Play extends UBPlugin {
    
    /**
     * Adapted from https://stackoverflow.com/a/6904504
     */
    @RegExp
    public static final String YOUTUBE_VIDEO_REGEX = "(?:youtube\\.com/(?:[^/]+/.+/|(?:v|e(?:mbed)?)/|.*[?&]v=)|youtu\\.be/)([^\"&?/\\s]{11})";
    
    @Override
    public @Nullable MessageEmbed onCommand(@NotNull String[] args, @NotNull Message message) {
        EmbedBuilder eb = new EmbedBuilder();
        EmbedDefaults.setEmbedDefaults(eb, message);
        
        if(args.length == 1) {
            eb.addField("Music Player", "Usage: play <URL | search query>", false);
            eb.setColor(Color.RED);
            return eb.build();
        }
        
        return null;
    }
    
    @Override
    public void onRegister() {
        Manual.setHelpPage("play", "Play a song from a supported site, or search if no URL is provided.\n" +
                                           "Usage: play <URL | search query>\n" +
                                           "URL must be a fully qualified URL, for example: https://youtube.com/watch?v=dQw4w9WgXcQ or " +
                "a video ID (dQw4w9WgXcQ)\n");
    }
}
