package dev.alexisok.untitledbot.command;

import dev.alexisok.untitledbot.Main;
import dev.alexisok.untitledbot.data.GetUserData;
import dev.alexisok.untitledbot.logging.Logger;
import dev.alexisok.untitledbot.modules.basic.atsomeone.AtSomeone;
import dev.alexisok.untitledbot.modules.basic.brainfreak.BrainFreak;
import dev.alexisok.untitledbot.modules.basic.eightball.EightBall;
import dev.alexisok.untitledbot.modules.basic.help.Help;
import dev.alexisok.untitledbot.modules.basic.owo.Owo;
import dev.alexisok.untitledbot.modules.basic.prefix.Prefix;
import dev.alexisok.untitledbot.modules.basic.ship.Ship;
import dev.alexisok.untitledbot.modules.basic.status.Status;
import dev.alexisok.untitledbot.modules.basic.timestamp.TimeStamp;
import dev.alexisok.untitledbot.modules.basic.twenty.TwentyDice;
import dev.alexisok.untitledbot.modules.basic.uptime.Uptime;
import dev.alexisok.untitledbot.modules.rank.Ranks;
import dev.alexisok.untitledbot.modules.reactions.Hug;
import dev.alexisok.untitledbot.modules.rpg.RPGManager;
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
        CommandRegistrar.register("invite", "core.invite", (args, message) -> {
            EmbedBuilder eb = new EmbedBuilder();
            EmbedDefaults.setEmbedDefaults(eb, message);
            
            eb.setColor(Color.GREEN);
            
            String inviteLink = String.format("https://discord.com/oauth2/authorize?client_id=%s&scope=bot&permissions=3460160",
                    Main.jda.getSelfUser().getId());
            
            eb.addField("Invite link",
                    String.format("You can invite the bot to a server using [this invite link](%s).", inviteLink),
                    false);
            
            return eb.build();
        });
        CommandRegistrar.register("about", "core.about", (args, message) -> {
            EmbedBuilder eb = new EmbedBuilder();
            EmbedDefaults.setEmbedDefaults(eb, message);
            
            String returnString = "```" +
                                          "\n" +
                                          "      ╔╗─╔╗╔╗     ╔╗  ╔╗    ╔╗\n" +
                                          "     ╔╝╚╦╝╚╣║     ║║  ║║   ╔╝╚╗\n" +
                                          "╔╗╔╦═╬╗╔╬╗╔╣║╔══╦═╝║  ║╚═╦═╩╗╔╝\n" +
                                          "║║║║╔╗╣║╠╣║║║║║═╣╔╗╠══╣╔╗║╔╗║║\n" +
                                          "║╚╝║║║║╚╣║╚╣╚╣║═╣╚╝╠══╣╚╝║╚╝║╚╗\n" +
                                          "╚══╩╝╚╩═╩╩═╩═╩══╩══╝  ╚══╩══╩═╝" +
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
        CommandRegistrar.register("test", "test.test.a", (args, message) -> {
            EmbedBuilder eb = new EmbedBuilder();
            EmbedDefaults.setEmbedDefaults(eb, message);
            eb.setColor(Color.CYAN);
            eb.addField("title", "works i think", false);
            return eb.build();
        });
        
        new Prefix().onRegister();
        
        Logger.log("Core commands have been registered.");
        registerHelp();
    }
    
    /**
     * Register core help pages.
     */
    private static void registerHelp() {
        Manual.setHelpPage("help", "Get help with a specific command.\nUsage: `man <command>`.");
        Manual.setHelpPage("status", "Get the status of the bot and JVM.");
        Manual.setHelpPage("shutdown", "Shutdown the bot.\nUsage: `shutdown [code]` where code is the optional exit code.");
        Manual.setHelpPage("invite", "Get the invite link for the bot.");
        Manual.setHelpPage("about", "much knowledge");
        Manual.setHelpPage("rank", "Get the current level and XP of a user.\nUsage: " +
                                           "rank [user @]\n" +
                                           "leave argument blank for your own stats.");
    }
    
    public static void registerModules() {
        Logger.log("Registering modules.");
        new EightBall().onRegister();
        new AtSomeone().onRegister();
        new Ranks().onRegister();
        new RPGManager().onRegister();
        new TwentyDice().onRegister();
        new Ship().onRegister();
        new GetUserData().onRegister();
        new BrainFreak().onRegister();
        new TimeStamp().onRegister();
        new AllCommands().onRegister();
        new Help().onRegister();
        new Status().onRegister();
        new Uptime().onRegister();
        new Owo().onRegister();
        new Hug().onRegister();
        Logger.log("Modules have been registered.");
    }
}
