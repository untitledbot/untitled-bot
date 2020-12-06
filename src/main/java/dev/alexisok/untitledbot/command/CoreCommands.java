package dev.alexisok.untitledbot.command;

import dev.alexisok.untitledbot.command.enums.UBPerm;
import dev.alexisok.untitledbot.data.GetUserData;
import dev.alexisok.untitledbot.logging.Logger;
import dev.alexisok.untitledbot.modules.basic.privacy.Privacy;
import dev.alexisok.untitledbot.modules.basic.purge.Purge;
import dev.alexisok.untitledbot.modules.basic.sleep.Sleep;
import dev.alexisok.untitledbot.modules.images.apiflipnote.*;
import dev.alexisok.untitledbot.modules.images.apiflipnote.filter.*;
import dev.alexisok.untitledbot.modules.images.apiuseless.*;
import dev.alexisok.untitledbot.modules.basic.atsomeone.AtSomeone;
import dev.alexisok.untitledbot.modules.basic.avatar.Avatar;
import dev.alexisok.untitledbot.modules.basic.ayaya.AYAYA;
import dev.alexisok.untitledbot.modules.basic.blacklist.Blacklist;
import dev.alexisok.untitledbot.modules.basic.bruh.Bruh;
import dev.alexisok.untitledbot.modules.basic.cache.CacheInfo;
import dev.alexisok.untitledbot.modules.basic.casetoggle.Lowercase;
import dev.alexisok.untitledbot.modules.basic.casetoggle.ToggleCase;
import dev.alexisok.untitledbot.modules.basic.casetoggle.Uppercase;
import dev.alexisok.untitledbot.modules.basic.catfact.CatFact;
import dev.alexisok.untitledbot.modules.basic.datamod.UserDataMod;
import dev.alexisok.untitledbot.modules.basic.discordCommand.Discord;
import dev.alexisok.untitledbot.modules.basic.eightball.EightBall;
import dev.alexisok.untitledbot.modules.basic.help.Help;
import dev.alexisok.untitledbot.modules.basic.owo.Owo;
import dev.alexisok.untitledbot.modules.basic.permission.PermissionsCommand;
import dev.alexisok.untitledbot.modules.basic.ping.Ping;
import dev.alexisok.untitledbot.modules.basic.prefix.Prefix;
import dev.alexisok.untitledbot.modules.basic.remind.Remind;
import dev.alexisok.untitledbot.modules.basic.report.BugReport;
import dev.alexisok.untitledbot.modules.basic.reverse.Reverse;
import dev.alexisok.untitledbot.modules.basic.roleCount.RoleCount;
import dev.alexisok.untitledbot.modules.basic.rot13.ROT13;
import dev.alexisok.untitledbot.modules.basic.ship.Ship;
import dev.alexisok.untitledbot.modules.basic.shutdown.Shutdown;
import dev.alexisok.untitledbot.modules.basic.snipe.Snipe;
import dev.alexisok.untitledbot.modules.basic.source.Source;
import dev.alexisok.untitledbot.modules.basic.stack.Stack;
import dev.alexisok.untitledbot.modules.basic.status.Status;
import dev.alexisok.untitledbot.modules.basic.timestamp.TimeStamp;
import dev.alexisok.untitledbot.modules.basic.twenty.TwentyDice;
import dev.alexisok.untitledbot.modules.basic.uptime.Uptime;
import dev.alexisok.untitledbot.modules.basic.userinfo.UserInfo;
import dev.alexisok.untitledbot.modules.basic.vote.Vote;
import dev.alexisok.untitledbot.modules.basic.cache.VoidCache;
import dev.alexisok.untitledbot.modules.config.ConfigHandle;
import dev.alexisok.untitledbot.modules.eco.*;
import dev.alexisok.untitledbot.modules.basic.eval.Eval;
import dev.alexisok.untitledbot.modules.music.*;
import dev.alexisok.untitledbot.modules.basic.noprefix.Exit;
import dev.alexisok.untitledbot.modules.basic.noprefix.NoPrefix;
import dev.alexisok.untitledbot.modules.basic.profile.Profile;
import dev.alexisok.untitledbot.modules.rank.Ranks;
import dev.alexisok.untitledbot.modules.rank.Rnak;
import dev.alexisok.untitledbot.modules.rank.rankcommands.RankRoleGet;
import dev.alexisok.untitledbot.modules.rank.rankcommands.RankRoleSet;
import dev.alexisok.untitledbot.modules.rank.xpcommands.Steal;
import dev.alexisok.untitledbot.modules.rank.xpcommands.Work;
import dev.alexisok.untitledbot.modules.reactions.AttackOnLenny;
import dev.alexisok.untitledbot.modules.reactions.Dis;
import dev.alexisok.untitledbot.modules.reactions.Hide;
import dev.alexisok.untitledbot.modules.basic.reward.VoteReward;
import dev.alexisok.untitledbot.modules.starboard.StarboardHandle;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;

/**
 * This registers commands or points to all of the commands in the {@link dev.alexisok.untitledbot.modules}
 * package.
 *
 * @author AlexIsOK
 * @since 0.0.1
 */
public final class CoreCommands {
    
    /**
     * Register all core commands.  This does not need to be private
     * because commands can only be registered once.
     */
    public static void registerCoreCommands() {
        Logger.log("Registering core commands.");
        //invite command
        CommandRegistrar.register("invite", (args, message) -> {
            EmbedBuilder eb = new EmbedBuilder();
            EmbedDefaults.setEmbedDefaults(eb, message);
            
            eb.setColor(Color.GREEN);
            
            String inviteLink = "https://discord.com/oauth2/authorize?client_id=730135989863055472&scope=bot&permissions=3460160";
            
            eb.addField("Invite link",
                    String.format("You can invite the bot to a server using [this invite link](%s).", inviteLink),
                    false);
            
            return eb.build();
        });
        
        //The about command to get information about the bot and the server link.
        CommandRegistrar.register("about", (args, message) -> {
            EmbedBuilder eb = new EmbedBuilder();
            EmbedDefaults.setEmbedDefaults(eb, message);
            
            String returnString = "```" +
                                          "\n" +
                                          "      ╔╗─╔╗╔╗     ╔╗  ╔╗    ╔╗\n" +
                                          "     ╔╝╚╦╝╚╣║     ║║  ║║   ╔╝╚╗\n" +
                                          "╔╗╔╦═╬╗╔╬╗╔╣║╔══╦═╝║  ║╚═╦═╩╗╔╝\n" +
                                          "║║║║╔╗╣║╠╣║║║║║═╣╔╗╠══╣╔╗║╔╗║║\n" +
                                          "║╚╝║║║║╚╣║╚╣╚╣║═╣╚╝╠══╣╚╝║╚╝║╚╗\n" +
                                          "╚══╩╝╚╩═╩╩═╩═╩══╩══╝  ╚══╩══╩═╝\n" +
                                          "```" +
                                          "Hello!  I am a bot made by AlexIsOK!  I do many things (you can see the full list with" +
                                          " the 'commands' command) and I am constantly improving as well!  If you have any suggestions," +
                                          " bugs to report, or just want a server where you can hang out, " + //these spaces are going to be the end of me
                                          "you can do so in the [official Discord server](https://discord.gg/r5ndhyX)!\n" +
                                          "\n" +
                                          "For help with the bot, use the `commands` command or join the official server for " +
                                          "support (it's a small community but would grow with more users).";
            
            eb.setColor(Color.GREEN);
            eb.addField("", returnString, false);
            return eb.build();
        });
        
        //idk man testing or something
        CommandRegistrar.register("test", (args, message) -> {
            EmbedBuilder eb = new EmbedBuilder();
            EmbedDefaults.setEmbedDefaults(eb, message);
            eb.setColor(Color.CYAN);
            eb.addField("title", "works i think", false);
            return eb.build();
        });
        
        //cooldown command
        CommandRegistrar.register("cooldown", UBPerm.OWNER, (args, message) -> {
            if(args.length == 2)
                return new EmbedBuilder().addField("its " + CommandRegistrar.getCommandCooldown(args[1]), "a", false).build();
            EmbedBuilder eb = new EmbedBuilder();
            CommandRegistrar.setCommandCooldown(args[1], (args[2]));
            return eb.addField("done i guess", ".", false).build();
        });
        
        Logger.log("Core commands have been registered.");
        registerHelp();
    }
    
    /**
     * Register help pages for the following commands:
     * help, status, invite, about, rank.
     * 
     * Not all of those are core commands.
     */
    private static void registerHelp() {
        Manual.setHelpPage("help", "Get help with a specific command.\nUsage: `help <command>`.");
        Manual.setHelpPage("status", "Get the status of the bot and JVM.");
        Manual.setHelpPage("invite", "Get the invite link for the bot.");
        Manual.setHelpPage("about", "much knowledge");
        Manual.setHelpPage("rank", "Get the current level and XP of a user.\nUsage: " +
                                           "rank [user @]\n" +
                                           "leave argument blank for your own stats.");
    }
    
    /**
     * Register almost every single command in the bot.
     * Each command is stored as its own object which
     * may take up more memory but is much more organized.
     * 
     * In the code, the commands are listed in about the order
     * they were made, with {@link EightBall} being the first.
     */
    public static void registerModules() {
        Logger.log("Registering modules.");
        
        new EightBall().onRegister();
        new AtSomeone().onRegister();
        new Ranks().onRegister();
        new TwentyDice().onRegister();
        new Ship().onRegister();
        new GetUserData().onRegister();
        new Prefix().onRegister();
//        new BrainFreak().onRegister();
        new TimeStamp().onRegister();
        new AllCommands().onRegister();
        new Help().onRegister();
        new Status().onRegister();
        new Uptime().onRegister();
        new UserInfo().onRegister();
        new Owo().onRegister();
        new Avatar().onRegister();
        new AYAYA().onRegister();
        new Dis().onRegister();
        new Hide().onRegister();
        new VoteReward().onRegister();
        new RankRoleSet().onRegister();
        new RankRoleGet().onRegister();
        new BugReport().onRegister();
        new Blacklist().onRegister();
        new Vote().onRegister();
        new Ping().onRegister();
//        new Inventory().onRegister();
        new Reverse().onRegister();
        new Discord().onRegister();
        new Balance().onRegister();
        new ConfigHandle().onRegister();
        new Work().onRegister();
        new Profile().onRegister();
        new Steal().onRegister();
        new Uppercase().onRegister();
        new Lowercase().onRegister();
        new ToggleCase().onRegister();
        
        //1.3.22 commands
        new UserDataMod().onRegister();
        new ROT13().onRegister();
        new CatFact().onRegister();
        new Deposit().onRegister();
        new Withdraw().onRegister();
        new Pay().onRegister();
        new StarboardHandle().onRegister();
        new Bet().onRegister();
        new Which().onRegister();
        new Source().onRegister();
        new NoPrefix().onRegister();
        new Exit().onRegister();
        new Remind().onRegister();
        new PermissionsCommand().onRegister();
        new Shutdown().onRegister();
        new Eval().onRegister();
        new VoidCache().onRegister();
        new Stack().onRegister();
        new CacheInfo().onRegister();
        new Rnak().onRegister();
        new AttackOnLenny().onRegister();
        new RoleCount().onRegister();
        
        //alex flipnote api
        new AmIAJoke().onRegister();
        new Bad().onRegister();
        new TomCall().onRegister();
        new Magik().onRegister();
        new Scroll().onRegister();
        new Deepfry().onRegister();
        new Invert().onRegister();
        new Blur().onRegister();
        new Sepia().onRegister();
        new JPEG().onRegister();
        new Wide().onRegister();
        new Pixelate().onRegister();
        new Achievement().onRegister();
        
        new Implode().onRegister();
        new Wave().onRegister();
        new Swirl().onRegister();
        new Charcoal().onRegister();
        new Sketch().onRegister();
        new Spread().onRegister();
        new Glitch().onRegister();
        new Explode().onRegister();
        new Emboss().onRegister();
        
        //music
        new Snipe().onRegister();
        new Play().onRegister();
        new Stop().onRegister();
        new Pause().onRegister();
        new IsPlaying().onRegister();
        new Skip().onRegister();
        new Volume().onRegister();
        new NowPlaying().onRegister();
        new Queue().onRegister();
        new Seek().onRegister();
        new Join().onRegister();
        new Leave().onRegister();
        new DJHandle().onRegister();
        new Repeat().onRegister();
        
        new Bruh().onRegister();
        new GetSafeSearch().onRegister();
        
        new Privacy().onRegister();
        //minecraft api seems to have some problems.
//        new MinecraftAPI().onRegister();
        new Purge().onRegister();
        
        //1.3.25
        new Sleep().onRegister();
        Logger.log("Modules have been registered.");
    }
}
