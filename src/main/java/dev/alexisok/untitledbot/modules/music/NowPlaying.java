package dev.alexisok.untitledbot.modules.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import dev.alexisok.untitledbot.Main;
import dev.alexisok.untitledbot.command.CommandRegistrar;
import dev.alexisok.untitledbot.command.EmbedDefaults;
import dev.alexisok.untitledbot.command.Manual;
import dev.alexisok.untitledbot.logging.Logger;
import dev.alexisok.untitledbot.plugin.UBPlugin;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.stream.IntStream;

/**
 * @author AlexIsOK
 * @since 1.3.23
 */
public final class NowPlaying extends UBPlugin {
    
    @Override
    public @NotNull MessageEmbed onCommand(String[] args, @NotNull Message message) {
        EmbedBuilder eb = new EmbedBuilder();
        EmbedDefaults.setEmbedDefaults(eb, message);
        
        if(!MusicKernel.INSTANCE.isPlaying(message.getGuild())) {
            eb.addField("Now Playing", "Doesn't look like anything is playing...", false);
            eb.setColor(Color.RED);
            return eb.build();
        }
        
        long max = MusicKernel.INSTANCE.nowPlaying(message.getGuild()).getDuration() / 1000;
        long current = MusicKernel.INSTANCE.nowPlaying(message.getGuild()).getPosition() / 1000;
        String title = MusicKernel.INSTANCE.nowPlaying(message.getGuild()).getInfo().title;
        String author = MusicKernel.INSTANCE.nowPlaying(message.getGuild()).getInfo().author;
        String url = MusicKernel.INSTANCE.nowPlaying(message.getGuild()).getInfo().uri;
        eb.setTitle("Now Playing", url);
        eb.setDescription(String.format("" +
                "Playing **%s** by %s%n" +
                "Time: %d:%s / %d:%s%n" +
                "%s",
                escapeDiscordMarkdown(title), escapeDiscordMarkdown(author),
                current / 60,
                ((current % 60) + "").length() == 1 ? "0" + (current % 60) : (current % 60), //change things like 1:2 to 1:02
                max / 60,
                ((max % 60) + "").length() == 1 ? "0" + (max % 60) : (max % 60), getProgressBar((double) current / (double) max))); //change things like 1:2 to 1:02
        eb.setColor(Color.GREEN);
        return eb.build();
    }
    
    public static synchronized String current(@NotNull Guild g) {
        long current = MusicKernel.INSTANCE.nowPlaying(g).getPosition() / 1000;

        return ((current % 60) + "").length() == 1 ? "0" + (current % 60) : String.valueOf((current % 60)); //change things like 1:2 to 1:02;
    }
    
    public static synchronized String max(@NotNull Guild g) {
        long current = MusicKernel.INSTANCE.nowPlaying(g).getDuration() / 1000;
        
        return ((current % 60) + "").length() == 1 ? "0" + (current % 60) : String.valueOf((current % 60)); //change things like 1:2 to 1:02;
    }
    
    /**
     * Get the progress as a number of the given song provided the Guild.
     * @param g the Guild to get.
     * @return the progress as an integer between 0 and 100 (inclusive), or {@code -1} if there is nothing playing.
     */
    public static synchronized int getProgress(@NotNull Guild g) {
        if(!MusicKernel.INSTANCE.isPlaying(g))
            return -1;
        
        AudioTrack track;
        try {
            track = MusicKernel.INSTANCE.nowPlaying(g);
            if(track == null)
                return -1;
        } catch(Exception ignored) {
            return -1;
        }
        
        double progress = (track.getPosition() / 1000.0) / (track.getDuration() / 1000.0);
        
        return (int) progress * 100;
    }
    
    /**
     * Get the progress as a number of the given song provided the Guild.
     * @param guildID the ID of the guild to get.
     * @return the progress as an integer between 0 and 100 (inclusive), or {@code -1} if there is nothing playing or the guild does not exist.
     */
    public static synchronized int getProgress(@NotNull String guildID) {
        Guild g = Main.getGuildFromID(guildID);
        
        if(g == null)
            return -1;
        
        return getProgress(g);
    }
    
    /**
     * Get the progress as a number of the given song provided the Guild.
     * @param guildID the ID of the guild to get.
     * @return the progress as an integer between 0 and 100 (inclusive), or {@code -1} if there is nothing playing or the guild does not exist.
     */
    public static synchronized int getProgress(long guildID) {
        return getProgress(String.valueOf(guildID));
    }
    
    /**
     * Make a nice little progress bar from a double
     * @param progress the progress as a value between 0 and 1
     * @return the bar
     */
    private static synchronized String getProgressBar(double progress) {
        if(progress >= 1)
            progress = 1;
        if(progress <= 0)
            progress = 0;
        
        StringBuilder rs = new StringBuilder("```\n");
        
        int prg = (int) (progress * 100 / 2);
        int left = 50 - prg;
        
        IntStream.range(0, prg - 1).mapToObj(i -> "▓").forEach(rs::append);
        
        rs.append("▓");
        
        IntStream.range(0, left).mapToObj(i -> "░").forEach(rs::append);

        rs.append("  ").append(String.format("%.0f", progress * 100)).append("%");
        
        rs.append("\n```");
        return rs.toString();
    }
    
    @NotNull
    @Contract(pure = true)
    public static String escapeDiscordMarkdown(@NotNull String toEscape) {
        toEscape = toEscape.replaceAll("\\*", "\\*");
        toEscape = toEscape.replaceAll("_", "\\_");
        toEscape = toEscape.replaceAll("\\|", "\\|");
        return toEscape.replaceAll("~", "\\~");
    }
    
    @Override
    public void onRegister() {
        CommandRegistrar.register("nowplaying", this);
        Manual.setHelpPage("nowplaying", "Get info on the currently playing video.");
        CommandRegistrar.registerAlias("nowplaying", "playing", "np", "whatsthis");
    }
}
