package dev.alexisok.untitledbot.command;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import org.jetbrains.annotations.NotNull;

/**
 * 
 * Set the defaults for embeds.
 * 
 * @author AlexIsOK
 * @since 0.0.1
 */
public class EmbedDefaults {
    
    /**
     * Set the defaults for an embed.  DO NOT PASS A CLONE OF THE OBJECT, this method
     * modifies the parameter object directly.
     * @param eb the embed builder to modify
     * @param m the message received from the command.
     */
    public static void setEmbedDefaults(@NotNull EmbedBuilder eb, @NotNull Message m) {
        eb.setAuthor(m.getAuthor().getName(), m.getAuthor().getAvatarUrl(), m.getAuthor().getAvatarUrl());
        eb.setTitle("untitled-bot");
    }
    
}
