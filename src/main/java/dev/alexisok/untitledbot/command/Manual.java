package dev.alexisok.untitledbot.command;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

/**
 * Help pages for use with the "help" command.
 * 
 * Alias manuals now inherit their owner manual from {@link CommandRegistrar#registerAlias(String, String...)}.
 * 
 * @author AlexIsOK
 * @since 0.0.1
 */
public final class Manual {
	
	private static final HashMap<String, String> MAN_PAGES = new HashMap<>();
	
	/**
	 * Get a specific help page.
	 * @param page the page.
	 * @return the help page specified or {@code null} if no such page exists.
	 */
	@Nullable
	@Contract(pure = true)
	public static String getHelpPages(String page) {
		if(MAN_PAGES.containsKey(page))
			return String.format("Help for %s:%n%n%s", page, MAN_PAGES.get(page));
		else
			return String.format("There doesn't seem to be a help page for %s!", page);
	}
	
	/**
	 * Get a help page directly without any extra content.
	 * 
	 * @param page the page.
	 * @return the help page specified or {@code null} if no such page exists.
	 */
	@Nullable
	@Contract(pure = true)
	public static String getHelpPagesRaw(String page) {
		return MAN_PAGES.getOrDefault(page, null);
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
