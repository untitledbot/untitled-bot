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
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * 
 * 
 * @author AlexIsOK
 * @since 1.4.0
 */
public final class Starboard extends ListenerAdapter {
    
    //speed ups
    private static final HashMap<String, Boolean> STARBOARD_ENABLED_CACHE = new HashMap<>();
    
    private static final String STARBOARD_DIR = "./stars/";
    
    static {
        Logger.log("Checking to see if the stars directory exists...");
        
        Logger.log("Starboard directory creation returned " + new File(STARBOARD_DIR).mkdir());
        Logger.log("...I have no idea what that means, just check if there is a directory named 'stars' if you're having problems.");
    }
    
    /**
     * Void the starboard cache for a guild in case it is updated.
     * @param guildID the guild ID
     */
    public static void voidCacheForGuild(String guildID) {
        STARBOARD_ENABLED_CACHE.remove(guildID);
    }
    
    @Override
    public void onGuildMessageReactionAdd(@Nonnull GuildMessageReactionAddEvent e) {
        boolean shouldRun;
        if(!STARBOARD_ENABLED_CACHE.containsKey(e.getGuild().getBannerId())) {
            shouldRun = Vault.getUserDataLocalOrDefault(null, e.getGuild().getId(), "starboard", "false").equals("true");
            STARBOARD_ENABLED_CACHE.put(e.getGuild().getId(), shouldRun);
        } else {
            shouldRun = STARBOARD_ENABLED_CACHE.getOrDefault(e.getGuild().getId(), false);
        }
        
        if(shouldRun) {
            try {
                //oh no
                //if the emote is not :star:
                if(e.getReactionEmote().getEmoji().equals("\u2B50")) {
                    //get the required amount of the emote
                    int count = Integer.parseInt(Vault.getUserDataLocalOrDefault(null, e.getGuild().getId(), "starboard.threshold", "10"));
                    //retrieve the message and queue it to check
                    e.getChannel().retrieveMessageById(e.getMessageId()).queue(consumer -> {
                        //remove any reactions from the List that are not :star: (does not remove them on Discord)
                        ArrayList<MessageReaction> reactions = new ArrayList<>(consumer.getReactions());
                        reactions.removeIf(messageReaction1 -> !messageReaction1.getReactionEmote().getEmoji().equals("\u2B50"));
                        //check conditions
                        if(reactions.get(0).getCount() >= count) {
                            //pin the message...
                            e.getChannel().retrieveMessageById(e.getMessageId()).queue(consumer1 -> pinMessage(e.getGuild().getId(), consumer1));
                        }
                    });
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
        String channelID = Vault.getUserDataLocalOrDefault(null, guildID, "starboard.channel", "none");
        
        if(channelID.equals("none")) return;
    
        TextChannel tc = Main.jda.getTextChannelById(channelID);
        
        if(tc == null) return;
        
        if(!tc.canTalk()) return;
    
        EmbedBuilder eb = new EmbedBuilder();
        
        addMessageIDToFile(linkedMessage);
        
        try {
            eb.setImage(linkedMessage.getAttachments().get(0).getUrl());
        } catch(Throwable ignored) {}
        
        String message = linkedMessage.getContentRaw();
        message = message.substring(0, Math.min(message.length(), 1999)); //1999 to be safe
    
        eb.setTimestamp(linkedMessage.getTimeCreated());
        eb.setColor(Color.BLUE);
        eb.setTitle(linkedMessage.getAuthor().getName() + "#" + linkedMessage.getAuthor().getDiscriminator());
        eb.setFooter("\n\n\n" + linkedMessage.getAuthor().getName(), linkedMessage.getAuthor().getAvatarUrl());
        eb.setDescription(message);
        
        try {
            tc.sendMessage(eb.build()).queue();
        } catch(Throwable ignored) {}
    }
    
    private static void addMessageIDToFile(@NotNull Message m) {
        File f = new File(STARBOARD_DIR + m.getGuild().getId() + ".star");
        try {
            if(!f.exists()) {
                if(!f.createNewFile())
                    throw new IOException();
            }
            Files.write(Paths.get(f.toURI()), m.getId().getBytes(), StandardOpenOption.APPEND);
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void onGuildMessageReactionRemove(@Nonnull GuildMessageReactionRemoveEvent e) {
        
    }
}
