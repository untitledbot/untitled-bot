package dev.alexisok.untitledbot.command;

import dev.alexisok.untitledbot.plugin.UBPlugin;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Lists all commands to the user.
 * 
 * @author AlexIsOK
 * @since 1.3
 */
public class AllCommands extends UBPlugin {
    
    private static final MessageEmbed HELP_MESSAGE;
    
    static {
        EmbedBuilder eb = new EmbedBuilder();
    
        eb.setTitle("untitled-bot commands");
    
        eb.addField("Ranks", "\n" +
                                     "`rank` - get the rank of yourself or a specific user.\n" +
                                     "`leaderboard` - get the highest ranking users in the guild.\n" +
                                     "`rank-total` - get the total amount of xp of yourself or another user.\n" +
                                     "`rank-settings` - rankup and rank boost settings for moderators.\n" +
                                     "", true);
        eb.addField("Utilities", "\n" +
                                         "`help [command]` - get help for a specific command.\n" +
                                         "`prefix <prefix>` - set this guilds prefix.\n" +
                                         "`status` - get the status and statistics of the bot.\n" +
                                         "`shop` - shop items (not fully implemented).\n" +
                                         "`timestamp` - get the timestamp of a Discord snowflake.\n" +
                                         "`uptime` - get the uptime of the bot.\n" +
                                         "`info` - get info about the server or a specific user.\n", true);
        eb.addField("Moderation", "\n" +
                                          "`log-channel` - set the logging channel.\n" +
                                          "`add-log` - add a log type to the log channel.\n" +
                                          "`remove-log` - remove a log type from the channel.\n" +
                                          "`log-types` - list all current log types.\n" +
                                          "`get-log` - list what's being logged (all users).", true);
        eb.addField("Fun", "\n" +
                                   "`someone` - mention a random user without pinging them.\n" +
                                   "`brainfuck` - run brainfuck code" +
                                   " (yes, [it is a real thing](https://en.wikipedia.org/wiki/Brainfuck)).\n" +
                                   "`8ball` - simulate a magic 8 ball.\n" +
                                   "`ship` - ship two users (was asked to add this).\n" +
                                   "`20` - roll a twenty sided die.\n" +
                                   "`owo` - owofy a stwing of text,,,", true);
        eb.addField("Reactions", "\n" +
                                         "`hug` - hug another user (or yourself).\n" +
                                         "", true);
    
        eb.addBlankField(false);
    
        eb.addField("Want something added?", "\n" +
                                                     "If you would like to see another command added, or more features " +
                                                     "on this bot, please [join the Discord server](https://discord.gg/vSWgQ9a) :)", false);
        
        HELP_MESSAGE = eb.build();
    }
    
    @Override
    public @Nullable MessageEmbed onCommand(String[] args, @NotNull Message message) {
        return HELP_MESSAGE;
    }
    
    @Override
    public void onRegister() {
        CommandRegistrar.register("commands", "core.commands", this);
    }
}
