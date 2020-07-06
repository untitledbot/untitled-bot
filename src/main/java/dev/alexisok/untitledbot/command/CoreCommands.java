package dev.alexisok.untitledbot.command;

import dev.alexisok.untitledbot.Main;
import dev.alexisok.untitledbot.logging.Logger;
import dev.alexisok.untitledbot.modules.basic.atsomeone.AtSomeone;
import dev.alexisok.untitledbot.modules.basic.eightball.EightBall;
import dev.alexisok.untitledbot.plugin.UBPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Objects;

/**
 * @author AlexIsOK
 * @since 0.0.1
 */
public class CoreCommands {
	
	/**
	 * Register all core commands.
	 */
	public static void registerCoreCommands() {
		Logger.log("Registering core commands.");
		CommandRegistrar.register("help", "core.help", ((args, message) -> {
			try {
				return Manual.getHelpPages(args[1]);
			} catch(ArrayIndexOutOfBoundsException ignored) {
				return "Usage: `man <command>`";
			}
		}));
		CommandRegistrar.registerAlias("help", "man", "halp");
		CommandRegistrar.register("status", "core.stats", (((args, message) -> {
			String returnString = "";
			returnString += "JDA status: " + Main.jda.getStatus() + "\n";
			returnString += "Available memory: " + Runtime.getRuntime().freeMemory() + "\n";
			returnString += "Total memory: " + Runtime.getRuntime().totalMemory() + "\n";
			returnString += "Processors: " + Runtime.getRuntime().availableProcessors() + "\n";
			returnString += "Java version: " + Runtime.version() + "\n";
			
			return returnString;
		})));
		CommandRegistrar.register("shutdown", "core.shutdown", (args, message) -> {
			try {
				Logger.log("shutting down...\nThank you for using untitled-bot!");
				Logger.log("Exit caused by message " + Arrays.toString(args) + " by user ID " + message.getAuthor().getId());
				System.exit(Integer.parseInt(args[1]));
			} catch(ArrayIndexOutOfBoundsException ignored) {
				Logger.log("shutting down...\nThank you for using untitled-bot!");
				Logger.log("Exit caused by message " + Arrays.toString(args) + " by user ID " + message.getAuthor().getId());
				System.exit(0);
			}
			return "Usage: `shutdown [code]`";
		});
		CommandRegistrar.register("permission", "core.permission", ((args, message) -> {
			if(message.getAuthor().isBot())
				return "Bot users are not allowed to execute this command.";
			if(message.getGuild().getRoles()) //FIXME
		}));
		Logger.log("Core commands have been registered.");
	}
	
	/**
	 * Register core help pages.
	 */
	private static void registerHelp() {
		Manual.setHelpPage("help", "Get help with a specific command.  Usage: `man <command>`.");
		Manual.setHelpPage("status", "Get the status of the bot and JVM.");
		Manual.setHelpPage("shutdown", "Shutdown the bot.  Usage: `shutdown [code]` where code is the optional exit code.");
		CommandRegistrar.registerAliasManual("shutdown", "stop", "exit");
		CommandRegistrar.registerAliasManual("help", "man", "halp");
	}
	
	public static void registerModules() {
		Logger.log("Registering modules.");
		new EightBall().onRegister();
		new AtSomeone().onRegister();
		Logger.log("Modules have been registered.");
	}
}
