package dev.alexisok.untitledbot;

import dev.alexisok.untitledbot.command.CommandRegistrar;
import dev.alexisok.untitledbot.logging.Logger;
import net.dv8tion.jda.api.events.UpdateEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Objects;

/**
 * This class handles communicating with Discord.
 * This class is not final, as you are allowed to override
 * the methods in this class (besides the message received
 * methods).
 * 
 * @author AlexIsOK
 * @since 0.0.1
 */
public class BotClass extends ListenerAdapter {
	/**
	 * Only allow package-private instances of this class.
	 */
	BotClass() {}
	
	/**
	 * This is messy...
	 * @param event the mre
	 */
	@Override
	public final void onMessageReceived(@Nonnull MessageReceivedEvent event) {
		//TODO rich embed
		
		//if the message does not start with the prefix or the message is only the prefix
		if(!event.getMessage().getContentRaw().startsWith(Main.DEFAULT_PREFIX) || event.getMessage().getContentRaw().equals(Main.DEFAULT_PREFIX))
			return;
		
		//remove the prefix
		String message = event.getMessage().getContentRaw().substring(Main.DEFAULT_PREFIX.length());
		//args...
		String[] args = message.split(" ");
		
		//execute a command and return the message it provides
		event   .getChannel()
				.sendMessage(Objects.requireNonNull(CommandRegistrar.runCommand(args[0], args, event.getMessage())))
				.queue();
	}
	
	@Override public final void onGuildMessageReceived(@Nonnull GuildMessageReceivedEvent event) {}
	@Override public final void onPrivateMessageReceived(@Nonnull PrivateMessageReceivedEvent event) {}
	
	/**
	 * Send a message to a specific guild channel
	 * @param ID the ID of the guild channel
	 * @param message the message
	 */
	public static void sendMessageGuild(String ID, String message) {
		Objects.requireNonNull(Main.jda.getTextChannelById(ID)).sendMessage(message).queue();
	}
	
	/**
	 * Send a message to a specific user
	 * @param ID the user ID
	 * @param message the message
	 */
	public static void sendMessagePrivate(String ID, String message) {
		Objects.requireNonNull(Main.jda.getPrivateChannelById(ID)).sendMessage(message).queue();
	}
}
