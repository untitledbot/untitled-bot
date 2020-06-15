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
	
	private static final ArrayList<String> MAN_PAGES = new ArrayList<>();
	
	/**
	 * Get the first help page.
	 * @return the first help page.
	 */
	public static @NotNull String getHelpPages() {
		return getHelpPages(1);
	}
	
	/**
	 * Get a specific help page.
	 * @param page the page.
	 * @return the help page specified.
	 */
	public static String getHelpPages(int page) {
		if(page <= 0)
			page = 1;
		if(page * 5 < MAN_PAGES.size())
			return "No such page exists!";
		StringBuilder manPageReturn = new StringBuilder("===Manual page " + page + "===\n"); //StringBuilder is confusing and stupid
		for(int i = page * 5 - 2; i < (page * 5) + 4; i++) {
			try {
				manPageReturn.append(MAN_PAGES.get(i)).append("\n");
			} catch(ArrayIndexOutOfBoundsException ignored) {break;}
		}
		manPageReturn.append("\nFor more help, do `help [page]`.\n");
		return manPageReturn.toString();
	}
	
	/**
	 * Set a help entry.
	 * @param command the entry
	 * @param help the description (help description).
	 */
	public static void setHelpPage(@NotNull String command, String help) {
		if(help == null)
			help = "No help has been provided for this command by the plugin author.";
		MAN_PAGES.add(command + ": " + help);
	}
}
