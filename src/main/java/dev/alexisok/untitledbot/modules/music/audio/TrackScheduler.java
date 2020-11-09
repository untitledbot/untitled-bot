package dev.alexisok.untitledbot.modules.music.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import dev.alexisok.untitledbot.modules.music.MusicKernel;
import lombok.Getter;
import lombok.Setter;
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
    
    @Getter@Setter
    private boolean repeat = false;
    
    @Getter
    private final String guildID;
    
    public TrackScheduler(@NotNull AudioPlayer player, @NotNull String guildID) {
        this.player = player;
        this.queue = new LinkedBlockingQueue<>();
        this.guildID = guildID;
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
        if(endReason.equals(AudioTrackEndReason.REPLACED))
        if(this.repeat && endReason.equals(AudioTrackEndReason.FINISHED)) {
            this.player.playTrack(track);
            return;
        }
        
        if(this.queue.size() == 0) {
            MusicKernel.INSTANCE.onQueueEnd(this.guildID);
            return;
        }
        
        AudioTrack nextTrack = this.queue.peek();
        
        if(endReason.mayStartNext)
            this.nextTrack();
        
        MusicKernel.INSTANCE.onNext(this.guildID, nextTrack);
    }
    
    public BlockingQueue<AudioTrack> getQueue() {
        return new LinkedBlockingQueue<>(this.queue);
    }

    /**
     * Clear the queue
     */
    public void clear() {
        this.queue.clear();
    }
}
