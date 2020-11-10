package dev.alexisok.untitledbot.modules.music.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import dev.alexisok.untitledbot.modules.music.MusicKernel;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author AlexIsOK
 * @since 1.3.23
 */
public final class TrackScheduler extends AudioEventAdapter {
    
    private final AudioPlayer player;
    private final ArrayList<AudioTrack> queue;
    
    @Getter@Setter
    private boolean repeat = false;
    
    @Getter
    private final String guildID;
    
    public TrackScheduler(@NotNull AudioPlayer player, @NotNull String guildID) {
        this.player = player;
        this.queue = new ArrayList<>();
        this.guildID = guildID;
    }
    
    public void queue(@NotNull AudioTrack track) {
        if(!player.startTrack(track, true))
            queue.add(track);
    }

    /**
     * Goes to the next track.
     * @return the track that is done playing.
     */
    @Contract
    @Nullable
    public AudioTrack nextTrack() {
        AudioTrack current = this.player.getPlayingTrack();
        if(this.queue.size() == 0 && this.player.getPlayingTrack() == null) {
            MusicKernel.INSTANCE.onQueueEnd(this.guildID);
            return current;
        }
        if(this.queue.size() != 0) {
            this.player.startTrack(this.queue.get(0), false);
            this.queue.remove(0);
        } else {
            this.player.startTrack(null, false);
        }
        return current;
    }
    
    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        if(endReason.equals(AudioTrackEndReason.REPLACED))
        if(this.repeat && endReason.equals(AudioTrackEndReason.FINISHED)) {
            MusicKernel.INSTANCE.onNext(this.guildID, track);
            this.player.playTrack(track);
            return;
        }
        
        if(this.queue.size() == 0 && !endReason.mayStartNext) {
            MusicKernel.INSTANCE.onQueueEnd(this.guildID);
            return;
        }
        
        if(this.queue.size() == 0) {
            MusicKernel.INSTANCE.onQueueEnd(this.guildID);
            return;
        }
        
        AudioTrack nextTrack = this.queue.get(0);
        
        if(endReason.mayStartNext)
            this.nextTrack();
        
        MusicKernel.INSTANCE.onNext(this.guildID, nextTrack);
    }
    
    public ArrayList<AudioTrack> getQueue() {
        return new ArrayList<>(this.queue);
    }
    
    /**
     * Clear the queue
     */
    public void clear() {
        this.queue.clear();
    }

    public AudioTrack skip(int n) {
        if(n == 0) return this.nextTrack();
        else return this.queue.remove(n);
    }
}
