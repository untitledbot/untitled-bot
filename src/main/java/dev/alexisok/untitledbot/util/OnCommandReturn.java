package dev.alexisok.untitledbot.util;

import net.dv8tion.jda.api.entities.MessageEmbed;

/**
 * Used by {@link dev.alexisok.untitledbot.command.CommandRegistrar}
 * 
 * @author AlexIsOK
 * @since 1.3.25
 */
public interface OnCommandReturn {
    void onReturn(MessageEmbed embed);
}
