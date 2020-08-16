package dev.alexisok.untitledbot.command;

import dev.alexisok.untitledbot.BotClass;
import dev.alexisok.untitledbot.Main;
import dev.alexisok.untitledbot.annotation.ToBeRemoved;
import dev.alexisok.untitledbot.data.UserData;
import dev.alexisok.untitledbot.logging.Logger;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;

/**
 * This is where all commands are registered.  To register the command, you must do the following:
 * 
 * <ul>
 *     <li>
 *         Run {@link CommandRegistrar#register(String, String, Command)} to register the command.
 *     </li>
 *     <li>
 *         (optional but encouraged) Add the help page: {@link Manual#setHelpPage(String, String)}
 *     </li>
 *     <li>
 *         (optional) Add aliases for the command: {@link CommandRegistrar#registerAlias(String, String...)}<br>
 *     </li>
 * </ul>
 * 
 * Commands must have a permission node.  The permission nodes must follow a regex of {@code ^[a-z]([a-z][.]?)+[a-z]$},
 * and the command names (what the user types in on Discord) must match {@code ^[a-z0-9_-]*$}.
 * 
 * @author AlexIsOK
 * @since 0.0.1
 */
public class CommandRegistrar {
	
	public CommandRegistrar() {}
	
	private final HashMap<String, Command> REGISTRAR = new HashMap<>();
	
	//commandName, permission
	private static final HashMap<String, String> PERMS_REGISTRAR = new HashMap<>();
	
	//the hook registrar.
	private static final ArrayList<MessageHook> HOOK_REGISTRAR = new ArrayList<>();
	
	private static long commandsSent = 0L;
	
	/**
	 * ...this used to be used for something...
	 * 
	 * @return the size of the registrar.
	 */
	public int registrarSize() {
		return REGISTRAR.size();
	}
	
	/**
	 * @return the amount of commands sent through this bot.
	 */
	public static long getTotalCommands() {
		return commandsSent;
	}
	
	/**
	 * Register a command.
	 * 
	 * The {@code permission} parameter is here for legacy reasons, but it may change in future releases.
	 * 
	 * @param commandName the name of the command.  Must match {@code ^[a-z0-9_-]*$} (alphanumerical
	 *                    lowercase only plus underscores and hyphens).   
	 * @param permission If the permission node is {@code admin}, users will need the Discord administrator permission
	 *                   to execute the command, or {@code owner} for the owner of the bot.  Any other nodes will throw an exception.<br>
	 *                   Use {@link CommandRegistrar#register(String, Command)} to register a command that everyone can run.    
	 *                   <br>
	 *                   Special cases: you can pass "admin" as the string name to require the user to
	 *                   require the user to have administrator or a role with administrator.
	 * @param command the {@link Command} to use.  {@link Command#onCommand(String[], Message)}
	 *                will be executed when the command is called.
	 * @throws CommandAlreadyRegisteredException if the command already exists.
	 * @throws RuntimeException if the command does not match the regex.
	 */
	public static void register(@NotNull String commandName, String permission, @NotNull Command command)
			throws CommandAlreadyRegisteredException {
		
		for(CommandRegistrar r : BotClass.getRegistrars()) {
			if (r.REGISTRAR.containsKey(commandName))
				throw new CommandAlreadyRegisteredException();
			
			if (!commandName.matches("^[a-z0-9_-]*$"))
				throw new RuntimeException("Command does not match regex!");
			if (!permission.matches("^[a-z]([a-z][.]?)+[a-z]$") && !permission.equals("admin") && !permission.equals("owner")) //this took too long to make...
				throw new RuntimeException("Command permission does not match regex!");
			
			r.REGISTRAR.put(commandName, command);
			PERMS_REGISTRAR.put(commandName, permission);
		}
	}
	
	/**
	 * Register a command.
	 * @param commandName the name of the command.  Must match {@code ^[a-z0-9_-]*$} (alphanumerical
	 *                    lowercase only plus underscores and hyphens).
	 * @param command the {@link Command} to use.  {@link Command#onCommand(String[], Message)}
	 *                will be executed when the command is called.
	 * @throws CommandAlreadyRegisteredException if the command already exists.
	 * @throws RuntimeException if the command does not match the regex.
	 */
	public static void register(@NotNull String commandName, @NotNull Command command)
			throws CommandAlreadyRegisteredException {
		
		for(CommandRegistrar r : BotClass.getRegistrars()) {
			if (r.REGISTRAR.containsKey(commandName))
				throw new CommandAlreadyRegisteredException();
			
			if (!commandName.matches("^[a-z0-9_-]*$"))
				throw new RuntimeException("Command does not match regex!");
			
			r.REGISTRAR.put(commandName, command);
			PERMS_REGISTRAR.put(commandName, "global");
		}
	}
	
	/**
	 * Run a command.  This can be invoked by plugins
	 * so there can be more creative plugins.
	 * 
	 * This is also the part that checks the permissions of the user.
	 * 
	 * @param commandName the name of the command to execute.
	 * @param args the arguments for the command.
	 * @param m the {@link Message}   
	 * @return the return embed.  Returns {@code null} if the command was not found (or if the command returns null by itself).
	 */
	public @Nullable MessageEmbed runCommand(@NotNull String commandName, @NotNull String[] args, @NotNull Message m) {
		
		commandsSent++;
		
		String permissionNode = getCommandPermissionNode(commandName);
		
		Logger.debug("Getting permission node " + permissionNode);
		
		//return null if the command does not exist.
		if(!REGISTRAR.containsKey(commandName))
			return null;
			
		//owner is a global super user and can access any commands on any servers
		if(m.getAuthor().getId().equals(Main.OWNER_ID))
			return REGISTRAR.get(commandName).onCommand(args, m);
		
		
		EmbedBuilder eb = new EmbedBuilder();
		EmbedDefaults.setEmbedDefaults(eb, m);
		
		if(permissionNode.equalsIgnoreCase("owner")) {
			eb.addField("", "This command can only be run by the owner of the bot.", false);
			eb.setColor(Color.RED);
			return eb.build();
		}
		
		//if the user is a superuser, execute the command without checking the permission node.
		if(Objects.requireNonNull(m.getMember()).hasPermission(Permission.ADMINISTRATOR))
			return REGISTRAR.get(commandName).onCommand(args, m);
		
		
		if(permissionNode.equalsIgnoreCase("admin")) {
			eb.addField("", "This command requires the administrator permission on Discord.", false);
			eb.setColor(Color.RED);
			return eb.build();
		}
		
		
		UserData.checkUserExists(m.getAuthor().getId(), m.getGuild().getId());
		UserData.checkUserExists(null, m.getGuild().getId());
		
		return REGISTRAR.get(commandName).onCommand(args, m);
	}
	
	/**
	 * Check to see if the registrar has a command.
	 * 
	 * @param command the command to check
	 * @return true if the command is registered, false otherwise.
	 */
	public boolean hasCommand(String command) {
		return REGISTRAR.containsKey(command);
	}
	
	/**
	 * Get the permission node of a command.
	 * 
	 * @param command the command
	 * @return the permission node, {@code null} if it is not registered.
	 */
	public @Nullable String getCommandPermissionNode(String command) {
		if(!hasCommand(command))
			return null;
		return PERMS_REGISTRAR.get(command);
	}
	
	
	/**
	 * Register one or more aliases for a command.  When the alias is
	 * run, the actual command will be called instead.
	 *
	 * Aliases are put in the registrar as actual commands and act
	 * as actual commands, though they only call the command they are
	 * aliasing for.
	 *
	 * Alias will inherit the same permission node from the command
	 * it is aliasing.  Aliases also inherit their owners manual.
	 *
	 * @param command the command name.
	 * @param aliases the aliases to give the command.
	 */
	public static void registerAlias(@NotNull String command, @NotNull String... aliases) {
		for(CommandRegistrar r : BotClass.getRegistrars()) {
			Arrays.stream(aliases).forEachOrdered(alias -> register(alias, PERMS_REGISTRAR.get(command), r.REGISTRAR.get(command)));
			Arrays.stream(aliases).forEachOrdered(alias -> Manual.setHelpPage(alias, Manual.getHelpPagesRaw(command)));
		}
	}
	
	/**
	 * 
	 * This method is obsolete, aliases now inherit their 
	 * 
	 * @deprecated since 1.3.8
	 * @see CommandRegistrar#registerAlias(String, String...)
	 * @param originalCommand the command to copy off of
	 * @param aliases the alias command
	 */
	@Deprecated
	@ToBeRemoved("1.4")
	public static void registerAliasManual(@NotNull String originalCommand, @NotNull String...aliases) {
		for(String alias : aliases)
			Manual.setHelpPage(alias, Manual.getHelpPagesRaw(originalCommand));
	}
	
	/**
	 * Run the generic listener hooks.
	 * @param event the event to be passed to the hooks.
	 * @see MessageHook
	 * @see dev.alexisok.untitledbot.modules.rank.Ranks
	 */
	public static void runGenericListeners(GenericEvent event) {
		for(MessageHook mh : HOOK_REGISTRAR)
			mh.onAnyEvent(event);
	}
	
	/**
	 * Run the message listener hooks.
	 * @param event the event to be passed to the hooks.
	 * @see MessageHook
	 * @see dev.alexisok.untitledbot.modules.rank.Ranks
	 */
	public static void runMessageHooks(MessageReceivedEvent event) {
		for(MessageHook mh : HOOK_REGISTRAR)
			mh.onMessage(event);
	}
	
	/**
	 * Add a {@link MessageHook} to this class.
	 * @param mh the hook to be added.
	 */
	public static void registerHook(MessageHook mh) {
		HOOK_REGISTRAR.add(mh);
	}
	
	
}
