package dev.alexisok.untitledbot.modules.alexflipnote.api;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * @author AlexIsOK
 * @since 1.3.23
 */
public final class DoImageThingFlip {
    
    private DoImageThingFlip(){}

    /**
     * Generate a {@link net.dv8tion.jda.api.entities.MessageEmbed} with the provided information.
     *
     * @param relativePath the path as it appears relative to https://api.alexflipnote.dev/
     *                     Example: {@code bad?image=%s}
     * @param eb the base embed builder
     * @param message the {@link Message} that was sent.
     * @return the message embed ready to send
     */
    public static synchronized MessageEmbed generateImage(@NotNull String relativePath,
                                                          @NotNull EmbedBuilder eb,
                                                          @NotNull Message message,
                                                          @NotNull String[] args) {

        //jda handles stopping typing
        message.getChannel().sendTyping().queue();

        if(message.getAttachments().size() == 1) {
            if (!message.getAttachments().get(0).isImage()) {
                eb.setTitle("Error");
                eb.setDescription("Please attach an *image* for the file to use.");
                return eb.build();
            }

//            message.getChannel().sendFile();

            String returnString = download(
                    String.format(
                            "https://api.alexflipnote.dev/" + relativePath,
                            message.getAttachments().get(0).getUrl()),
                    message.getId()
            );

            message.getChannel().sendFile(new File(returnString)).queue(r -> new File(returnString).delete());

            return null;
        } else if(message.getMentionedMembers().size() == 1) {
            Member m = message.getGuild().getMemberById(message.getMentionedMembers().get(0).getId());
            if(m != null) {
                String returnString = download(
                        String.format(
                                "https://api.alexflipnote.dev/" + relativePath,
                                m.getUser().getEffectiveAvatarUrl()),
                        message.getId()
                );
                message.getChannel().sendFile(new File(returnString)).queue(r -> new File(returnString).delete());
                return null;
            }
        }
        String returnString = download(
                String.format(
                        "https://api.alexflipnote.dev/" + relativePath,
                        deAnimate(message.getAuthor().getEffectiveAvatarUrl())
                ),
                message.getId()
        );
        message.getChannel().sendFile(new File(returnString)).queue(r -> new File(returnString).delete());
        return null;
    }

    /**
     * Removes an animated avatar
     * @param animatedURL the URL that is animated
     * @return the non-animated URL
     */
    private static String deAnimate(@NotNull String animatedURL) {
        return animatedURL.replace("a_", "").replace("gif", "png");
    }

    @Nullable
    @Contract(pure = true)
    protected static synchronized String download(@NotNull String urlStr, @NotNull String uniqueID) {
        URL url;
        try {
            url = new URL(urlStr);
        } catch(MalformedURLException ignored) {
            return null;
        }
        try(InputStream in = url.openStream()) {
            Files.copy(in, Paths.get("./tmp/" + uniqueID + ".png"), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "./tmp/" + uniqueID + ".png";
    }
}
