package dev.alexisok.untitledbot.modules.music.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author AlexIsOK
 * @since 1.3.23
 */
public final class TrackScheduler extends AudioEventAdapter {
    
    private final AudioPlayer player;
    private final BlockingQueue<AudioTrack> queue;
    
    public TrackScheduler(@NotNull AudioPlayer player) {
        this.player = player;
        this.queue = new LinkedBlockingQueue<>();
    }
    
    public void queue(@NotNull AudioTrack track) {
        if(!player.startTrack(track, true))
            queue.offer(track);
    }
    
    public void nextTrack() {
        this.player.startTrack(this.queue.poll(), false);
    }
    
    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        if(endReason.mayStartNext)
            this.nextTrack();
    }
    
    public BlockingQueue<AudioTrack> getQueue() {
        return new LinkedBlockingQueue<>(this.queue);
    }
}