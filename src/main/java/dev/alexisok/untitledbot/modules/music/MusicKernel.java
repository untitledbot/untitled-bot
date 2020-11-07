package dev.alexisok.untitledbot.modules.music;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import dev.alexisok.untitledbot.modules.music.audio.MusicManager;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

/**
 * Handles a lot of music stuff and commands.
 * 
 * @author AlexIsOK
 * @since 1.3.23
 */
class MusicKernel {
    
    public static final MusicKernel INSTANCE;
    
    static {
        INSTANCE = new MusicKernel();
    }
    
    private final AudioPlayerManager playerManager;
    
    //guild id, music manager
    private final HashMap<String, MusicManager> musicManagers;
    
    private MusicKernel() {
        this.musicManagers = new HashMap<>();
        this.playerManager = new DefaultAudioPlayerManager();
        AudioSourceManagers.registerRemoteSources(playerManager);
        AudioSourceManagers.registerLocalSource(playerManager);
    }
    
    protected void loadAndPlay(@NotNull TextChannel channel, @NotNull String trackURL, @NotNull VoiceChannel requestedVC) {
        MusicManager mm = getAudioPlayer(channel.getGuild());
        
        this.playerManager.loadItemOrdered(mm, trackURL, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(@NotNull AudioTrack track) {
                channel.sendMessage(String.format("The track %s has been added to the queue.", track.getInfo().title)).queue(r -> {});
                play(channel.getGuild(),requestedVC, mm, track);
            }
            
            @Override
            public void playlistLoaded(@NotNull AudioPlaylist playlist) {
                AudioTrack first = playlist.getSelectedTrack();

                if (first == null) {
                    first = playlist.getTracks().get(0);
                }
                
                channel.sendMessage(String.format("Adding %s to the queue.%n" +
                        "Note: added playlist %s as well.", first.getInfo().title, playlist.getName())).queue();
                play(channel.getGuild(), requestedVC, mm, first);
            }
            
            @Override
            public void noMatches() {
                channel.sendMessage(String.format("I couldn't find any results for %s...", trackURL)).queue();
            }
            
            @Override
            public void loadFailed(@NotNull FriendlyException exception) {
                channel.sendMessage(String.format("Could not play the requested song!%nIf this happens again, please report this.")).queue();
            }
        });
    }

    /**
     * Play something
     * @param g the guild
     * @param vc the voice chat where the user requested the 
     * @param manager the music manager
     * @param track the track
     */
    public synchronized void play(@NotNull Guild g, @NotNull VoiceChannel vc, @NotNull MusicManager manager, @NotNull AudioTrack track) {
        if(g.getAudioManager().isConnected())
            return;
        manager.scheduler.queue(track);
        g.getAudioManager().openAudioConnection(vc);
    }
    
    /**
     * Stop a track.
     * @param g the guild
     */
    public synchronized void stop(@NotNull Guild g) {
        this.musicManagers.get(g.getId()).player.destroy();
        g.getAudioManager().closeAudioConnection();
    }
    
    public synchronized void skip(@NotNull Guild g) {
        this.musicManagers.get(g.getId()).scheduler.nextTrack();
    }

    /**
     * Set the pause state on the track
     * @param g the guild
     * @param state the paused state (true = pause, false = not paused)
     */
    public synchronized void pause(@NotNull Guild g, boolean state) {
        this.musicManagers.get(g.getId()).player.setPaused(state);
    }

    /**
     * Get the pause state on the track
     * @param g the guild
     * @return the pause state
     */
    public synchronized boolean isPaused(@NotNull Guild g) {
        return this.musicManagers.get(g.getId()).player.isPaused();
    }
    
    public synchronized boolean isPlaying(@NotNull Guild g) {
        return this.musicManagers.get(g.getId()).player.getPlayingTrack() != null;
    }
    
    private synchronized MusicManager getAudioPlayer(@NotNull Guild guild) {
        MusicManager mm = this.musicManagers.get(guild.getId());
        
        if(mm == null) {
            mm = new MusicManager(this.playerManager);
            this.musicManagers.put(guild.getId(), mm);
        }
        
        guild.getAudioManager().setSendingHandler(mm.getSendHandler());
        
        return mm;
    }
    
}
