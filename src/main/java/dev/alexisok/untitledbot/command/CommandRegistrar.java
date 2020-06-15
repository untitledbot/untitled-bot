package dev.alexisok.untitledbot.command;

import net.dv8tion.jda.api.entities.Message;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

/**
 * @author AlexIsOK
 * @since 0.0.1
 */
public class CommandRegistrar {
	
	private static final HashMap<String, Command> REGISTRAR = new HashMap<>();
	
	/**
	 * @return the size of the registrar.
	 */
	static int registrarSize() {
		return REGISTRAR.size();
	}
	
	/**
	 * Register a command.
	 * @param commandName the name of the command.  Must match "^[a-z0-9_-]*$" (alphanumerical
	 *                    lowercase only plus underscores and hyphens).   
	 * @param command the {@link Command} to use.  {@link Command#onCommand(String[], Message)}
	 *                will be executed when the command is called.
	 * @throws CommandAlreadyRegisteredException if the command already exists.
	 * @throws RuntimeException if the command does not match the regex.
	 */
	public static void register(@NotNull String commandName, @NotNull Command command) throws CommandAlreadyRegisteredException {
		
		if(REGISTRAR.containsKey(commandName))
			throw new CommandAlreadyRegisteredException();
		
		if(!commandName.matches("^[a-z0-9_-]*$"))
			throw new RuntimeException("Command does not match regex!");
		
		REGISTRAR.put(commandName, command);
	}
	
}
