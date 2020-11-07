package dev.alexisok.untitledbot.modules.music.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import org.jetbrains.annotations.NotNull;

/**
 * @author AlexIsOK
 * @since 1.3.23
 */
public final class MusicManager {
    
    public final AudioPlayer player;
    public final TrackScheduler scheduler;
    
    public MusicManager(@NotNull AudioPlayerManager manager) {
        this.player = manager.createPlayer();
        this.scheduler = new TrackScheduler(this.player);
        this.player.addListener(scheduler);
    }
    
    public AudioHandler getSendHandler() {
        return new AudioHandler(this.player);
    }
    
}
