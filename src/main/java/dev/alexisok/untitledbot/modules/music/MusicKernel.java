package dev.alexisok.untitledbot.modules.music;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import dev.alexisok.untitledbot.command.EmbedDefaults;
import dev.alexisok.untitledbot.modules.music.audio.MusicManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.Date;
import java.util.HashMap;

/**
 * Handles a lot of music stuff and commands.
 * 
 * @author AlexIsOK
 * @since 1.3.23
 */
public class MusicKernel {
    
    public static final MusicKernel INSTANCE;
    
    private static final int MAX_SONGS_PER_GUILD = 200;
    
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
                EmbedBuilder eb = new EmbedBuilder();
                eb.setTimestamp(new Date().toInstant());
                eb.addField("Play", "\u25B6\uFE0F The track " + track.getInfo().title + " has been added to the queue!", false);
                eb.setColor(Color.GREEN);
                channel.sendMessage(eb.build()).queue();
                play(channel.getGuild(),requestedVC, mm, track);
            }
            
            @Override
            public void playlistLoaded(@NotNull AudioPlaylist playlist) {
                EmbedBuilder eb = new EmbedBuilder();
                eb.setTimestamp(new Date().toInstant());
                eb.setColor(Color.GREEN);
                AudioTrack first = playlist.getSelectedTrack();
                
                int queueSize = INSTANCE.musicManagers.get(channel.getGuild().getId()).scheduler.getQueue().size();
                
                if(queueSize + playlist.getTracks().size() >= MAX_SONGS_PER_GUILD) {
                    eb.addField("Play", "Error loading playlist; playlist is either over 200 videos or " +
                            "the queue would overflow past 200", false);
                    eb.setColor(Color.RED);
                    channel.sendMessage(eb.build()).queue();
                    return;
                }
                
                if (first == null) {
                    first = playlist.getTracks().get(0);
                }
                
                //TODO reverse this for loop if there are problems loading playlists in the correct order.
                for(AudioTrack playlistItem : playlist.getTracks()) {
                    mm.scheduler.queue(playlistItem);
                }
                
                eb.addField("Play", String.format("Adding %s to the queue.%n" +
                        "Note: added playlist %s as well.", first.getInfo().title, playlist.getName()), false);
                play(channel.getGuild(), requestedVC, mm, first);
            }
            
            @Override
            public void noMatches() {
                
            }
            
            @Override
            public void loadFailed(@NotNull FriendlyException exception) {
                channel.sendMessage(String.format("Could not play the requested song!%nIf this happens again, please report this.")).queue();
                exception.printStackTrace();
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
        manager.scheduler.queue(track);
        if(g.getAudioManager().isConnected())
            return;
        g.getAudioManager().openAudioConnection(vc);
    }
    
    public synchronized AudioTrack[] queue(@NotNull Guild g) {
        return this.musicManagers.get(g.getId()).scheduler.getQueue().toArray(new AudioTrack[0]);
    }
    
    /**
     * Stop a track.
     * @param g the guild
     */
    public synchronized void stop(@NotNull Guild g) {
        this.musicManagers.get(g.getId()).player.destroy();
        g.getAudioManager().closeAudioConnection();
    }
    
    public synchronized AudioTrack skip(@NotNull Guild g) {
        AudioTrack currentTrack = this.musicManagers.get(g.getId()).player.getPlayingTrack();
        this.musicManagers.get(g.getId()).scheduler.nextTrack();
        return currentTrack;
    }

    /**
     * Set the pause state on the track
     * @param g the guild
     * @param state the paused state (true = pause, false = not paused)
     */
    public synchronized void pause(@NotNull Guild g, boolean state) {
        this.musicManagers.get(g.getId()).player.setPaused(state);
    }
    
    public synchronized AudioTrack nowPlaying(@NotNull Guild g) {
        return this.musicManagers.get(g.getId()).player.getPlayingTrack();
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

    /**
     * Check to see if the bot is the last non-bot user in the voice chat.
     * 
     * Disconnects if it is the only user.
     * 
     * @param vc the voice chat.
     */
    public synchronized void onUserLeaveVC(@NotNull VoiceChannel vc) {
        
        //don't check guilds the bot isn't playing music in
        if(!this.musicManagers.containsKey(vc.getGuild().getId()))
            return;
        
        boolean user = false;
        boolean hasSelf = false;
        
        for(Member m : vc.getMembers()) {
            
            if(m.getUser().getId().equals("730135989863055472"))
                hasSelf = true;
            
            if(m.getUser().isBot())
                continue;
            
            user = true;
        }
        
        if(!user && hasSelf) {
            //the same as running the "stop" command.
            this.stop(vc.getGuild());
        }
    }
    
}
