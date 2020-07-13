package dev.alexisok.untitledbot.command;

import net.dv8tion.jda.api.entities.Message;

/**
 * A command for use in {@link CommandRegistrar}
 * 
 * @author AlexIsOK
 * @since 0.0.1
 */
public interface Command {
	
	/**
	 * Executes when the command is run.  Return {@code null} if
	 * you do not wish for the bot to respond to the command.
	 * 
	 * @param args arguments for the command.
	 *             The first argument is always the name of
	 *             the command.  Arguments are the discord
	 *             message separated by spaces.
	 * @param message the {@link Message} that can be used
	 *                to get information from the user
	 *                (such as their Discord snowflake ID)
	 */
	String onCommand(String[] args, Message message);
	
}
