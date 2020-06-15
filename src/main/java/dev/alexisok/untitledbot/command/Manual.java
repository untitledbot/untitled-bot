package dev.alexisok.untitledbot.command;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Help pages for use with the "help" command.
 * 
 * @author AlexIsOK
 * @since 0.0.1
 */
public class Manual {
	
	private static final HashMap<String, String> MAN_PAGES = new HashMap<>();
	
	/**
	 * Get a specific help page.
	 * @param page the page.
	 * @return the help page specified.
	 */
	public static @NotNull String getHelpPages(String page) {
		if(!MAN_PAGES.containsKey(page))
			return "No such page exists!";
		return MAN_PAGES.get(page);
	}
	
	/**
	 * Set a help entry.
	 * @param command the entry
	 * @param help the description (help description).
	 */
	public static void setHelpPage(@NotNull String command, String help) {
		if(help == null)
			help = "No help has been provided for this command by the plugin author.";
		MAN_PAGES.put(command, help);
	}
}
