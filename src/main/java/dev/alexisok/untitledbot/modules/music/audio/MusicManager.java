package dev.alexisok.untitledbot.modules.music.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import net.dv8tion.jda.api.entities.VoiceChannel;
import org.jetbrains.annotations.NotNull;

/**
 * @author AlexIsOK
 * @since 1.3.23
 */
public final class MusicManager {
    
    public final AudioPlayer player;
    public final TrackScheduler scheduler;
    public final VoiceChannel channel;
    
    public MusicManager(@NotNull AudioPlayerManager manager, @NotNull String guildID, @NotNull VoiceChannel channel) {
        this.player = manager.createPlayer();
        this.scheduler = new TrackScheduler(this.player, guildID);
        this.player.addListener(scheduler);
        this.channel = channel;
    }
    
    public AudioHandler getSendHandler() {
        return new AudioHandler(this.player);
    }

    public void seek(@NotNull String id, long l) {
        if(this.player.getPlayingTrack().isSeekable())
            this.player.getPlayingTrack().setPosition(l);
    }
}
