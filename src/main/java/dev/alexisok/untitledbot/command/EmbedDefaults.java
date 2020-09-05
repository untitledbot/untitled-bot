package dev.alexisok.untitledbot.command;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;

/**
 * 
 * Set the defaults for embeds.
 * All plugins should use this for their embeds.
 * 
 * @author AlexIsOK
 * @since 0.0.1
 */
public final class EmbedDefaults {
    
    /**
     * Set the defaults for an embed.  DO NOT PASS A CLONE OF THE OBJECT, this method
     * modifies the parameter object directly.
     * @param eb the embed builder to modify
     * @param message the message received from the command.
     */
    public static void setEmbedDefaults(@NotNull EmbedBuilder eb, @NotNull Message message) {
        eb.setFooter("\n\n\n" + message.getAuthor().getName(), message.getAuthor().getAvatarUrl());
        eb.setTimestamp(Instant.now());
    }
    
}
