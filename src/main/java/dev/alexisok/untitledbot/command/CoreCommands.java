package dev.alexisok.untitledbot.command;

import dev.alexisok.untitledbot.Main;
import dev.alexisok.untitledbot.data.GetUserData;
import dev.alexisok.untitledbot.logging.Logger;
import dev.alexisok.untitledbot.modules.basic.atsomeone.AtSomeone;
import dev.alexisok.untitledbot.modules.basic.brainfreak.BrainFreak;
import dev.alexisok.untitledbot.modules.basic.eightball.EightBall;
import dev.alexisok.untitledbot.modules.basic.help.Help;
import dev.alexisok.untitledbot.modules.basic.perms.Permissions;
import dev.alexisok.untitledbot.modules.basic.prefix.Prefix;
import dev.alexisok.untitledbot.modules.basic.ship.Ship;
import dev.alexisok.untitledbot.modules.basic.status.Status;
import dev.alexisok.untitledbot.modules.basic.timestamp.TimeStamp;
import dev.alexisok.untitledbot.modules.basic.twenty.TwentyDice;
import dev.alexisok.untitledbot.modules.rank.Ranks;
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
		//help command
		new Help().onRegister();
		new Status().onRegister();
		new Permissions().onRegister();
		
		//this is a test.  delete this. TODO
		CommandRegistrar.register("stall", "owner", (args, message) -> {
			
			new java.util.Scanner(System.in).next();
			return null;
		});
		
		//invite command
		CommandRegistrar.register("invite", "core.invite", (args, message) -> {
			EmbedBuilder eb = new EmbedBuilder();
			EmbedDefaults.setEmbedDefaults(eb, message);
			
			eb.setColor(Color.GREEN);
			
			eb.addField("Invite link",
					"You can invite the bot to the server using the invite link " +
							"https://discord.com/oauth2/authorize?client_id=" +
							Main.jda.getSelfUser().getId() + "" +
							"&scope=bot&permissions=2146958839\n\nYou can find the invite links for the other versions " +
							"of the bot in the Discord server: https://discord.gg/TVvKkad",
					false);
			
			return eb.build();
		});
		CommandRegistrar.register("about", "core.about", (args, message) -> {
			EmbedBuilder eb = new EmbedBuilder();
			EmbedDefaults.setEmbedDefaults(eb, message);
			
			String returnString = "Hello!  I am a bot made by AlexIsOK!  I do many things (you can see the full list with" +
					                      " the 'help' command) and I am constantly improving as well!  If you have any suggestions" +
					                      " or bugs to report, you can do so at https://github.com/alexisok/untitled-bot/issues\n" +
					                      "\n" +
					                      "For help with the bot, read the documentation, or if you're really confused you can" +
					                      " join the Discord server using the 'invite' command, it's not a very big community" +
					                      " as of now, but it would be great to have more people involved!";
			
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
		setDefaults();
	}
	
	
	/**
	 * Set default permission nodes.
	 */
	private static void setDefaults() {
		CommandRegistrar.setDefaultPermissionForNode("core.ranks", true);
		CommandRegistrar.setDefaultPermissionForNode("core.stats", true);
		CommandRegistrar.setDefaultPermissionForNode("core.invite", true);
		CommandRegistrar.setDefaultPermissionForNode("core.about", true);
		CommandRegistrar.setDefaultPermissionForNode("module.example.eightball", true);
		CommandRegistrar.setDefaultPermissionForNode("fun.twenty", true);
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
		Logger.log("Modules have been registered.");
	}
}
