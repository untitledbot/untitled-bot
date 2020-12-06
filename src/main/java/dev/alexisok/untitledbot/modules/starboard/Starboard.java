package dev.alexisok.untitledbot.modules.starboard;

import dev.alexisok.untitledbot.BotClass;
import dev.alexisok.untitledbot.logging.Logger;
import dev.alexisok.untitledbot.util.vault.Vault;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
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
    
    //message ID, message sent
    private static final HashMap<String, Message> TO_UPDATE_CACHE = new HashMap<>();
    
    static {
        Logger.log("Checking to see if the stars directory exists...");
        
        Logger.log("Starboard directory creation returned " + new File(STARBOARD_DIR).mkdir());
        Logger.log("...I have no idea what that means, just check if there is a directory named 'stars' if you're having problems.");
    }
    
    /**
     * Void the starboard cache for a guild in case it is updated.
     * @param guildID the guild ID
     */
    public static synchronized void voidCacheForGuild(String guildID) {
        STARBOARD_ENABLED_CACHE.remove(guildID);
    }

    /**
     * Void the entire cache without using guild IDs.
     */
    public static synchronized void voidAllCache() {
        STARBOARD_ENABLED_CACHE.clear();
    }
    
    
    @NotNull
    @Contract(pure = true)
    private static synchronized String getDiscordLink(@NotNull String guildID, @NotNull String channelID, @NotNull String messageID) {
        return String.format("https://discord.com/channels/%s/%s/%s", guildID, channelID, messageID);
    }
    
    @Override
    public synchronized void onGuildMessageReactionAdd(@Nonnull GuildMessageReactionAddEvent e) {
        boolean shouldRun;
        if(!STARBOARD_ENABLED_CACHE.containsKey(e.getGuild().getId())) {
            shouldRun = Vault.getUserDataLocalOrDefault(null, e.getGuild().getId(), "starboard", "false").equals("true");
            STARBOARD_ENABLED_CACHE.put(e.getGuild().getId(), shouldRun);
        } else {
            shouldRun = STARBOARD_ENABLED_CACHE.getOrDefault(e.getGuild().getId(), false);
        }
        
        
        if(TO_UPDATE_CACHE.containsKey(e.getMessageId())) {
            Logger.debug("Updating starboard message...");
            Message old = TO_UPDATE_CACHE.get(e.getMessageId());
            if(e.getReactionEmote().isEmoji() && e.getReactionEmote().getEmoji().equals("\u2B50")) {
                e.getChannel().retrieveMessageById(e.getMessageId()).queue(consumer -> {
                    //remove any reactions from the List that are not :star: (does not remove them on Discord)
                    ArrayList<MessageReaction> reactions = new ArrayList<>(consumer.getReactions());
                    reactions.removeIf(messageReaction1 -> !messageReaction1.getReactionEmote().isEmoji());
                    reactions.removeIf(messageReaction1 -> !messageReaction1.getReactionEmote().getEmoji().equals("\u2B50"));
                    String emojiToAdd = "\u2B50";
                    if(!reactions.get(0).hasCount())
                        return;
                    int count = reactions.get(0).getCount();
                    if(count > 6)
                        emojiToAdd = "\uD83C\uDF1F";
                    if(count > 12)
                        emojiToAdd = "\u2728";
                    if(count > 18)
                        emojiToAdd = "\uD83D\uDCAB";
                    old.editMessage(
                            new MessageBuilder().setEmbed(old.getEmbeds().get(0))
                                    .setContent(emojiToAdd + " " + count).build()
                    ).queue();
                });
            }
            return;
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
                        reactions.removeIf(messageReaction1 -> !messageReaction1.getReactionEmote().isEmoji());
                        reactions.removeIf(messageReaction1 -> !messageReaction1.getReactionEmote().getEmoji().equals("\u2B50"));
                        //check conditions
                        if(reactions.get(0).getCount() >= count) {
                            //pin the message...
                            e.getChannel().retrieveMessageById(e.getMessageId()).queue(consumer1 -> pinMessage(e.getGuild().getId(), consumer1, count));
                        }
                    });
                }
            } catch (Throwable ignored) {}
        }
    }
    
    /**
     * "Pin" a message to the starboard.
     * @param guildID the ID of the guild.
     * @param linkedMessage the message to be pinned
     * @param count the amount of :star: emotes.
     */
    private static synchronized void pinMessage(@NotNull String guildID, @NotNull Message linkedMessage, int count) {
        
        try {
            if(FileUtils.readFileToString(new File(STARBOARD_DIR + linkedMessage.getGuild().getId() + ".star"), StandardCharsets.UTF_8).contains(linkedMessage.getId()))
                return;
        } catch(IOException ignored) {}
        
        String channelID = Vault.getUserDataLocalOrDefault(null, guildID, "starboard.channel", "none");
        
        if(channelID.equals("none")) return;
    
        TextChannel tc = linkedMessage.getJDA().getTextChannelById(channelID);
        
        if(tc == null) return;
        
        if(!tc.canTalk()) return;
    
        EmbedBuilder eb = new EmbedBuilder();
        
        addMessageIDToFile(linkedMessage);
        
        try {
            eb.setImage(linkedMessage.getAttachments().get(0).getUrl());
        } catch(Throwable ignored) {}
        
        String message = linkedMessage.getContentRaw();
        message = message.substring(0, Math.min(message.length(), 1999)); //1999 to be safe
        
        if(linkedMessage.getEmbeds().size() != 0) {
            String description = linkedMessage.getEmbeds().get(0).getDescription();
            if(description != null && description.length() != 0) {
                message = description;
            }
            
        }
        
        eb.setDescription(message);
        
        eb.setTimestamp(linkedMessage.getTimeCreated());
        eb.setColor(Color.BLUE);
        eb.setTitle( //set the title to the authors name and then link it to the message
                linkedMessage.getAuthor().getName() + "#" + linkedMessage.getAuthor().getDiscriminator()
        );
        eb.addField("", "[Message Link](" +
                getDiscordLink (
                        linkedMessage.getGuild().getId(),
                        linkedMessage.getChannel().getId(),
                        linkedMessage.getId()
                ) + ")", false);
        eb.setFooter("\n\n\n" + linkedMessage.getAuthor().getName(), linkedMessage.getAuthor().getAvatarUrl());
        
        MessageBuilder b = new MessageBuilder();
        b.setContent("\u2B50 " + count);
        b.setEmbed(eb.build());
        
        try {
            tc.sendMessage(b.build()).queue(consumer -> {
                BotClass.addToDeleteCache(linkedMessage.getId(), consumer);
                TO_UPDATE_CACHE.put(linkedMessage.getId(), consumer);
                addMessageIDToFile(consumer);
                consumer.addReaction("U+2B50").queue();
            });
        } catch(Throwable ignored) {}
    }
    
    private static synchronized void addMessageIDToFile(@NotNull Message m) {
        File f = new File(STARBOARD_DIR + m.getGuild().getId() + ".star");
        try {
            if(!f.exists()) {
                if(!f.createNewFile())
                    throw new IOException();
            }
            Files.write(Paths.get(f.toURI()), ('\n' + m.getId()).getBytes(), StandardOpenOption.APPEND);
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void onGuildMessageReactionRemove(@Nonnull GuildMessageReactionRemoveEvent e) {
        
    }
}
