package dev.alexisok.untitledbot.modules.music;

import dev.alexisok.untitledbot.Main;
import dev.alexisok.untitledbot.command.CommandRegistrar;
import dev.alexisok.untitledbot.command.EmbedDefaults;
import dev.alexisok.untitledbot.plugin.UBPlugin;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

/**
 * @author AlexIsOK
 * @since 1.3.23
 */
public final class Volume extends UBPlugin {
    
    private static final MessageEmbed EMBED = new EmbedBuilder()
            .setTitle("Volume")
            .setDescription("To set the volume for the bot, please manually adjust the volume on my slider for Discord.\n\n" +
                    "The reason for this is that changing the volume from 100% causes heavy CPU usage on the server end.\n" +
                    "See [this link](https://github.com/sedmelluq/lavaplayer/issues/465) for more info.")
            .setColor(Color.RED)
            .setImage("https://media.discordapp.net/attachments/732614175523602552/774483063342104606/Screenshot_from_2020-11-06_20-16-12.png")
            .build();
    
    @Override
    public @NotNull MessageEmbed onCommand(String[] args, @NotNull Message message) {
        
        if(Main.CONTRIBUTORS.contains(message.getAuthor().getId())) {
            EmbedBuilder eb = new EmbedBuilder();
            EmbedDefaults.setEmbedDefaults(eb, message);
            
            if(args.length == 1 || (args.length == 2 && !args[1].matches("[0-9]{1,3}")))
                return eb.addField("Volume", "The volume must be a value between 0 and 100.", false).setColor(Color.RED).build();
            
            int vol = Integer.parseInt(args[1]);
            
            if(vol < 0 || vol > 100)
                return eb.addField("Volume", "The volume must be a value between 0 and 100.", false).setColor(Color.RED).build();
            
            MusicKernel.INSTANCE.setVolume(message.getGuild().getId(), vol);
            eb.addField("Volume", "Volume has been set.  Thank you for being a contributor to untitled-bot!", false);
            eb.setColor(Color.GREEN);
            
            return eb.build();
            
        }
        
        return EMBED;
    }
    
    @Override
    public void onRegister() {
        CommandRegistrar.register("volume", this);
        CommandRegistrar.registerAlias("volume", "vol", "v");
    }
}
