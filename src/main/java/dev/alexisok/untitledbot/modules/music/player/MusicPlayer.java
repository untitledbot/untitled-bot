package dev.alexisok.untitledbot.modules.music.player;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import com.sedmelluq.discord.lavaplayer.track.playback.MutableAudioFrame;
import net.dv8tion.jda.api.audio.AudioSendHandler;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author AlexIsOK
 * @since 1.3.22
 */
public final class MusicPlayer {
    
    private final AudioPlayerManager playerManager;
    private final Map<Long, GuildMusicManager> musicManagers;
    
    private MusicPlayer() {
        this.musicManagers = new HashMap<>();

        this.playerManager = new DefaultAudioPlayerManager();
        AudioSourceManagers.registerRemoteSources(playerManager);
        AudioSourceManagers.registerLocalSource(playerManager);
    }

    public static class GuildMusicManager {
        /**
         * Audio player for the guild.
         */
        public final AudioPlayer player;
        /**
         * Track scheduler for the player.
         */
        public final TrackScheduler scheduler;

        /**
         * Creates a player and a track scheduler.
         * @param manager Audio player manager to use for creating the player.
         */
        public GuildMusicManager(AudioPlayerManager manager) {
            player = manager.createPlayer();
            scheduler = new TrackScheduler(player);
            player.addListener(scheduler);
        }

        /**
         * @return Wrapper around AudioPlayer to use it as an AudioSendHandler.
         */
        public AudioPlayerSendHandler getSendHandler() {
            return new AudioPlayerSendHandler(player);
        }
        
    }
    
    public static class TrackScheduler extends AudioEventAdapter {
        private final AudioPlayer player;
        private final BlockingQueue<AudioTrack> queue;

        /**
         * @param player The audio player this scheduler uses
         */
        public TrackScheduler(AudioPlayer player) {
            this.player = player;
            this.queue = new LinkedBlockingQueue<>();
        }

        /**
         * Add the next track to queue or play right away if nothing is in the queue.
         *
         * @param track The track to play or add to queue.
         */
        public void queue(AudioTrack track) {
            // Calling startTrack with the noInterrupt set to true will start the track only if nothing is currently playing. If
            // something is playing, it returns false and does nothing. In that case the player was already playing so this
            // track goes to the queue instead.
            if (!player.startTrack(track, true)) {
                queue.offer(track);
            }
        }

        /**
         * Start the next track, stopping the current one if it is playing.
         */
        public void nextTrack() {
            // Start the next track, regardless of if something is already playing or not. In case queue was empty, we are
            // giving null to startTrack, which is a valid argument and will simply stop the player.
            player.startTrack(queue.poll(), false);
        }

        @Override
        public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
            // Only start the next track if the end reason is suitable for it (FINISHED or LOAD_FAILED)
            if (endReason.mayStartNext) {
                nextTrack();
            }
        }
    }
    
    public static class AudioPlayerSendHandler implements AudioSendHandler {
        private final AudioPlayer audioPlayer;
        private final ByteBuffer buffer;
        private final MutableAudioFrame frame;

        /**
         * @param audioPlayer Audio player to wrap.
         */
        public AudioPlayerSendHandler(AudioPlayer audioPlayer) {
            this.audioPlayer = audioPlayer;
            this.buffer = ByteBuffer.allocate(1024);
            this.frame = new MutableAudioFrame();
            this.frame.setBuffer(buffer);
        }

        @Override
        public boolean canProvide() {
            // returns true if audio was provided
            return audioPlayer.provide(frame);
        }

        @Override
        public ByteBuffer provide20MsAudio() {
            // flip to make it a read buffer
            buffer.flip();
            return buffer;
        }

        @Override
        public boolean isOpus() {
            return true;
        }
    }
}
