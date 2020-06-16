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
		CommandRegistrar.register("help", "core.help", ((args, message) -> {
			try {
				return Manual.getHelpPages(args[1]);
			} catch(ArrayIndexOutOfBoundsException ignored) {
				return "Usage: `help <command>`";
			}
		}));
		CommandRegistrar.register("stats", "core.stats", (((args, message) -> {
			return null; //FIXME
		})));
	}
	
}
