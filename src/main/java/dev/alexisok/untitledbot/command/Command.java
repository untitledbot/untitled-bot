package dev.alexisok.untitledbot.command;

import net.dv8tion.jda.api.entities.Message;

/**
<<<<<<< HEAD
=======
 * A command for use in {@link CommandRegistrar}
 * 
>>>>>>> 82fb7c4b5c9a7267a0af98aa4e5d65d3bb285c27
 * @author AlexIsOK
 * @since 0.0.1
 */
public interface Command {
	
	/**
<<<<<<< HEAD
	 * Runs when the command is activated.
	 * 
	 * @param command the command that is being executed - this should be
	 *                the same every time.   
	 * @param arguments the arguments for the command NOT including the
	 *                  command itself.   
	 * @param msg the {@link Message} that was sent (this allows the
	 *            plugin to get other data from the message, such
	 *            as user, time, etc.)
	 */
	void onCommand(final String command, final String[] arguments, final Message msg);
=======
	 * Executes when the command is run.
	 * 
	 * @param args arguments for the command.
	 *             The first argument is always the name of
	 *             the command.  Arguments are the discord
	 *             message separated by spaces.
	 * @param message the {@link Message} that can be used
	 *                to get information from the user
	 *                (such as their Discord snowflake ID)
	 */
	void onCommand(String[] args, Message message);
>>>>>>> 82fb7c4b5c9a7267a0af98aa4e5d65d3bb285c27
	
}
