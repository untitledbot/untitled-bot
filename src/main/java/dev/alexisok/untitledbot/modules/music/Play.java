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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeSearchProvider;

import java.awt.*;
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
    
    @Nullable
    @Override
    public MessageEmbed onCommand(String[] args, @NotNull Message message) {
        //set the last message to the channel so "now playing" messages send there.
        MusicKernel.INSTANCE.setLast(message.getGuild().getId(), message.getTextChannel());
        
        //standard plugin stuff
        EmbedBuilder eb = new EmbedBuilder();
        EmbedDefaults.setEmbedDefaults(eb, message);
        
        
        //unset the pause on the channel so people actually know what's going on with the bot.
//        MusicKernel.INSTANCE.pause(message.getGuild(), false);
        
        if(args.length == 1) {
            eb.addField("Music Player", "Usage: `play <url | search query>`", false);
            eb.setColor(Color.RED);
            return eb.build();
        }
        for(VoiceChannel vc : message.getGuild().getVoiceChannels()) {
            if(vc.getMembers().contains(message.getMember())) {
                if(!Objects.requireNonNull(message.getGuild().getMemberById(Main.jda.getSelfUser().getId())).hasPermission(vc, Permission.VOICE_CONNECT)) {
                    Logger.debug("Could not get access to a voice channel.");
                    break;
                }
                if(!args[1].matches("http(s)?://(www.)?youtu(.be|be.com)*(.+)") ||
                        args[1].matches("http(s)?://(www.)?soundcloud.com/*(.+)")) {
                    message.getChannel().sendTyping().queue();
                    YoutubeSearchResultLoader search = new YoutubeSearchProvider();
                    List<AudioTrackInfo> track = new ArrayList<>();
                    search.loadSearchResult(Arrays.toString(Arrays.copyOfRange(args, 1, args.length)), audioTrackInfo -> {
                        if(track.size() >= 5) {
                            return null;
                        }
                        track.add(audioTrackInfo);
                        return null;
                    });
                    
                    String tracks = "";
                    
                    for(int i = 0; i < 5; i++) {
                        long current = track.get(i).length / 1000;
                        tracks += String.format("%d. `%d:%s` - [**%s** by **%s**](%s)%n",
                                i + 1,
                                current / 60,
                                ((current % 60) + "").length() == 1 ? "0" + (current % 60) : (current % 60),
                                escapeDiscordMarkdown(track.get(i).title),
                                escapeDiscordMarkdown(track.get(i).author),
                                track.get(i).uri);
                    }
                    eb.setTitle("React with 1 through 5 to pick a video.");
                    eb.setDescription(tracks);
                    eb.setColor(Color.GREEN);
                    
                    try {
                        if (RESULTS.containsKey(message.getAuthor().getId()))
                            RESULTS.get(message.getAuthor().getId()).botMessage.delete().queue();
                    } catch(Throwable ignored) {}
                    
                    //send the message, add reactions 1-5, then add it to the queue to be listened to
                    message.getChannel().sendMessage(eb.build()).queue(r -> {
                        r.addReaction("1\uFE0F\u20E3").queue((r2) -> {
                            r.addReaction("2\uFE0F\u20E3").queue(r3 -> {
                                r.addReaction("3\uFE0F\u20E3").queue((r4) -> {
                                    r.addReaction("4\uFE0F\u20E3").queue(r5 -> {
                                        r.addReaction("5\uFE0f\u20E3").queue(owo -> {
                                            RESULTS.put(message.getAuthor().getId(), new ResultsObject(track, message, r));
                                        });
                                    });
                                });
                            });
                        });
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

    @Override
    public void onMessage(GuildMessageReceivedEvent m) {}

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
            
            for(VoiceChannel vc : info.userMessage.getGuild().getVoiceChannels()) {
                if(vc.getMembers().contains(message.getMember())) {
                    MusicKernel.INSTANCE.loadAndPlay(message.getTextChannel(), info.infos.get(toPlay - 1).uri, vc);
                }
            }
            
            info.botMessage.delete().queue();
        }
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
