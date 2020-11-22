package dev.alexisok.untitledbot.modules.music;

import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeSearchResultLoader;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import dev.alexisok.untitledbot.Main;
import dev.alexisok.untitledbot.command.CommandRegistrar;
import dev.alexisok.untitledbot.command.EmbedDefaults;
import dev.alexisok.untitledbot.command.Manual;
import dev.alexisok.untitledbot.command.MessageHook;
import dev.alexisok.untitledbot.logging.Logger;
import dev.alexisok.untitledbot.plugin.UBPlugin;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeSearchProvider;

import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
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
            Logger.critical("Could not read from the banned words file!  Safe search has been disabled!", 0, false);
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
            eb.addField("Music Player", "Usage: `play <url | search query>`", false);
            eb.setColor(Color.RED);
            return eb.build();
        }
        
        //unpause
        if(MusicKernel.INSTANCE.isPlaying(message.getGuild()) && MusicKernel.INSTANCE.isPaused(message.getGuild()))
            MusicKernel.INSTANCE.pause(message.getGuild(), false);
        
        for(VoiceChannel vc : message.getGuild().getVoiceChannels()) {
            if(vc.getMembers().contains(message.getMember())) {
                if(!Objects.requireNonNull(message.getGuild().getMemberById(Main.jda.getSelfUser().getId())).hasPermission(vc, Permission.VOICE_CONNECT)) {
                    Logger.debug("Could not get access to a voice channel.");
                    break;
                }
                if(!args[1].matches("http(s)?://(www.)?youtu(.be|be.com)*(.+)")) {
                    message.getChannel().sendTyping().queue();
                    YoutubeSearchResultLoader search = new YoutubeSearchProvider();
                    List<AudioTrackInfo> track = new ArrayList<>();
                    search.loadSearchResult(Arrays.toString(Arrays.copyOfRange(args, 1, args.length)), audioTrackInfo -> {
                        if(track.size() >= 5)
                            return null; //continue
                        if(audioTrackInfo.length >= 7200000)
                            return null;
                        if(usingSafeSearch)
                            if(audioTrackInfo.title.matches(FILTER_REGEX)) {
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
                        MusicKernel.INSTANCE.loadAndPlay(message.getTextChannel(), "https://youtube.com/watch?v=" + track.get(0), vc);
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
                    eb.setTitle("React with or say 1 through 5 to pick a video.");
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
                    
                    //send the message, add reactions 1-5, then add it to the queue to be listened to
                    message.getChannel().sendMessage(eb.build()).queue(r -> {
                        try {
                            RESULTS.put(message.getAuthor().getId(), new ResultsObject(track, message, r));
                            r.addReaction("1\uFE0F\u20E3").queue((r2) -> {
                                r.addReaction("2\uFE0F\u20E3").queue(r3 -> {
                                    r.addReaction("3\uFE0F\u20E3").queue((r4) -> {
                                        r.addReaction("4\uFE0F\u20E3").queue(r5 -> {
                                            r.addReaction("5\uFE0f\u20E3").queue(owo -> {});
                                        });
                                    });
                                });
                            });
                        } catch(InsufficientPermissionException ignored) {} //bot might not be able to add reactions?
                    });
                    return null;
                }
                MusicKernel.INSTANCE.loadAndPlay(message.getTextChannel(), args[1], vc);
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
        Manual.setHelpPage("play", "Play a song from a YouTube URL (search coming soon!).");
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
        if(e instanceof GuildMessageReactionAddEvent) {
            if(((GuildMessageReactionAddEvent) e).getMember().getId().equals(Main.jda.getSelfUser().getId()))
                return;
            if(!RESULTS.containsKey(((GuildMessageReactionAddEvent) e).getMember().getId()))
                return;
            ResultsObject info = RESULTS.get(((GuildMessageReactionAddEvent) e).getMember().getId());
            if(!info.userMessage.getAuthor().getId().equals(((GuildMessageReactionAddEvent) e).getUserId()))
                return;
            MessageReaction reaction = ((GuildMessageReactionAddEvent) e).getReaction();
            if(!reaction.getReactionEmote().isEmoji())
                return;
            if(!((GuildMessageReactionAddEvent) e).getMessageId().equals(info.botMessage.getId()))
                return;
            int toPlay = 0;
            if(reaction.getReactionEmote().getEmoji().equals("1️⃣"))
                toPlay = 1;
            else if(reaction.getReactionEmote().getEmoji().equals("2️⃣"))
                toPlay = 2;
            else if(reaction.getReactionEmote().getEmoji().equals("3️⃣"))
                toPlay = 3;
            else if(reaction.getReactionEmote().getEmoji().equals("4️⃣"))
                toPlay = 4;
            else if(reaction.getReactionEmote().getEmoji().equals("5️⃣"))
                toPlay = 5;
            
            if(toPlay == 0)
                return;
            
            Message message = info.userMessage;
            
            virtualClick(toPlay, info, message);
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
                    MusicKernel.INSTANCE.loadAndPlay(message.getTextChannel(), info.infos.get(toPlay - 1).uri, vc);
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
}
