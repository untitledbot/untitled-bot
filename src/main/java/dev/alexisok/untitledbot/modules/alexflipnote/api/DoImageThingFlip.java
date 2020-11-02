package dev.alexisok.untitledbot.modules.alexflipnote.api;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jetbrains.annotations.NotNull;

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
        
        if(message.getAttachments().size() == 1) {
            if (!message.getAttachments().get(0).isImage()) {
                eb.setTitle("Error");
                eb.setDescription("Please attach an *image* for the file to use.");
                return eb.build();
            }
            
            eb.setImage(String.format("https://api.alexflipnote.dev/" + relativePath, message.getAttachments().get(0).getUrl()));
            return eb.build();
        } else if(message.getMentionedMembers().size() == 1) {
            Member m = message.getGuild().getMemberById(message.getMentionedMembers().get(0).getId());
            if(m != null) {
                eb.setImage(String.format("https://api.alexflipnote.dev/" + relativePath, deAnimate(m.getUser().getEffectiveAvatarUrl())));
                return eb.build();
            }
        }
        eb.setImage(String.format("https://api.alexflipnote.dev/" + relativePath, deAnimate(message.getAuthor().getEffectiveAvatarUrl())));
        return eb.build();
    }

    /**
     * Removes an animated avatar
     * @param animatedURL the URL that is animated
     * @return the non-animated URL
     */
    private static String deAnimate(@NotNull String animatedURL) {
        return animatedURL.replace("a_", "").replace("gif", "png");
    }
    
}
