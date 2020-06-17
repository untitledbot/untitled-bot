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
		if(!event.getMessage().getContentRaw().startsWith(Main.DEFAULT_PREFIX))
			return;
		String message = event.getMessage().getContentRaw().replaceFirst(Main.DEFAULT_PREFIX, "");
		String[] args = message.split(" ");
		//noinspection ResultOfMethodCallIgnored
		event.getChannel().sendMessage(Objects.requireNonNull(
				CommandRegistrar.runCommand(args[0], args, event.getMessage())));
	}
	
	@Override public final void onGuildMessageReceived(@Nonnull GuildMessageReceivedEvent event) {}
	@Override public final void onPrivateMessageReceived(@Nonnull PrivateMessageReceivedEvent event) {}
}
