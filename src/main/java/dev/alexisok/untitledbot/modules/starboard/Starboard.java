package dev.alexisok.untitledbot.modules.starboard;

import dev.alexisok.untitledbot.Main;
import dev.alexisok.untitledbot.logging.Logger;
import dev.alexisok.untitledbot.modules.vault.Vault;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 
 * 
 * @author AlexIsOK
 * @since 1.4.0
 */
public final class Starboard extends ListenerAdapter {
    
    private static final HashMap<String, Boolean> STARBOARD_ENABLED_CACHE = new HashMap<>();
    private static final HashMap<String, String>  STARBOARD_CHANNEL_CACHE = new HashMap<>();
    
    /**
     * Void the starboard cache for a guild in case it is updated.
     * @param guildID the guild ID
     */
    public static void voidCacheForGuild(String guildID) {
        STARBOARD_ENABLED_CACHE.remove(guildID);
        STARBOARD_CHANNEL_CACHE.remove(guildID);
    }
    
    @Override
    public void onGuildMessageReactionAdd(@Nonnull GuildMessageReactionAddEvent e) {
        Logger.debug("Guild message emote added.");
        boolean shouldRun;
        if(!STARBOARD_ENABLED_CACHE.containsKey(e.getGuild().getBannerId())) {
            shouldRun = Vault.getUserDataLocalOrDefault(null, e.getGuild().getId(), "starboard", "false").equals("true");
            STARBOARD_ENABLED_CACHE.put(e.getGuild().getId(), shouldRun);
        } else {
            shouldRun = STARBOARD_ENABLED_CACHE.getOrDefault(e.getGuild().getId(), false);
        }
        
        if(shouldRun) {
            Logger.debug("Stuff and things");
            try {
                if(e.getReactionEmote().getEmoji().equals("\u2B50")) {
                    int count = Integer.parseInt(Vault.getUserDataLocalOrDefault(null, e.getGuild().getId(), "starboard.threshold", "10"));
                    ArrayList<MessageReaction> reactions = new ArrayList<>(e.getChannel().retrieveMessageById(e.getMessageId()).complete().getReactions());
                    reactions.removeIf(messageReaction1 -> !messageReaction1.getReactionEmote().getEmoji().equals("\u2B50"));
                    if(reactions.get(0).getCount() >= count) {
                        Message a = e.getChannel().retrieveMessageById(e.getMessageId()).complete();
                        pinMessage(e.getGuild().getId(), a);
                    }
                }
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
    }
    
    /**
     * "Pin" a message to the starboard.
     * @param guildID the ID of the guild.
     */
    private static void pinMessage(@NotNull String guildID, @NotNull Message linkedMessage) {
        Logger.debug("Pinning the message");
        
        String channelID = Vault.getUserDataLocalOrDefault(null, guildID, "starboard.channel", "none");
        
        if(channelID.equals("none")) return;
    
        TextChannel tc = Main.jda.getTextChannelById(channelID);
        
        if(tc == null) return;
        
        if(!tc.canTalk()) return;
    
        EmbedBuilder eb = new EmbedBuilder();
    
        try {
            eb.setImage(linkedMessage.getAttachments().get(0).getUrl());
        } catch(Throwable ignored) {}
        
        String message = linkedMessage.getContentRaw();
        message = message.substring(0, Math.min(message.length(), 1900)); //1900 to be safe TODO test this
        
        
        eb.setAuthor(linkedMessage.getAuthor().getAvatarUrl());
        eb.setTimestamp(linkedMessage.getTimeCreated());
        eb.setColor(Color.BLUE);
        eb.setTitle(linkedMessage.getAuthor().getName() + "#" + linkedMessage.getAuthor().getDiscriminator());
        eb.setDescription(message);
        
        try {
            tc.sendMessage(eb.build()).queue();
        } catch(Throwable ignored) {}
    }
    
    @Override
    public void onGuildMessageReactionRemove(@Nonnull GuildMessageReactionRemoveEvent e) {
        
    }
}
