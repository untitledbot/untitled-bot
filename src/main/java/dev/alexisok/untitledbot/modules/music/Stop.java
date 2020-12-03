package dev.alexisok.untitledbot.modules.music;

import dev.alexisok.untitledbot.command.CommandRegistrar;
import dev.alexisok.untitledbot.command.EmbedDefaults;
import dev.alexisok.untitledbot.command.Manual;
import dev.alexisok.untitledbot.plugin.UBPlugin;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

/**
 * @author AlexIsOK
 * @since 1.3.23
 */
public final class Stop extends UBPlugin {

    @Override
    public @NotNull MessageEmbed onCommand(String[] args, @NotNull Message message) {
        MusicKernel.INSTANCE.setLast(message.getGuild().getId(), message.getTextChannel());
        EmbedBuilder eb = new EmbedBuilder();
        EmbedDefaults.setEmbedDefaults(eb, message);
        
        Role dj = MusicKernel.getDJRole(message.getGuild().getId());
        
        if(dj != null && !message.getMember().getPermissions().contains(Permission.MESSAGE_MANAGE)) {
            try {
                if(!message.getMember().getRoles().contains(dj)) {
                    eb.addField("Music Player", "You must have the DJ Role <@&" + dj.getId() + "> to do this!", false);
                    eb.setColor(Color.RED);
                    return eb.build();
                }
            } catch(Throwable ignored) {}
        }
        
        try {
            MusicKernel.INSTANCE.stop(message.getGuild());
        } catch(Throwable ignored) {
            eb.addField("Stop", "Could not stop the player.", false);
            eb.setColor(Color.RED);
            return eb.build();
        }
        
        eb.addField("Stop", "Player has been stopped and queue has been cleared.", false);
        eb.setColor(Color.GREEN);
        
        return eb.build();
    }

    @Override
    public void onRegister() {
        CommandRegistrar.register("stop", this);
        Manual.setHelpPage("stop", "Stop the currently playing song.");
        CommandRegistrar.registerAlias("stop", "die", "destroy", "reeeee");
    }
}
