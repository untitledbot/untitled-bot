package dev.alexisok.untitledbot.modules.music;

import com.neovisionaries.i18n.CountryCode;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeSearchResultLoader;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.specification.Track;
import dev.alexisok.untitledbot.Main;
import dev.alexisok.untitledbot.command.CommandRegistrar;
import dev.alexisok.untitledbot.command.EmbedDefaults;
import dev.alexisok.untitledbot.command.Manual;
import dev.alexisok.untitledbot.command.MessageHook;
import dev.alexisok.untitledbot.command.enums.UBPerm;
import dev.alexisok.untitledbot.logging.Logger;
import dev.alexisok.untitledbot.plugin.UBPlugin;
import lombok.Data;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.interactions.components.Button;
import org.apache.hc.core5.http.ParseException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeSearchProvider;
import org.json.JSONObject;

import java.awt.Color;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.List;

import static dev.alexisok.untitledbot.modules.music.NowPlaying.escapeDiscordMarkdown;

/**
 * Plays music through the bot.
 * 
 * This might need some tlc with sharding and multi-threading.
 * 
 * @author AlexIsOK
 * @since 1.3.23
 */
@SuppressWarnings("CodeBlock2Expr")
public class Play extends UBPlugin implements MessageHook {
    
    private static final HashMap<String, ResultsObject> RESULTS = new HashMap<>();
    
    protected static final String FILTER_REGEX;
    
    private static boolean usingSafeSearch = true;
    
    private static String spotifyToken;
    
    private static SpotifyApi spotifyApi;

    /**
     * Refresh the Spotify Bearer token.
     */
    @Contract
    public static void refreshSpotifyToken(@NotNull String token) throws IOException {
        HttpClient client = HttpClients.createDefault();
        HttpPost post = new HttpPost("https://accounts.spotify.com/api/token?grant_type=client_credentials");
        
        post.setHeader("Content-Type", "application/x-www-form-urlencoded");
        post.setHeader("Accept", "application/json");
        post.setHeader("Authorization", "Basic " + token);
        post.setHeader("Host", "accounts.spotify.com");
//        post.setHeader("Content-Length", "0");
        
        HttpResponse response = client.execute(post);
        HttpEntity entity = response.getEntity();
        
        byte[] bytes = entity.getContent().readAllBytes();
        
        JSONObject object = new JSONObject(new String(bytes, StandardCharsets.UTF_8));
        
        Logger.log("Refreshing Spotify token...");
        
        spotifyApi = new SpotifyApi.Builder()
                .setAccessToken(object.getString("access_token"))
                .build();
    }
    
    static {
        CommandRegistrar.register("1", (args, message) -> {
            if(RESULTS.containsKey(message.getAuthor().getId())) {
                virtualClick(Integer.parseInt(args[0]), RESULTS.get(message.getAuthor().getId()), message);
                try {
                    message.delete().queue();
                } catch(Throwable ignored) {} //if the bot can't delete the message
            }
            return null;
        });
        CommandRegistrar.registerAlias("1", "2", "3", "4", "5");
        //hey you know what this works, not the best idea but it still works, you know?
        
        List<String> bannedWords = new ArrayList<>();
        
        try(BufferedReader br = new BufferedReader(new FileReader("./filters/filters.txt"))) {
            br.lines().forEach(bannedWords::add);
        } catch(IOException e) {
            e.printStackTrace();
            Logger.critical("Could not read from the banned words file!  Safe search has been disabled!");
            usingSafeSearch = false;
        }
        
        StringBuilder tmp = new StringBuilder(".*?(?i)(");
        
        for(int i = 0; i < bannedWords.size(); i++) {
            if(bannedWords.get(i).isEmpty() || bannedWords.get(i).equalsIgnoreCase("\n"))
                continue;
            tmp.append(bannedWords.get(i));
            if(bannedWords.size() - 1 != i)
                tmp.append("|");
        }
        
        tmp.append(").*?");
        
        FILTER_REGEX = tmp.toString();
        
        Properties p = new Properties();
        try {
            p.load(new FileInputStream("./secrets.properties"));
            String key = p.getProperty("spotify.token");
            if(key != null) {
                refreshSpotifyToken(key);
            }
        } catch (IOException e) {
            e.printStackTrace();
            Logger.critical("Could not load the secrets file for SP");
        }
        
        //for testing
        CommandRegistrar.register("test-spotify", UBPerm.OWNER, (args, message) -> {
            try {
                List<String> tracks = youtubeEquivalent(getTracksFromURI(args[1]));
                tracks.forEach((t) -> {
                    message.reply(t).queue();
                });
            } catch(IOException | ParseException | SpotifyWebApiException e) {
                e.printStackTrace();
            }
            return null;
        });
        
        CommandRegistrar.register("test-local", UBPerm.OWNER, (args, message) -> {
            MusicKernel.INSTANCE.loadAndPlay(message.getTextChannel(), "file://local_audio.mp3", Main.getVoiceChannelById(args[1]), message);
            return null;
        });
    }
    
    /**
     * Get a list of YouTube equivalent songs from Spotify.
     * @param simpleTracks the list of simple songs.
     * @return the list of audio track URLs (may be less than the parameter track length)
     */
    @NotNull
    @Contract(pure = true)
    private static List<String> youtubeEquivalent(@NotNull final List<SuperSimpleTrack> simpleTracks) {
        List<String> rt = new ArrayList<>();
        simpleTracks.forEach(superSimpleTrack -> {
            final boolean[] picked = {false};
            new YoutubeSearchProvider().loadSearchResult(superSimpleTrack.author + " " + superSimpleTrack.title, audioTrackInfo -> {
                //if the song was already found
                if(picked[0])
                    return null;
                
                //if the audio track is too long
                if(audioTrackInfo.isStream || audioTrackInfo.length >= 18000000) //10ish hours
                    return null;
                
                //if there is more than a 7 second difference, don't play it.
                if(Math.abs(superSimpleTrack.timeInSeconds - (audioTrackInfo.length / 1000)) > 7)
                    return null;
                
                String simplelc = superSimpleTrack.title.toLowerCase();
                String audiotlc = audioTrackInfo.title.toLowerCase();
                
                //if the author or song title aren't in the youtube video, skip
                if(!audiotlc.contains(simplelc) || !audiotlc.contains(superSimpleTrack.author.toLowerCase()))
                    return null;
                
                //filter any live concerts or anything that might have the
                //same title but isn't the same (unless the original song
                //explicitly states that it's a live version)
                if(!simplelc.contains("live") && audiotlc.contains("live")) return null;
                
                //filter any 8d audio remixes
                if(!simplelc.contains("8d") && audiotlc.contains("8d")) return null;
                
                //other filters
                if(!simplelc.contains("remix") && audiotlc.contains("remix")) return null;
                
                //really don't want this to happen
                if(!simplelc.contains("nightcore") && audiotlc.contains("nightcore")) return null;
                
                picked[0] = true;
                
                rt.add(audioTrackInfo.uri);
                return null;
            });
        });
        
        return rt;
    }
    
    /**
     * Get tracks from the Spotify URI (accepts individual, playlist, and albums)
     * @param URI the URI.
     * @return the list of tracks, may be empty if none found.
     */
    @NotNull
    @Contract(pure = true)
    private static List<SuperSimpleTrack> getTracksFromURI(@NotNull String URI) throws IOException, ParseException, SpotifyWebApiException {
        List<SuperSimpleTrack> rl = new ArrayList<>();
        
        if(!URI.contains("open.spotify.com")) return rl;

        String path = URI.split("open\\.spotify\\.com")[1].split("/")[1]; //type of media
        String ID   = URI.split("open\\.spotify\\.com")[1].split("/")[2].split("\\?")[0]; //id of media
        
        //check the path relative to the base url
        switch(path) {
            case "track": {
                Track t = spotifyApi.getTrack(ID).build().execute();
                rl.add(new SuperSimpleTrack(t.getName(), t.getArtists()[0].getName(), t.getDurationMs() / 1000));
                return rl;
            }
            case "album": {
                var a = spotifyApi.getAlbumsTracks(ID).build().execute();
                Arrays.stream(a.getItems()).forEach(trackSimplified -> {
                    //limit list size
                    if(rl.size() >= 15)
                        return;
                    
                    rl.add(new SuperSimpleTrack(trackSimplified.getName(), trackSimplified.getArtists()[0].getName(), trackSimplified.getDurationMs() / 1000));
                });
                return rl;
            }
            case "playlist": {
                var a = spotifyApi.getPlaylist(ID).build().execute();
                Arrays.stream(a.getTracks().getItems()).forEach(playlistTrack -> {
                    //limit list size
                    if(rl.size() >= 15)
                        return;
                    
                    Track t;
                    try {
                        t = spotifyApi.getTrack(playlistTrack.getTrack().getId()).build().execute();
                    } catch (IOException | SpotifyWebApiException | ParseException e) {
                        return;
                    }
                    rl.add(new SuperSimpleTrack(t.getName(), t.getArtists()[0].getName(), t.getDurationMs() / 1000));
                });
                return rl;
            }
            case "artist": {
                try {
                    var a = spotifyApi.getArtistsTopTracks(ID, CountryCode.US).build().execute();
                    
                    for(int i = 0; i < 5; i++) {
                        rl.add(new SuperSimpleTrack(a[i].getName(), a[i].getArtists()[0].getName(), a[i].getDurationMs() / 1000));
                    }
                    
                    return rl;
                    
                } catch(Exception ignored) {
                    return rl;
                }
            }
            default: return rl;
        }
    }
    
    @Nullable
    @Override
    public MessageEmbed onCommand(String[] args, @NotNull Message message) {
        //set the last message to the channel so "now playing" messages send there.
        MusicKernel.INSTANCE.setLast(message.getGuild().getId(), message.getTextChannel());
        
        //standard plugin stuff
        EmbedBuilder eb = new EmbedBuilder();
        EmbedDefaults.setEmbedDefaults(eb, message);
        
        if(args.length == 1) {
            eb.addField("Music Player", "Usage: `play <YT/Spotify URL or search YT>`", false);
            eb.setColor(Color.RED);
            return eb.build();
        }
        
        //unpause
        if(MusicKernel.INSTANCE.isPlaying(message.getGuild()) && MusicKernel.INSTANCE.isPaused(message.getGuild()))
            MusicKernel.INSTANCE.pause(message.getGuild(), false);
        
        for(VoiceChannel vc : message.getGuild().getVoiceChannels()) {
            if(vc.getMembers().contains(message.getMember())) {
                if(!Objects.requireNonNull(message.getGuild().getMemberById(message.getJDA().getSelfUser().getId())).hasPermission(vc, Permission.VOICE_CONNECT)) {
                    Logger.debug("Could not get access to a voice channel.");
                    break;
                }
                
                //twitch channel or vod
                if(args[1].matches("http(s)?://(www\\.)?twitch.tv/(videos/[0-9]+|[A-Za-z0-9_]{4,25})")) {
                    MusicKernel.INSTANCE.loadAndPlay(message.getTextChannel(), args[1], vc, message);
                    return eb.setTitle("Music Player")
                            .setDescription("The " + (args[1].contains("videos")
                                    ? "VOD has been added to the queue!"
                                    : "stream has been queued!"))
                            .setColor(Color.GREEN)
                            .build();
                }
                
                if(args[1].matches("http(s)?://(www\\.)?vimeo.com/[0-9]+")) {
                    MusicKernel.INSTANCE.loadAndPlay(message.getTextChannel(), args[1], vc, message, true);
                    return eb.setTitle("Music Player")
                            .setDescription("Playing the Vimeo audio.")
                            .setColor(Color.GREEN)
                            .build();
                }
                
                if(args[1].matches("http(s)?://[A-Za-z0-9_-]+\\.bandcamp\\.com/(album|track)/[A-Za-z0-9_-]+")) {
                    MusicKernel.INSTANCE.loadAndPlay(message.getTextChannel(), args[1], vc, message, true);
                    return null;
                }
                
                if(args[1].matches("http(s)?://(www\\.)?open\\.spotify\\.com/(album|track|playlist|artist)/[A-Za-z0-9?=_&:]+")) {
                    message.getChannel().sendTyping().queue();
                    try {
                        List<SuperSimpleTrack> simpleTracks = getTracksFromURI(args[1]);
                        List<String> foundTracks = youtubeEquivalent(simpleTracks);
                        
                        message.getChannel().sendTyping().queue();
                        
                        if(simpleTracks.size() > foundTracks.size()) {
                            message.reply(String.format("Note: %d track(s) were not added as they do not have a YouTube equivalent.", simpleTracks.size() - foundTracks.size()))
                                    .mentionRepliedUser(false)
                                    .queue();
                        }
                        
                        foundTracks.forEach(track -> {
                            MusicKernel.INSTANCE.loadAndPlay(message.getTextChannel(), track, vc, message, false);
                        });
                        
                        return eb.setTitle("Music Player")
                                .setDescription(String.format("Added %d track(s) to the queue from the Spotify URL", foundTracks.size()))
                                .setColor(Color.GREEN)
                                .build();
                    } catch (IOException | ParseException | SpotifyWebApiException e) {
                        e.printStackTrace();
                        
                        return null;
                    }
                }
                
                if(!args[1].matches("http(s)?://(www.)?youtu(.be|be.com)*(.+)") && !args[1].contains("soundcloud.com/")) {
                    message.getChannel().sendTyping().queue();
                    YoutubeSearchResultLoader search = new YoutubeSearchProvider();
                    List<AudioTrackInfo> track = new ArrayList<>();
                    search.loadSearchResult(Arrays.toString(Arrays.copyOfRange(args, 1, args.length)), audioTrackInfo -> {
                        if(track.size() >= 5)
                            return null; //continue
                        if(audioTrackInfo.length >= 18000000)
                            return null;
                        
                        //don't do safe search if the channel is NSFW.
                        if(usingSafeSearch
                                && !((TextChannel) message.getChannel()).isNSFW()
                                && audioTrackInfo.title.matches(FILTER_REGEX)) {
                            return null; //continue
                        }
                        track.add(audioTrackInfo);
                        return null;
                    });
                    
                    StringBuilder tracks = new StringBuilder();
                    
                    if(track.size() == 0) {
                        eb.addField("Could not find any results for this query.", "", false);
                        eb.setColor(Color.RED);
                        return eb.build();
                    }
                    
                    //if the track is an id such as dQw4w9WgXcQ only one or two results will be returned, best to catch this here.
                    if(track.size() != 5) {
                        MusicKernel.INSTANCE.loadAndPlay(message.getTextChannel(), "https://youtube.com/watch?v=" + track.get(0), vc, message);
                        return null;
                    }
                    
                    for(int i = 0; i < 5; i++) {
                        long current = track.get(i).length / 1000;
                        tracks.append(String.format("%d. `%d:%s` - [**%s** by **%s**](%s)%n",
                                i + 1,
                                current / 60,
                                ((current % 60) + "").length() == 1 ? "0" + (current % 60) : (current % 60),
                                escapeDiscordMarkdown(track.get(i).title),
                                escapeDiscordMarkdown(track.get(i).author),
                                track.get(i).uri));
                    }
                    eb.setTitle("Click one of the following buttons or send 1-5 in chat");
                    eb.setDescription(tracks.toString());
                    eb.setColor(Color.GREEN);
                    
                    try {
                        if(RESULTS.containsKey(message.getAuthor().getId()))
                            RESULTS.get(message.getAuthor().getId()).botMessage.delete()
                                    .onErrorMap(throwable -> {
                                        return null; //old message was already deleted.
                                    }).queue();
                    } catch(Throwable e) {
                        e.printStackTrace();
                    }
                    
                    //send the message, add buttons 1-5, then add it to the queue to be listened to
                    message.getChannel().sendMessage(eb.build()).setActionRow(
                            Button.primary("1", "1"),
                            Button.primary("2", "2"),
                            Button.primary("3", "3"),
                            Button.primary("4", "4"),
                            Button.primary("5", "5")
                        ).queue(r -> {
                            RESULTS.put(message.getAuthor().getId(), new ResultsObject(track, message, r)); 
                        }
                    );
                    return null;
                }
                MusicKernel.INSTANCE.loadAndPlay(message.getTextChannel(), args[1], vc, message);
                return null;
            }
        }
        eb.addField("Music Player", "Please join a voice channel to play music.\n" +
                "If you are already in a voice channel, make sure I have access to it.", false);
        eb.setColor(Color.RED);
        return eb.build();
    }
    
    @Override
    public void onRegister() {
        CommandRegistrar.register("play", this);
        Manual.setHelpPage("play", "Play a song from a YouTube/SoundCloud/Spotify URL (or type to search YT).\n\n" +
                "Examples:\n" +
                "```\n" +
                "%splay Me at the zoo\n" +
                "%splay https://www.youtube.com/watch?v=jNQXAC9IVRw\n" +
                "%splay https://soundcloud.com/woaowmusic/metal-crusher-remix\n" +
                "```");
        CommandRegistrar.registerAlias("play", "pl", "p", "load");
        CommandRegistrar.registerHook(new Play());
    }

    /**
     * @param m the mre.
     */
    @Override
    public void onMessage(GuildMessageReceivedEvent m) {
        String content = m.getMessage().getContentRaw();
        if(RESULTS.containsKey(m.getAuthor().getId()) && content.matches("^[1-5]$"))
            virtualClick(Integer.parseInt(content), RESULTS.get(m.getAuthor().getId()), m.getMessage());
    }

    /**
     * Check for message reactions for the numbers 1-5 on the music module.
     * @param e the event
     */
    @Override
    public void onAnyEvent(GenericEvent e) {
        if(e instanceof ButtonClickEvent) {
            if(((ButtonClickEvent) e).getMember().getId().equals(e.getJDA().getSelfUser().getId()))
                return;
            
            if(!RESULTS.containsKey(((ButtonClickEvent) e).getMember().getId()))
                return;
            
            ResultsObject info = RESULTS.get(((ButtonClickEvent) e).getMember().getId());
            
            if(!info.userMessage.getAuthor().getId().equals(((ButtonClickEvent) e).getMember().getId()))
                return;
            
            if(!((ButtonClickEvent) e).getMessageId().equals(info.botMessage.getId()))
                return;
            int label = Integer.parseInt(((ButtonClickEvent) e).getButton().getLabel());
            
            Message message = info.userMessage;
            
            virtualClick(label, info, message);
        }
    }

    /**
     * When a user selects an item 1 - 5, used to make things easier.
     * @param toPlay the number selected.
     */
    private static void virtualClick(int toPlay, ResultsObject info, Message message) {
        if(toPlay < 1 || toPlay > 5)
            throw new IllegalArgumentException("Number must be between 1 and 5 (inclusive).");
        
        //this most likely will not be null but best to check
        if(info == null || message == null) return;
        
        for(VoiceChannel vc : info.userMessage.getGuild().getVoiceChannels()) {
            if(vc.getMembers().contains(message.getMember())) {
                try {
                    MusicKernel.INSTANCE.loadAndPlay(message.getTextChannel(), info.infos.get(toPlay - 1).uri, vc, message);
                } catch(IndexOutOfBoundsException ignored) {}
            }
        }
        
        //delete the original message
        info.botMessage.delete().queue();
        RESULTS.remove(message.getAuthor().getId());
    }
    
    private static class ResultsObject {
        
        private final List<AudioTrackInfo> infos;
        
        private final Message userMessage;
        
        private final Message botMessage;
        
        private ResultsObject(@NotNull List<AudioTrackInfo> infos, @NotNull Message userMessage, @NotNull Message botMessage) {
            this.infos = infos;
            this.userMessage = userMessage;
            this.botMessage = botMessage;
        }
    }
    
    @Data
    private static class SuperSimpleTrack {
        private final String title, author;
        private final int timeInSeconds;
    }
}
