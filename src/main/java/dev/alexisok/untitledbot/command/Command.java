package dev.alexisok.untitledbot.command;

import net.dv8tion.jda.api.entities.Message;

/**
 * @author AlexIsOK
 * @since 0.0.1
 */
public interface Command {
	
	/**
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
	
}
