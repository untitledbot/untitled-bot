package dev.alexisok.untitledbot.modules.basic.vcsnipe;

import dev.alexisok.untitledbot.BotClass;
import dev.alexisok.untitledbot.command.CommandRegistrar;
import dev.alexisok.untitledbot.command.EmbedDefaults;
import dev.alexisok.untitledbot.command.Manual;
import dev.alexisok.untitledbot.command.MessageHook;
import dev.alexisok.untitledbot.plugin.UBPlugin;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.HashMap;

public class SnipeVC extends UBPlugin implements MessageHook {
    
    //                           vcID, userID
    private static final HashMap<Long, Long> SNIPES = new HashMap<>();
    
    @Override
    public @NotNull MessageEmbed onCommand(String[] args, @NotNull Message message) {
        EmbedBuilder eb = new EmbedBuilder();
        EmbedDefaults.setEmbedDefaults(eb, message);
        
        try {
            VoiceChannel ch = message.getMember().getVoiceState().getChannel();
            
            if(!message.getGuild().getVoiceChannels().contains(ch)) {
                throw new Exception();
            }
            
            if(!SNIPES.containsKey(ch.getIdLong())) {
                eb.addField("Snipe VC", "Nothing to snipe for " + ch.getName(), false);
                eb.setColor(Color.RED);
                return eb.build();
            }
            
            long userID = SNIPES.get(ch.getIdLong());
            
            eb.addField("Snipe VC", String.format("The last user to leave the voice chat is <@%d> in voice channel %s", userID, ch.getName()), false);
            eb.setColor(Color.GREEN);
            
            return eb.build();
        } catch(Exception ignored) {
            eb.addField("Snipe VC", "You must be in a voice channel to use this command!", false);
            eb.setColor(Color.RED);
            return eb.build();
        }
        
    }
    
    @Override
    public void onRegister() {
        CommandRegistrar.register("snipe-vc", this);
        Manual.setHelpPage("snipe-vc", "Gets the last user that left the voice chat you are in.\n" +
                "Usage: `snipe-vc`.\n" +
                "You must be in a voice chat to use this command.");
        CommandRegistrar.registerAlias("snipe-vc", "vc-snipe", "snipevc", "vcsnipe");
        
        CommandRegistrar.registerHook(this);
    }
    
    @Override
    public void onMessage(GuildMessageReceivedEvent m) {}
    
    @Override
    public void onAnyEvent(GenericEvent e) {
        if(e instanceof GuildVoiceLeaveEvent) {
            GuildVoiceLeaveEvent q = (GuildVoiceLeaveEvent) e;
            
            SNIPES.put(q.getChannelLeft().getIdLong(), q.getMember().getIdLong());
        }
    }
}
