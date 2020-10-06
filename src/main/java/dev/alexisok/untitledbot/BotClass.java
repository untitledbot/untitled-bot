package dev.alexisok.untitledbot;

import dev.alexisok.untitledbot.command.CommandRegistrar;
import dev.alexisok.untitledbot.data.UserDataFileCouldNotBeCreatedException;
import dev.alexisok.untitledbot.logging.Logger;
import dev.alexisok.untitledbot.modules.vault.Vault;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

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
	
	{
		Logger.log("new instance of BotClass created:  " + this.toString());
	}
	
	private static long messagesSentTotal = 0L;
	
	public static long getMessagesSentTotal() {
		return messagesSentTotal;
	}
	
	public static final ArrayList<String> BLACKLIST = new ArrayList<>();
	
	private static final ArrayList<String> NO_PREFIX = new ArrayList<>();
	
	private static final MessageEmbed JOIN_MESSAGE = new EmbedBuilder().addField("untitled-bot",
			"\n```" +
					"      ╔╗ ╔╗╔╗     ╔╗  ╔╗    ╔╗\n" +
					"     ╔╝╚╦╝╚╣║     ║║  ║║   ╔╝╚╗\n" +
					"╔╗╔╦═╬╗╔╬╗╔╣║╔══╦═╝║  ║╚═╦═╩╗╔╝\n" +
					"║║║║╔╗╣║╠╣║║║║║═╣╔╗╠══╣╔╗║╔╗║║\n" +
					"║╚╝║║║║╚╣║╚╣╚╣║═╣╚╝╠══╣╚╝║╚╝║╚╗\n" +
					"╚══╩╝╚╩═╩╩═╩═╩══╩══╝  ╚══╩══╩═╝\n" +
					"```" +
					"\n\n\n" +
					"Thank you for inviting untitled-bot!\n" +
					"For help with the bot, use `" + Main.PREFIX + "help`\n" +
					"The website for the bot is [here](https://untitled-bot.xyz), and the " +
					"official support server is [here](https://discord.gg/vSWgQ9a).", false).build();
	
	//cache for server prefixes
	private static final HashMap<String, String> PREFIX_CACHE = new HashMap<>();
	
	/**
	 * Nullify the prefix cache for a specific guild.
	 * @param guildID the ID of the guild.
	 */
	public static void nullifyPrefixCacheSpecific(@NotNull String guildID) {
		PREFIX_CACHE.remove(guildID);
	}
	
	/**
	 * Update the cached prefix for a guild.
	 * @param guildID the ID of the guild as a String
	 * @param prefix the prefix of that guild
	 */
	public static void updateGuildPrefix(@NotNull String guildID, @NotNull String prefix) {
		Logger.debug(String.format("Updating prefix cache to include %s for %s", prefix, guildID));
		PREFIX_CACHE.put(guildID, prefix);
		Logger.debug("Prefix cache updated.");
	}
	
	/**
	 * Get the prefix of a guild if it is cached.
	 * 
	 * Attempts to do the following:
	 * 
	 * 1. get the prefix from the cache
	 * 
	 * 2. if the prefix is not in the cache, load it
	 * 
	 * 3. if the prefix does not exist, use ">"
	 * 
	 * @param guildID the ID of the guild.
	 * @param userID the ID of the user.  (Make {@code null} to skip user prefix).
	 * @return the prefix.
	 */
	@NotNull
	@Contract(pure = true)
	public static String getPrefix(@NotNull String guildID, String userID) {
		if(userID != null && NO_PREFIX.contains(userID))
			return "";
		String prefix;
		if(!PREFIX_CACHE.containsKey(guildID)) {
			prefix = Vault.getUserDataLocalOrDefault(null, guildID, "guild.prefix", ">");
			updateGuildPrefix(guildID, prefix);
		} else {
			prefix = PREFIX_CACHE.get(guildID);
		}
		
		return prefix == null ? ">" : prefix;
	}
	
	/**
	 * Add a user to the no-prefix list.
	 * 
	 * @param userID the ID of the user as a String.
	 * @return {@code true} if the user was added, {@code false} if they were already added.
	 */
	public static boolean addToNoPrefix(@NotNull String userID) {
		if(NO_PREFIX.contains(userID))
			return false;
		return NO_PREFIX.add(userID);
	}
	
	/**
	 * Removes a user from the no-prefix list.
	 * @param userID the ID of the user as a String.
	 * @return {@code true} if they were removed, {@code false} if they were not removed.
	 */
	public static boolean removeFromNoPrefix(@NotNull String userID) {
		if(!NO_PREFIX.contains(userID))
			return false;
		return NO_PREFIX.remove(userID);
	}
	
	/**
	 * This is messy...
	 * @param event the mre
	 */
	@Override
	public final void onMessageReceived(@Nonnull MessageReceivedEvent event) {
		
		messagesSentTotal++;
		
		CommandRegistrar.runMessageHooks(event);
		
		if(!event.isFromGuild() || event.getAuthor().isBot() || event.isWebhookMessage())
			return;
		
		if(BLACKLIST.contains(event.getAuthor().getId()))
			return;
		
		//get the prefix of the guild
		String prefix = getPrefix(event.getGuild().getId(), event.getAuthor().getId());
		
		String message = event.getMessage().getContentRaw();
		
		if(message.startsWith(prefix + " "))
			message = message.replaceFirst(prefix + " ", prefix);
		
		try {
			//if the message is @untitled-bot
			if(event.getMessage().getMentionedMembers().get(0).getId().equals("730135989863055472")
					    && message.split(" ").length == 1) {
				
				event.getChannel()
						.sendMessage(String.format("Hello!  My prefix for this guild is `%s`.%n" +
								                           "For a full list of commands, use `%shelp` or `%s help`.%n" +
								                           "The default prefix is `>` and can be set by an administrator " +
								                           "on this server by using the `prefix` command.", prefix, prefix, prefix))
						.queue();
				return;
			}
		} catch(IndexOutOfBoundsException ignored) {}
		
		if(!event.getMessage().getContentRaw().startsWith(prefix) || event.getMessage().getContentRaw().equals(prefix))
			return;
		
		message = message.substring(prefix.length());
		
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
	
	@Override
	public void onGuildJoin(@Nonnull GuildJoinEvent e) {
		List<TextChannel> ch = e.getGuild().getTextChannels();
		
		boolean found = false;
		
		try {
			for (TextChannel tc : ch) {
				if (tc.getName().contains("bot")) {
					found = true;
					tc.sendMessage(JOIN_MESSAGE).queueAfter(3000, TimeUnit.MILLISECONDS);
					break;
				}
			}
			if(!found)
				throw new UserDataFileCouldNotBeCreatedException();
		} catch(InsufficientPermissionException | UserDataFileCouldNotBeCreatedException ignored) {
			try {
				Objects.requireNonNull(e.getGuild().getDefaultChannel()).sendMessage(JOIN_MESSAGE).queueAfter(3000, TimeUnit.MILLISECONDS);
			} catch(InsufficientPermissionException | NullPointerException ignored2) {
				Logger.debug(String.format("Could not send a welcome message to %s.", e.getGuild().getId()));
			}
		}
	}
	
}
