package dev.alexisok.untitledbot.modules.music;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import dev.alexisok.untitledbot.Main;
import dev.alexisok.untitledbot.logging.Logger;
import dev.alexisok.untitledbot.modules.music.audio.MusicManager;
import dev.alexisok.untitledbot.modules.vault.Vault;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

/**
 * Handles a lot of music stuff and commands.
 * 
 * @author AlexIsOK
 * @since 1.3.23
 */
public class MusicKernel {
    
    public static final MusicKernel INSTANCE;
    
    private static final int MAX_SONGS_PER_GUILD = 200;
    
    private static final HashMap<String, @Nullable Role> DJ_ROLE_CACHE;
    
    private static final String DJ_ROLE_VAULT_NAME = "dj.id";
    
    static {
        INSTANCE = new MusicKernel();
        DJ_ROLE_CACHE = new HashMap<>();
    }
    
    {
        lastChannels = new HashMap<>();
    }

    /**
     * Get the DJ role for a specific guild
     * @param guildID the guild ID
     * @return the DJ role
     */
    @Nullable
    @Contract(pure = true)
    public static synchronized Role getDJRole(@NotNull String guildID) {
        if(!DJ_ROLE_CACHE.containsKey(guildID))
            updateCache(guildID);
        
        return DJ_ROLE_CACHE.getOrDefault(guildID, null);
    }
    
    public static synchronized void setDJRole(@NotNull String guildID, Role r) {
        DJ_ROLE_CACHE.put(guildID, r);
        Vault.storeUserDataLocal(null, guildID, DJ_ROLE_VAULT_NAME, r == null ? "null" : r.getId());
    }
    
    private static synchronized void updateCache(@NotNull String guildID) {
        String roleID = Vault.getUserDataLocal(null, guildID, DJ_ROLE_VAULT_NAME);
        
        if(roleID == null)
            return;

        Logger.debug("Updating DJ cache to include " + roleID + " for " + guildID);
        
        //do not check for null
        DJ_ROLE_CACHE.put(guildID, Main.jda.getRoleById(roleID));
    }
    
    /**
     * Send a message to a guild when the next track is starting.
     * @param guildID the id of the guild
     * @param track the track
     */
    public synchronized void onNext(@NotNull String guildID, @NotNull AudioTrack track) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("\u25B6\uFE0F Now Playing", track.getInfo().uri);
        eb.addField(NowPlaying.escapeDiscordMarkdown(track.getInfo().title),
                "By " + NowPlaying.escapeDiscordMarkdown(track.getInfo().author), false);
        this.lastChannels.get(guildID).sendMessage(eb.build()).queueAfter(200, TimeUnit.MILLISECONDS);
    }
    
    private final AudioPlayerManager playerManager;
    
    //guild id, music manager
    private final HashMap<String, MusicManager> musicManagers;
    
    private final HashMap<String, TextChannel> lastChannels;
    
    private MusicKernel() {
        this.musicManagers = new HashMap<>();
        this.playerManager = new DefaultAudioPlayerManager();
        AudioSourceManagers.registerRemoteSources(playerManager);
    }
    
    protected void loadAndPlay(@NotNull TextChannel channel, @NotNull String trackURL, @NotNull VoiceChannel requestedVC) {
        MusicManager mm = getAudioPlayer(channel.getGuild());
        
        this.playerManager.loadItemOrdered(mm, trackURL, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(@NotNull AudioTrack track) {
                EmbedBuilder eb = new EmbedBuilder();
                eb.setTimestamp(new Date().toInstant());
                eb.addField("Play", String.format("▶ The track **%s** has been added to the queue!",
                        NowPlaying.escapeDiscordMarkdown(track.getInfo().title)), false);
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
                
                if(first == null) {
                    first = playlist.getTracks().get(0);
                }
                
                for(AudioTrack playlistItem : playlist.getTracks()) {
                    mm.scheduler.queue(playlistItem);
                }
                
                eb.addField("Play", String.format("Adding **%s** to the queue.%n" +
                        "Note: added playlist **%s** as well.",
                        NowPlaying.escapeDiscordMarkdown(first.getInfo().title),
                        NowPlaying.escapeDiscordMarkdown(playlist.getName())), false);
                play(channel.getGuild(), requestedVC, mm, first);
                channel.sendMessage(eb.build()).queue();
            }
            
            @Override
            public void noMatches() {
                channel.sendMessage("No results were found for the song, please try a different query.").queue();
            }
            
            @Override
            public void loadFailed(@NotNull FriendlyException exception) {
                channel.sendMessage(String.format("Could not play the requested song!%nIf this happens with a different song, please report this.")).queue();
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

    /**
     * Get the queue of songs.
     * @param g the guild
     * @return the queued songs.
     */
    @Nullable
    @Contract(pure = true)
    public synchronized AudioTrack[] queue(@NotNull Guild g) {
        return this.musicManagers.get(g.getId()).scheduler.getQueue().toArray(new AudioTrack[0]);
    }
    
    /**
     * Stop a track.
     * @param g the guild
     */
    public synchronized void stop(@NotNull Guild g) {
        this.musicManagers.get(g.getId()).scheduler.clear();
        this.musicManagers.get(g.getId()).player.destroy();
        g.getAudioManager().closeAudioConnection();
        this.musicManagers.remove(g.getId());
    }

    /**
     * Skip a track for a guild.
     * @param g the guild.
     * @param n the number to skip, current = 0.
     * @return the skipped track.
     */
    @Contract
    @Nullable
    public synchronized AudioTrack skip(@NotNull Guild g, int n) {
        AudioTrack currentTrack = this.musicManagers.get(g.getId()).player.getPlayingTrack();
        
        if(n == 0)
            this.musicManagers.get(g.getId()).scheduler.skip(0); //current track
        else
            return this.musicManagers.get(g.getId()).scheduler.skip(Math.min(n - 1, this.musicManagers.get(g.getId()).scheduler.getQueue().size())); //specified track
        
        return currentTrack;
    }

    /**
     * Set the pause state on the track
     * @param g the guild
     * @param state the paused state (true = pause, false = not paused)
     */
    public synchronized void pause(@NotNull Guild g, boolean state) {
        if(isPlaying(g))
            this.musicManagers.get(g.getId()).player.setPaused(state);
    }

    /**
     * Get the now playing track for a guild.
     * @param g the guild.
     * @return the now playing track.
     */
    @Contract(pure = true)
    public synchronized AudioTrack nowPlaying(@NotNull Guild g) {
        return this.musicManagers.get(g.getId()).player.getPlayingTrack();
    }
    
    /**
     * Get the pause state on the track
     * @param g the guild
     * @return the pause state
     */
    @Contract(pure = true)
    public synchronized boolean isPaused(@NotNull Guild g) {
        return this.musicManagers.get(g.getId()).player.isPaused();
    }

    /**
     * Debug
     * @param g the guild
     * @return true if the player for this guild is playing
     */
    @Contract(pure = true)
    public synchronized boolean isPlaying(@NotNull Guild g) {
        return this.musicManagers.get(g.getId()).player.getPlayingTrack() != null;
    }
    
    /**
     * Get the current music manager.
     * @param guild the guild
     * @return the current music manager by guild ID
     */
    @NotNull
    @Contract(pure = true)
    private synchronized MusicManager getAudioPlayer(@NotNull Guild guild) {
        MusicManager mm = this.musicManagers.get(guild.getId());
        
        if(mm == null) {
            mm = new MusicManager(this.playerManager, guild.getId());
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
            
            if(m.getUser().getId().equalsIgnoreCase(vc.getJDA().getSelfUser().getId()))
                hasSelf = true;
            
            if(m.getUser().isBot())
                continue;
            
            user = true;
        }
        
        //if there are no humans left in the voice channel
        if(!user && hasSelf) {
            //the same as running the "pause" command.
            if(!this.isPaused(vc.getGuild())) {
                this.pause(vc.getGuild(), true);
                this.lastChannels.get(vc.getGuild().getId()).sendMessage("The player has been paused as " +
                        "all users have left the voice channel.").queue();
            }
        }
    }

    /**
     * Run when the queue has ended.
     * @param guildID the ID of the guild.
     */
    public void onQueueEnd(String guildID) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Music Player");
        eb.addField("Music Player", "The queue is empty, use the `play` command to play a song.", false);
        this.lastChannels.get(guildID).sendMessage(eb.build()).queueAfter(200, TimeUnit.MILLISECONDS);
    }

    /**
     * Set the last channel.
     * 
     * This is used so the bot will send the next playing song/leave message to this channel.
     * 
     * @param guildID the ID of the guild.
     * @param tc the text channel to send to.
     */
    public void setLast(@NotNull String guildID, @NotNull TextChannel tc) {
        this.lastChannels.put(guildID, tc);
    }
    
    /**
     * Get the amount of currently playing music players
     * @return the amount of currently playing music players
     */
    @Contract(pure = true)
    public int getPlayers() {
        return this.musicManagers.size();
    }

    /**
     * Join a voice channel.
     * @param vc the voice channel to join.
     */
    public void join(@NotNull VoiceChannel vc) {
        vc.getGuild().getAudioManager().openAudioConnection(vc);
    }
    
    public void setVolume(String id, int vol) {
        this.musicManagers.get(id).player.setVolume(vol);
    }

    /**
     * Seek to a specific time.
     * @param id the ID of the guild
     * @param l time to seek to in ms.
     */
    public void seek(@NotNull String id, long l) {
        this.musicManagers.get(id).seek(id, l);
    }
}
