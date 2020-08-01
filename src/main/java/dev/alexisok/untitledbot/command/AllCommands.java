package dev.alexisok.untitledbot.command;

import dev.alexisok.untitledbot.plugin.UBPlugin;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jetbrains.annotations.Nullable;

/**
 * Lists all commands to the user.
 * 
 * @author AlexIsOK
 * @since 1.3
 */
public class AllCommands extends UBPlugin {
    
    @Override
    public @Nullable MessageEmbed onCommand(String[] args, Message message) {
        
        message.getAuthor().openPrivateChannel().queue((channel) -> {
            EmbedBuilder eb = new EmbedBuilder();
            
            eb.setTitle("[optional] <required>");
            
            eb.addField("Ranks", "\n" +
                                         "`rank [user @]` - get the rank of yourself or a specific user.\n" +
                                         "`leaderboard` - get the highest ranking users in the guild.\n" +
                                         "`rank-total [user @]` - get the total amount of xp of yourself or another user.", false);
            eb.addField("Utilities", "\n" +
                                             "`help [command]` - get help for a specific command.\n" +
                                             "`prefix <prefix>` - set this guilds prefix.\n" +
                                             "`status` - get the status and statistics of the bot.", false);
            eb.addField("Moderation", "\n" +
                                              "`log-channel <text channel #>` - set the logging channel.\n" +
                                              "`add-log` - add a log type to the log channel.", false);
            
            eb.addField("Note", "\n" +
                                        "This may not be up-to-date, please check https://github.com/alexisok/untitled-bot\n" +
                                        "for the newest commands.", false);
            
            channel.sendMessage(eb.build()).queue();
        });
        
        return null;
    }
    
    @Override
    public void onRegister() {
        CommandRegistrar.register("commands", "core.commands", this);
    }
}
