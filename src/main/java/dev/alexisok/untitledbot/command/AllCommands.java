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
    
        Manual.setHelpPage("leveling", "\n" +
                                     "`rank` - get the rank of yourself or a specific user.\n" +
                                     "`leaderboard` - get the highest ranking users in the guild.\n" +
                                     "`rank-total` - get the total amount of xp of yourself or another user.\n" +
                                     "`rank-settings` - rankup and rank boost settings for moderators. (adm)\n" +
                                     "`rank-role` - assign rank roles for users when they level up. (adm)\n" +
                                     "`rank-roles` - get a list of current rank roles.");
        Manual.setHelpPage("util", "\n" +
                                         "`help [command]` - get help for a specific command.\n" +
                                         "`prefix <prefix>` - set this guilds prefix. (adm)\n" +
                                         "`status` - get the status and statistics of the bot.\n" +
                                         "`shop` - shop items (not fully implemented).\n" +
                                         "`timestamp` - get the timestamp of a Discord snowflake.\n" +
                                         "`uptime` - get the uptime of the bot.\n" +
                                         "`info` - get info about the server or a specific user.\n" +
                                         "`avatar` - get the avatar of yourself or another user.\n" +
                                         "`bug-report` - report a bug directly to me.\n");
        Manual.setHelpPage("moderation", "\n" +
                                          "`log-channel` - set the logging channel.(adm)\n" +
                                          "`add-log` - add a log type to the log channel.(adm)\n" +
                                          "`remove-log` - remove a log type from the channel.(adm)\n" +
                                          "`log-types` - list all current log types.\n" +
                                          "`get-log` - list what's being logged.");
        Manual.setHelpPage("fun", "\n" +
                                   "`someone` - mention a random user without pinging them.\n" +
                                   "`brainfuck` - run brainfuck code" +
                                   " (yes, [it is a real thing](https://en.wikipedia.org/wiki/Brainfuck)).\n" +
                                   "`8ball` - simulate a magic 8 ball.\n" +
                                   "`ship` - ship two users (was asked to add this).\n" +
                                   "`20` - roll a twenty sided die.\n" +
                                   "`owo` - owofy a stwing of text,,,");
        Manual.setHelpPage("reactions", "\n" +
                                         "`hug` - hug another user (or yourself)...\n" +
                                         "`ayaya` - react with ayaya (or something else).\n" +
                                         "`hide` - hide behind a wall.\n" +
                                         "`dis` - display ಠ_ಠ\n" +
                                         "");
        
        eb.addField("All commands", "Use the `help` command followed by a category:\n" +
                                            "`leveling` - leveling and rank commands.\n" +
                                            "`util` - utilities.\n" +
                                            "`moderation` - moderation and logging commands.\n" +
                                            "`fun` - fun commands.\n" +
                                            "`reactions` - GIF or text reactions.", false);
        
//        eb.addField("Want something added?", "\n" +
//                                                     "If you would like to see another command added, or more features " +
//                                                     "on this bot, please [join the Discord server](https://discord.gg/vSWgQ9a) :)", false);
        
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
