package dev.alexisok.untitledbot;

import dev.alexisok.untitledbot.command.CommandRegistrar;
import dev.alexisok.untitledbot.modules.vault.Vault;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;
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
	
	/**
	 * This is messy...
	 * @param event the mre
	 */
	@Override
	public final void onMessageReceived(@Nonnull MessageReceivedEvent event) {
		
		messagesSentTotal++;
		
		CommandRegistrar.runMessageHooks(event);
		
		if(!event.isFromGuild())
			return;
		
		//get the prefix of the guild
		String prefix = Vault.getUserDataLocal(null, event.getGuild().getId(), "guild.prefix");
		
		String message = event.getMessage().getContentRaw();
		
		//if the first mention is the bot
		if(event.getMessage().getMentionedUsers().size() != 0 && event.getMessage().getMentionedMembers().get(0).getId().equals(Main.jda.getSelfUser().getId()))
			message = message.substring(message.indexOf(" ") + 1);
		else if(prefix == null || prefix.equals("")) {
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
		} catch(NullPointerException ignored){} //this returns null if the command does not exist.
		//needs to be caught to avoid flooding logs.
	}
	
	@Override public final void onGuildMessageReceived(@Nonnull GuildMessageReceivedEvent event) {}
	@Override public final void onPrivateMessageReceived(@Nonnull PrivateMessageReceivedEvent event) {}
	
	@Override
	public void onGenericEvent(@Nonnull GenericEvent event) {
		CommandRegistrar.runGenericListeners(event);
	}
	
}
