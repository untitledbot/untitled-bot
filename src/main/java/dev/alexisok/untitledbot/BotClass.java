package dev.alexisok.untitledbot;

import dev.alexisok.untitledbot.command.CommandRegistrar;
import dev.alexisok.untitledbot.logging.Logger;
import dev.alexisok.untitledbot.modules.vault.Vault;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
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
public final class BotClass extends ListenerAdapter {
	
	/**
	 * Only allow package-private instances of this class.
	 */
	BotClass() {}
	
	private static long messagesSentTotal = 0L;
	
	public static long getMessagesSentTotal() {
		return messagesSentTotal;
	}
	
	//cache for server prefixes
	private static final HashMap<String, String> PREFIX_CACHE = new HashMap<>();
	
	/**
	 * Update the cached prefix for a guild.
	 * @param guildID the ID of the guild as a String
	 * @param prefix the prefix of that guild
	 */
	public static void updateGuildPrefix(String guildID, String prefix) {
		Logger.debug(String.format("Updating prefix cache to include %s for %s", prefix, guildID));
		PREFIX_CACHE.put(guildID, prefix);
		Logger.debug("Prefix cache updated.");
	}
	
	/**
	 * This is messy...
	 * @param event the mre
	 */
	@Override
	public final void onMessageReceived(@Nonnull MessageReceivedEvent event) {
		
		messagesSentTotal++;
		
		CommandRegistrar.runMessageHooks(event);
		
		if(!event.isFromGuild() || event.getAuthor().isBot())
			return;
		
		//get the prefix of the guild
		String prefix;
		
		if(!PREFIX_CACHE.containsKey(event.getGuild().getId())) {
			Logger.debug("Prefix cache was not found.");
			prefix = Vault.getUserDataLocal(null, event.getGuild().getId(), "guild.prefix");
			updateGuildPrefix(event.getGuild().getId(), prefix);
		} else {
			Logger.debug("Prefix cache was found.");
			prefix = PREFIX_CACHE.get(event.getGuild().getId());
		}
		
		String message = event.getMessage().getContentRaw();
		
		if(message.startsWith(prefix + " "))
			message = message.replaceFirst(prefix + " ", prefix);
		
		try {
			if (event.getMessage().getMentionedMembers().get(0).getId().equals(Main.jda.getSelfUser().getId())
					    && message.split(" ").length == 1) {
				
				if (prefix == null)
					prefix = ">";
				
				event.getChannel()
						.sendMessage(String.format("Hello!  My prefix for this guild is `%s`.%n" +
								                           "For a full list of commands, use `%shelp` or `%s help`.%n" +
								                           "The default prefix is `>` and can be set by an administrator " +
								                           "on this server by using the `prefix` command.", prefix, prefix, prefix))
						.queue();
				return;
			}
		} catch(IndexOutOfBoundsException ignored) {}
		
		if(prefix == null || prefix.equals("")) {
			//if the message does not start with the prefix or the message is only the prefix
			if(!event.getMessage().getContentRaw().startsWith(Main.PREFIX) || event.getMessage().getContentRaw().equals(Main.PREFIX))
				return;
			
			message = message.substring(Main.PREFIX.length());
		} else {
			if(!event.getMessage().getContentRaw().startsWith(prefix) || event.getMessage().getContentRaw().equals(prefix))
				return;
			
			message = message.substring(prefix.length());
		}
		
		//replace all "  " with " "
		while(message.contains("  "))
			message = message.replaceAll(" {2}", " ");
		
		//args...
		String[] args = message.split(" ");
		
		//execute a command and return the message it provides
		try {
			event.getChannel()
					.sendMessage((Objects.requireNonNull(CommandRegistrar.runCommand(args[0], args, event.getMessage()))))
					.queue();
		} catch(NullPointerException ignored) { //this returns null if the command does not exist.
		} catch(InsufficientPermissionException ignored) { //if the bot can't send messages (filled up logs before).
			Logger.debug("Could not send a message to a channel.");
		}
		
		//needs to be caught to avoid flooding logs.
	}
	
	@Override public final void onGuildMessageReceived(@Nonnull GuildMessageReceivedEvent event) {}
	@Override public final void onPrivateMessageReceived(@Nonnull PrivateMessageReceivedEvent event) {}
	
	@Override
	public void onGenericEvent(@Nonnull GenericEvent event) {
		CommandRegistrar.runGenericListeners(event);
	}
	
}
