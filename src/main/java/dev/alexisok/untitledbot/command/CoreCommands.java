package dev.alexisok.untitledbot.command;

import dev.alexisok.untitledbot.Main;

/**
 * @author AlexIsOK
 * @since 0.0.1
 */
public class CoreCommands {
	
	/**
	 * Register all core commands.
	 */
	public static void registerCoreCommands() {
		CommandRegistrar.register("help", "core.help", ((args, message) -> {
			try {
				return Manual.getHelpPages(args[1]);
			} catch(ArrayIndexOutOfBoundsException ignored) {
				return "Usage: `man <command>`";
			}
		}));
		CommandRegistrar.register("man", "core.help", ((args, message) -> {
			try {
				return Manual.getHelpPages(args[1]);
			} catch(ArrayIndexOutOfBoundsException ignored) {
				return "Usage: `man <command>`";
			}
		}));
		CommandRegistrar.register("status", "core.stats", (((args, message) -> {
			String returnString = "";
			returnString += "JDA status: " + Main.jda.getStatus() + "\n";
			returnString += "Available memory: " + Runtime.getRuntime().freeMemory() + "\n";
			returnString += "Total memory: " + Runtime.getRuntime().totalMemory() + "\n";
			returnString += "Processors: " + Runtime.getRuntime().availableProcessors() + "\n";
			returnString += "Java version: " + Runtime.version() + "\n";
			
			return returnString;
		})));
	}
	
	private static void registerHelp() {
		Manual.setHelpPage("help", "Get help with a specific command.  Usage: `man <command>`.");
		Manual.setHelpPage("man", "Get help with a specific command.  Usage: `man <command>`.");
		Manual.setHelpPage("status", "Get the status of the bot and JVM.");
	}
	
}
