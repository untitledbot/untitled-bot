package dev.alexisok.untitledbot.command;

import net.dv8tion.jda.api.entities.Message;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Objects;

/**
 * @author AlexIsOK
 * @since 0.0.1
 */
public class CommandRegistrar {
	
	private static final HashMap<String, Command> REGISTRAR = new HashMap<>();
	
	//commandName, permission
	private static final HashMap<String, String> PERMS_REGISTRAR = new HashMap<>();
	
	/**
	 * @return the size of the registrar.
	 */
	static int registrarSize() {
		return REGISTRAR.size();
	}
	
	/**
	 * Register a command.
	 * @param commandName the name of the command.  Must match {@code ^[a-z0-9_-]*$} (alphanumerical
	 *                    lowercase only plus underscores and hyphens).   
	 * @param permission the permission the command uses.  Permissions are used for members
	 *                   or groups of members, they can only use the command if they have
	 *                   the needed permission.  Must match {@code ^[a-z]([a-z][.]?)+[a-z]$} convention is to start
	 *                   the permission with your plugin name, period, then the command (example:
	 *                   {@code coolplugin.command} or {@code coolplugin.category.command}.
	 *                   Do not have the permission end or start with a period.  Numbers and
	 *                   capital letters do not match.
	 * @param command the {@link Command} to use.  {@link Command#onCommand(String[], Message)}
	 *                will be executed when the command is called.
	 * @throws CommandAlreadyRegisteredException if the command already exists.
	 * @throws RuntimeException if the command does not match the regex.
	 */
	public static void register(@NotNull String commandName, @NotNull String permission, @NotNull Command command)
			throws CommandAlreadyRegisteredException {
		
		if(REGISTRAR.containsKey(commandName))
			throw new CommandAlreadyRegisteredException();
		
		if(!commandName.matches("^[a-z0-9_-]*$"))
			throw new RuntimeException("Command does not match regex!");
		if(!permission.matches("^[a-z]([a-z][.]?)+[a-z]$")) //this took too long to make...
			throw new RuntimeException("Command permission does not match regex!");
		
		REGISTRAR.put(commandName, command);
		PERMS_REGISTRAR.put(commandName, permission);
	}
	
	/**
	 * Run a command.  This can be invoked by plugins
	 * so there can be more creative plugins.
	 * 
	 * @param commandName the name of the command to execute
	 * @return the return String.  Returns {@code null} if the command was not found.
	 */
	public static @Nullable String runCommand(String commandName, String[] args, Message m) {
		if(!REGISTRAR.containsKey(commandName))
			return null;
		return REGISTRAR.get(commandName).onCommand(args, m);
	}
	
	/**
	 * 
	 * @param command the command to check
	 * @return true if the command is registered, false otherwise.
	 */
	public static boolean hasCommand(String command) {
		return REGISTRAR.containsKey(command);
	}
	
	/**
	 * Get the permission node of a command.
	 * @param command the command
	 * @return the permission node, {@code null} if it is not registered.
	 */
	public static @Nullable String getCommandPermissionNode(String command) {
		if(!hasCommand(command))
			return null;
		return PERMS_REGISTRAR.get(command);
	}
	
	
	/**
	 * Register one or more aliases for a command.  When the alias is
	 * run, the actual command will be called instead.
	 *
	 * Aliases are put in the registrar as actual commands and act
	 * as actual commands, though they only call the command they are
	 * aliasing for.
	 *
	 * Alias will inherit the same permission node from the command
	 * it is aliasing.  You can easily register an alias manual page
	 * using
	 *
	 * @param command the command name.
	 * @param aliases the aliases to give the command.
	 */
	public static void registerAlias(@NotNull String command, @NotNull String... aliases) {
		for(String alias : aliases) {
			register(alias, (PERMS_REGISTRAR.get(command)),
					((args, message) -> CommandRegistrar.runCommand(alias, args, message)));
		}
	}
	
	/**
	 * Copy a manual page from a command.  This does not need to be an
	 * alias but it is the intended usage of this method.
	 * 
	 * @param originalCommand the command to copy off of
	 * @param aliases the alias command
	 */
	public static void registerAliasManual(@NotNull String originalCommand, @NotNull String...aliases) {
		for(String alias : aliases) {
			Manual.setHelpPage(alias, Manual.getHelpPagesRaw(originalCommand));
		}
	}
	
}
