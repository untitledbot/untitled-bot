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
            "`%srank` - get the rank of yourself or a specific user.\n" +
            "`%sleaderboard` - get the highest ranking users in the guild.\n" +
            "`%srank-total` - get the total amount of xp of yourself or another user.\n" +
            "`%srank-settings` - configure level up messages.(adm)\n" +
            "`%srank-role` - assign rank roles for users when they level up. (adm)\n" +
            "`%srank-roles` - get a list of current rank roles.\n" +
            "`%srank-bg` - set your rank background image.\n" +
            "`%srank-color` - set your rank text color.\n");
        Manual.setHelpPage("util", "\n" +
            "`%shelp [command]` - get help for a specific command.\n" +
            "`%sprefix <prefix>` - set this guilds prefix. (adm)\n" +
            "`%sstatus` - get the status and statistics of the bot.\n" +
            "`%stimestamp` - get the timestamp of a Discord snowflake.\n" +
            "`%suptime` - get the uptime of the bot.\n" +
            "`%sinfo` - get info about the server or a specific user.\n" +
            "`%savatar` - get the avatar of yourself or another user.\n" +
            "`%sbug-report` - report a bug directly to me.\n" +
            "`%sdiscord` - get an invite link for the Discord support server.\n" +
            "`%sping` - get the ping of the bot.\n" +
            "`%sinv` - list your inventory.\n" +
            "`%sreverse` - reverse a string.\n" +
            "`%sconfig` - easy configuration for the bot.(adm)\n" +
            "`%sprofile` - get the profile of another user.\n" +
            "`%scaps` - make a message all capital letters.\n" +
            "`%slowercase` - make a message all lowercase letters.\n" +
            "`%smock` - MaKe A mEsSaGe ChAnGe EaCh LeTtEr CaPiTaLiZaTiOn.\n" +
            "`%ssource` - get the source code for the bot.\n" +
            "`%swhich` - get the package for a command.\n" +
            "`%spermissions` - get the permissions of a specific user or yourself.\n" +
            "`%sremind` - have the bot dm you a reminder.\n" +
            "`%srot13` - run a string through rot13.\n");
        Manual.setHelpPage("moderation", "\n" +
            "`%slog-channel` - set the logging channel.(adm)\n" +
            "`%sadd-log` - add a log type to the log channel.(adm)\n" +
            "`%sremove-log` - remove a log type from the channel.(adm)\n" +
            "`%slog-types` - list all current log types.\n" +
            "`%sget-log` - list what's being logged.\n" +
            "`%ssnipe <channel>` - get the last deleted message of a channel.\n" +
            "`%spurge` - leave and delete all user data for this server.");
        Manual.setHelpPage("fun", "\n" +
            "`%ssomeone` - mention a random user without pinging them.\n" +
            "`%s8ball` - simulate a magic 8 ball.\n" +
            "`%sship` - ship two users (user requested!).\n" +
            "`%s20` - roll a twenty sided die.\n" +
            "`%sowo` - owofy a stwing of text,,,\n" +
            "`%scat` - get a cat fact.\n" +
            "`%saol` - a legendary tale of the spider lenny vs the wizard lenny.....\n");
        Manual.setHelpPage("econ", "\n" +
            "`%sbal` - get the balance of yourself or another user.\n" +
            "`%swork` - get UB$\n" +
            "`%ssteal` - get or lose UB$ depending on who you target.\n" +
            "`%sinv` - list your inventory.\n" +
            "`%spay` - pay a user UB$.\n" +
            "`%sbet` - coinflip command.\n" +
            "`%sdeposit` and `withdraw`, put/take money between the bank.");
        Manual.setHelpPage("reactions", "\n" +
            "`%sayaya` - react with ayaya (or something else).\n" +
            "`%shide` - hide behind a wall.\n" +
            "`%sdis` - display ಠ_ಠ\n" +
            "`%saol` - attack on lenny.\n" +
            "");
        
        Manual.setHelpPage("image", "\n" +
            "NOTE: please upload an image to manipulate or @mention another user, otherwise it will use your avatar.\n" +
            "`%smagik` - LITERALLY CORRUPTS images (not literally)\n" +
            "`%sblur` - blur an image\n" +
            "`%sdeepfry` - deepfry an image\n" +
            "`%sinvert` - invert an image\n" +
            "`%sjpeg` - make image jpeg\n" +
            "`%spixelate` - make image pixelate\n" +
            "`%ssepia` - listen it was on alexflipnote api idk what it does\n" +
            "`%swide` - T H I C C image\n" +
            "`%scharcoal` - make an image look like charcoal\n" +
            "`%sexplode` - kaboom\n" +
            "`%simplode` - implode an image\n" +
            "`%ssketch` - make an image look like it was sketched\n" +
            "`%sspread` - spread ALL the pixels\n" +
            "`%sswirl` - whszoosh\n" +
            "`%swave` - wewewewew\n" +
            "`%semboss` - embosses images?");
        
        Manual.setHelpPage("meme", "\n" +
            "`%sachievement` - generate a fake MC achievement\n" +
            "`%sjoke` - generate an 'Am I a joke to you?' meme\n" +
            "`%sbad` - generate a 'Bad.' meme\n" +
            "`%sscroll` - generate a 'Scroll of Truth' meme\n" +
            "`%scall` - generate a meme where tom calls someone\n");
        
        Manual.setHelpPage("music", "\n" +
            "`%splay` - play/search for a YouTube or Spotify song\n" +
            "`%sstop` - stop the player and clear the queue\n" +
            "`%sskip` - skip the currently playing song\n" +
            "`%squeue` - list the queue of upcoming songs\n" +
            "`%snp` - get the now playing songs\n" +
            "`%spause` - pause/unpause the songs\n" +
            "`%sjoin` - join the voice channel without playing anything\n" +
            "`%sleave` - leave the current voice channel without stopping the player\n" +
            "`%sdj` - set the DJ role for the server\n" +
            "`%sseek` - seek to a specific point in the song");
        
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
        CommandRegistrar.register("commands", this);
    }
}
