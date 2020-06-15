package dev.alexisok.untitledbot.command;

<<<<<<< HEAD
=======
import net.dv8tion.jda.api.entities.Message;

import java.util.HashMap;

>>>>>>> 82fb7c4b5c9a7267a0af98aa4e5d65d3bb285c27
/**
 * @author AlexIsOK
 * @since 0.0.1
 */
public class CommandRegistrar {
	
<<<<<<< HEAD
	/**
	 * Register a command to be used by the 
	 * @param commandName
	 * @param command
	 */
	public static void register(String commandName, Command command) {
		
=======
	private static final HashMap<String, Command> registrar = new HashMap<>();
	
	/**
	 * Register a command.
	 * @param commandName the name of the command.  Must match "^[a-z0-9_-]*$" (alphanumerical
	 *                    lowercase only plus underscores and hyphens).   
	 * @param command the {@link Command} to use.  {@link Command#onCommand(String[], Message)}
	 *                will be executed when the command is called.
	 * @throws CommandAlreadyRegisteredException if the command already exists.
	 * @throws RuntimeException if the command does not match the regex.
	 */
	public static void register(String commandName, Command command) throws CommandAlreadyRegisteredException {
		if(registrar.containsKey(commandName))
			throw new CommandAlreadyRegisteredException();
		
		if(!commandName.matches("^[a-z0-9_-]*$"))
			throw new RuntimeException("Command does not match regex!");
		
		registrar.put(commandName, command);
>>>>>>> 82fb7c4b5c9a7267a0af98aa4e5d65d3bb285c27
	}
	
}
