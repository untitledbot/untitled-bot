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
 * Uses command `commands`.
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
                                    "`timestamp` - get the timestamp of a Discord snowflake.\n" +
                                    "`uptime` - get the uptime of the bot.\n" +
                                    "`info` - get info about the server or a specific user.\n" +
                                    "`avatar` - get the avatar of yourself or another user.\n" +
                                    "`bug-report` - report a bug directly to me.\n" +
                                    "`discord` - get an invite link for the Discord support server.\n" +
                                    "`vote` - vote for the bot on Top.GG.\n" +
                                    "`ping` - get the ping of the bot.\n" +
                                    "`inv` - list your inventory.\n" +
                                    "`reverse` - reverse a string.\n" +
                                    "`config` - easy configuration for the bot.(adm)\n" +
                                    "`profile` - get the profile of another user.\n" +
                                    "`caps` - change a string to all capital letters.\n" +
                                    "`lowercase` - change a string to all lowercase letters.\n" +
                                    "`mock` - MaKe A mEsSaGe ChAnGe EaCh LeTtEr CaPiTaLiZaTiOn.\n" +
                                           "`source` - get the source code for the bot.\n" +
                                           "`which` - get the package for a command.\n" +
                                           "`permissions` - get the permissions of a specific user or yourself.\n" +
                                           "`remind` - have the bot dm you a reminder.\n" +
                                           "`rot13` - run a string through rot13.\n");
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
                                    "`owo` - owofy a stwing of text,,,\n" +
                                          "`cat` - get a cat fact." +
                "`aol` - a legendary tale of the spider lenny vs the wizard lenny.....\n");
        Manual.setHelpPage("econ", "\n" +
                                    "`bal` - get the balance of yourself or another user.\n" +
                                    "`work` - get UB$\n" +
                                    "`steal` - get or lose UB$ depending on who you target.\n" +
                                    "`shop` - the shop.\n" +
                                    "`inv` - list your inventory.\n" +
                                           "`pay` - pay a user UB$.\n" +
                                           "`bet` - coinflip command.\n" +
                                           "`deposit` and `withdraw`, put/take money between the bank.");
        Manual.setHelpPage("reactions", "\n" +
                                    "`hug` - hug another user (or yourself)...\n" +
                                    "`ayaya` - react with ayaya (or something else).\n" +
                                    "`hide` - hide behind a wall.\n" +
                                    "`dis` - display ಠ_ಠ\n" +
                                    "");
        
        eb.addField("All commands", "Please use one of the following commands for help:\n" +
                                            "`help leveling` - leveling and rank commands.\n" +
                                            "`help util` - utilities.\n" +
                                            "`help moderation` - moderation and logging commands.\n" +
                                            "`help fun` - fun commands.\n" +
                                            "`help econ` - economy commands.\n" +
                                            "`help reactions` - GIF or text reactions.", false);
        
//        eb.addField("Want something added?", "\n" +
//                                                     "If you would like to see another command added, or more features " +
//                                                     "on this bot, please [join the Discord server](https://discord.gg/vSWgQ9a) :)", false);
        
        eb.addField("", String.format("**[Support Server](%s) | [GitHub](%s) | [Vote on Top.GG](%s)**",
                "https://alexisok.dev/ub/discord.html", //DO NOT CHANGE TO XYZ SITE
                "https://github.com/untitledbot/untitled-bot",
                "https://top.gg/bot/730135989863055472/vote"), false);
        
        HELP_MESSAGE = eb.build();
    }
    
    /**
     * 
     * Get the help command as statically defined in this class.
     * The help command is generated when the command is registered,
     * but it might be changed when multi-language support is added.
     * 
     * @param args arguments for the command.
     *             The first argument is always the name of
     *             the command.  Arguments are the discord
     *             message separated by spaces.
     * @param message the {@link Message} that can be used
     *                to get information from the user
     * @return the help message.
     */
    @Override
    public @Nullable MessageEmbed onCommand(String[] args, @NotNull Message message) {
        return HELP_MESSAGE;
    }
    
    /**
     * Registers the command `commands`
     */
    @Override
    public void onRegister() {
        CommandRegistrar.register("commands", "core.commands", this);
    }
}
