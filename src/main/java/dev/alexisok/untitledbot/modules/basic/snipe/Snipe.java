package dev.alexisok.untitledbot.modules.basic.snipe;

import dev.alexisok.untitledbot.command.CommandRegistrar;
import dev.alexisok.untitledbot.command.Manual;
import dev.alexisok.untitledbot.command.MessageHook;
import dev.alexisok.untitledbot.modules.moderation.ModHook;
import dev.alexisok.untitledbot.plugin.UBPlugin;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageDeleteEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author AlexIsOK
 * @since 1.3.22
 */
public final class Snipe extends UBPlugin implements MessageHook {
    
    //channel id, message id
    private static final HashMap<String, String> SNIPE_CACHE = new HashMap<>();
    
    {
        CommandRegistrar.registerHook(this);
    }
    
    @Override
    public @NotNull MessageEmbed onCommand(String[] args, @NotNull Message message) {
        EmbedBuilder eb = new EmbedBuilder();

        TextChannel target = message.getMentionedChannels().size() == 1 ? message.getMentionedChannels().get(0) : message.getTextChannel();
        
        if(!SNIPE_CACHE.containsKey(target.getId())) {
            eb.setTitle("Snipe");
            eb.setDescription("No message to snipe.");
            eb.setColor(Color.RED);
            return eb.build();
        }
        
        Message sniped = ModHook.getMessageByID(SNIPE_CACHE.get(target.getId()));
        
        if(sniped == null) {
            eb.setTitle("Snipe");
            eb.setDescription("No message to snipe.");
            eb.setColor(Color.RED);
            return eb.build();
        }
        SNIPE_CACHE.remove(target.getId());
        
        if(sniped.getContentRaw().contains("discord.gg")) {
            eb.addField("Snipe", "Sniped message might contain an invite link!", false);
            return eb.build();
        }
        
        eb.setTitle("Snipe");
        eb.setDescription(sniped.getContentRaw());
        
        eb.addField("Info", String.format("" +
                "Sent by %s (%s)%n" +
                "ID: %s%n" +
                "" +
                "%nNote: the message has been removed from the snipe cache.",
                sniped.getAuthor().getAsMention(), sniped.getAuthor().getAsTag(),
                sniped.getAuthor().getId()), false);
        eb.setColor(Color.RED);
        return eb.build();
    }
    
    @Override
    public void onMessage(GuildMessageReceivedEvent m) {}
    
    @Override
    public void onAnyEvent(GenericEvent e) {
        if(e instanceof GuildMessageDeleteEvent) {
            GuildMessageDeleteEvent a = (GuildMessageDeleteEvent) e;
            SNIPE_CACHE.put(a.getChannel().getId(), a.getMessageId());
        }
    }

    @Override
    public void onRegister() {
        CommandRegistrar.register("snipe", this);
        Manual.setHelpPage("snipe", "Get the last deleted message in this channel or another.");
    }
}
