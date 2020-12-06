package dev.alexisok.untitledbot.modules.basic.snipe;

import dev.alexisok.untitledbot.command.CommandRegistrar;
import dev.alexisok.untitledbot.command.Manual;
import dev.alexisok.untitledbot.command.MessageHook;
import dev.alexisok.untitledbot.command.enums.UBPerm;
import dev.alexisok.untitledbot.modules.moderation.ModHook;
import dev.alexisok.untitledbot.plugin.UBPlugin;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageDeleteEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.HashMap;
import java.util.Objects;

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
        
        Message sniped = null;
        
        try {
            //this is done to prevent getting deleted messages from other channels outside of the guild.
            sniped = ModHook.getMessageByID(SNIPE_CACHE.get(Objects.requireNonNull(message.getGuild().getTextChannelById(target.getId())).getId()));
        } catch(NullPointerException ignored) {}
        
        if(sniped == null) {
            eb.setTitle("Snipe");
            eb.setDescription("No message to snipe.  Please enable the message-update or message-delete logs to use this command.");
            eb.setColor(Color.RED);
            return eb.build();
        }
        SNIPE_CACHE.remove(target.getId());
        
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
        CommandRegistrar.register("snipe", UBPerm.ADMIN, this);
        Manual.setHelpPage("snipe", "Get the last deleted message in this channel or another.");
    }
}
