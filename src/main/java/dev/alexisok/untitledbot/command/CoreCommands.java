package dev.alexisok.untitledbot.command;

/**
 * @author AlexIsOK
 * @since 0.0.1
 */
public class CoreCommands {
	
	/**
	 * Register all core commands.
	 */
	public static void registerCoreCommands() {
		CommandRegistrar.register("help", ((args, message) -> Manual.getHelpPages(args[1])));
		CommandRegistrar.register("stats", (((args, message) -> {
			return "";
		})));
	}
	
}
