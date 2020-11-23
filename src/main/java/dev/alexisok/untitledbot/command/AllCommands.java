package dev.alexisok.untitledbot.command;

import dev.alexisok.untitledbot.BotClass;
import dev.alexisok.untitledbot.plugin.UBPlugin;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

/**
 * Lists all commands to the user.
 * 
 * Uses command `commands`.
 * 
 * @author AlexIsOK
 * @since 1.3
 */
public class AllCommands extends UBPlugin {
    
    static {
        EmbedBuilder eb = new EmbedBuilder();
    
        eb.setTitle("untitled-bot commands");
    
        Manual.setHelpPage("leveling", "\n" +
            "`rank` - get the rank of yourself or a specific user.\n" +
            "`leaderboard` - get the highest ranking users in the guild.\n" +
            "`rank-total` - get the total amount of xp of yourself or another user.\n" +
            "`rank-settings` - configure level up messages.(adm)\n" +
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
            "`ping` - get the ping of the bot.\n" +
            "`inv` - list your inventory.\n" +
            "`reverse` - reverse a string.\n" +
            "`config` - easy configuration for the bot.(adm)\n" +
            "`profile` - get the profile of another user.\n" +
            "`caps` - make a message all capital letters.\n" +
            "`lowercase` - make a message all lowercase letters.\n" +
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
            "`get-log` - list what's being logged.\n" +
            "`snipe <channel> - get the last deleted message of a channel.");
        Manual.setHelpPage("fun", "\n" +
            "`someone` - mention a random user without pinging them.\n" +
            "`8ball` - simulate a magic 8 ball.\n" +
            "`ship` - ship two users (user requested!).\n" +
            "`20` - roll a twenty sided die.\n" +
            "`owo` - owofy a stwing of text,,,\n" +
            "`cat` - get a cat fact.\n" +
            "`aol` - a legendary tale of the spider lenny vs the wizard lenny.....\n");
        Manual.setHelpPage("econ", "\n" +
            "`bal` - get the balance of yourself or another user.\n" +
            "`work` - get UB$\n" +
            "`steal` - get or lose UB$ depending on who you target.\n" +
            "`inv` - list your inventory.\n" +
            "`pay` - pay a user UB$.\n" +
            "`bet` - coinflip command.\n" +
            "`deposit` and `withdraw`, put/take money between the bank.");
        Manual.setHelpPage("reactions", "\n" +
            "`ayaya` - react with ayaya (or something else).\n" +
            "`hide` - hide behind a wall.\n" +
            "`dis` - display ಠ_ಠ\n" +
            "`aol` - attack on lenny.\n" +
            "");
        
        Manual.setHelpPage("image", "\n" +
            "NOTE: please upload an image to manipulate or @mention another user, otherwise it will use your avatar.\n" +
            "`magik` - LITERALLY CORRUPTS images (not literally)\n" +
            "`blur` - blur an image\n" +
            "`deepfry` - deepfry an image\n" +
            "`invert` - invert an image\n" +
            "`jpeg` - make image jpeg\n" +
            "`pixelate` - make image pixelate\n" +
            "`sepia` - listen it was on alexflipnote api idk what it does\n" +
            "`wide` - T H I C C image\n" +
            "`charcoal` - make an image look like charcoal\n" +
            "`explode` - kaboom\n" +
            "`implode` - implode an image\n" +
            "`sketch` - make an image look like it was sketched\n" +
            "`spread` - spread ALL the pixels\n" +
            "`swirl` - whszoosh\n" +
            "`wave` - wewewewew\n" +
            "`emboss` - embosses images?");
        
        Manual.setHelpPage("meme", "\n" +
            "`achievement` - generate a fake MC achievement\n" +
            "`joke` - generate an 'Am I a joke to you?' meme\n" +
            "`bad` - generate a 'Bad.' meme\n" +
            "`scroll` - generate a 'Scroll of Truth' meme\n" +
            "`call` - generate a meme where tom calls someone\n");
        
        Manual.setHelpPage("music", "\n" +
            "`play` - play or search for a YouTube video\n" +
            "`stop` - stop the player and clear the queue\n" +
            "`skip` - skip the currently playing song\n" +
            "`queue` - list the queue of upcoming songs\n" +
            "`np` - get the now playing songs\n" +
            "`pause` - pause/unpause the songs\n" +
            "`join` - join the voice channel without playing anything\n");
        
        eb.addField("All commands", "Please use one of the following commands for help:\n" +
            "`>help leveling` - leveling and rank commands.\n" +
            "`>help util` - utilities.\n" +
            "`>help moderation` - moderation and logging commands.\n" +
            "`>help fun` - fun commands.\n" +
            "`>help econ` - economy commands.\n" +
            "`>help reactions` - GIF or text reactions.\n" +
            "`>help image` - image manipulation\n" +
            "`>help meme` - meme generation.\n" +
            "`>help music` - music commands.\n\n" +
            "To change the prefix of the bot, use `>prefix`", false);
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
        EmbedBuilder eb = new EmbedBuilder();
        EmbedDefaults.setEmbedDefaults(eb, message);
        String prefix = BotClass.getPrefixNice(message.getGuild().getId());
        eb.addField("All commands", String.format("Please use one of the following commands for help:\n" +
                "`%shelp leveling` - leveling and rank commands.\n" +
                "`%shelp util` - utilities.\n" +
                "`%shelp moderation` - moderation and logging commands.\n" +
                "`%shelp fun` - fun commands.\n" +
                "`%shelp econ` - economy commands.\n" +
                "`%shelp reactions` - GIF or text reactions.\n" +
                "`%shelp image` - image manipulation\n" +
                "`%shelp meme` - meme generation.\n" +
                "`%shelp music` - music commands.\n\n" +
                "To change the prefix of the bot, use `%sprefix`",
                prefix, prefix, prefix, prefix, prefix, prefix, prefix, prefix, prefix, prefix), false);
        eb.setColor(Color.GREEN);
        eb.addField("Need support?", String.format("**[Support Server](%s) | [GitHub](%s) | [Official Site](%s)**",
                "https://alexisok.dev/ub/discord.html", //DO NOT CHANGE TO XYZ SITE
                "https://github.com/untitledbot/untitled-bot",
                "https://untitled-bot.xyz/"), false);
        return eb.build();
    }
    
    /**
     * Registers the command `commands`
     */
    @Override
    public void onRegister() {
        CommandRegistrar.register("commands", "core.commands", this);
    }
}
