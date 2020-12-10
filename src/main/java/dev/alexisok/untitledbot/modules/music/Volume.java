package dev.alexisok.untitledbot.modules.music;

import dev.alexisok.untitledbot.command.CommandRegistrar;
import dev.alexisok.untitledbot.command.EmbedDefaults;
import dev.alexisok.untitledbot.command.Manual;
import dev.alexisok.untitledbot.plugin.UBPlugin;
import dev.alexisok.untitledbot.util.VotedCache;
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
            .setDescription("Please [vote for the bot on Discord Bot List](https://top.gg/bot/730135989863055472/vote) to use this command.\n" +
                    "The reason I vote lock this command is because it is very CPU intensive, and more votes for " +
                    "the bot mean more people use the bot.  It's a win-win (sort of)!\n\n" +
                    "**If you have recently voted for the bot, please wait about 10 seconds and try using this command again.**")
            .setColor(Color.GREEN)
            .build();
    
    @Override
    public @NotNull MessageEmbed onCommand(String[] args, @NotNull Message message) {
        
        if(VotedCache.hasVoted(message.getAuthor().getIdLong(), message.getGuild().getIdLong())) {
            EmbedBuilder eb = new EmbedBuilder();
            EmbedDefaults.setEmbedDefaults(eb, message);
            
            if(args.length == 1 || (args.length == 2 && !args[1].matches("[0-9]{1,3}")))
                return eb.addField("Volume", "The volume must be a value between 0 and 100.", false).setColor(Color.RED).build();
            
            int vol = Integer.parseInt(args[1]);
            
            if(vol < 0 || vol > 100)
                return eb.addField("Volume", "The volume must be a value between 0 and 100.", false).setColor(Color.RED).build();
            
            MusicKernel.INSTANCE.setVolume(message.getGuild().getIdLong(), vol);
            eb.addField("Volume", String.format("Volume has been set to %d.", vol), false);
            eb.setColor(Color.GREEN);
            
            return eb.build();
            
        }
        
        return EMBED;
    }
    
    @Override
    public void onRegister() {
        CommandRegistrar.register("volume", this);
        CommandRegistrar.registerAlias("volume", "vol", "v");
        Manual.setHelpPage("volume", "Set the volume for the bot.\n" +
                "Usage: `%svolume <0 through 100>`\n" +
                "Note: you must vote for the bot to use this command.");
    }
}
