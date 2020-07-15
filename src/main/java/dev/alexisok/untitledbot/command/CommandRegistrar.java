package dev.alexisok.untitledbot.command;

import dev.alexisok.untitledbot.Main;
import dev.alexisok.untitledbot.data.UserData;
import dev.alexisok.untitledbot.logging.Logger;
import dev.alexisok.untitledbot.modules.vault.Vault;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

/**
 * @author AlexIsOK
 * @since 0.0.1
 */
public class CommandRegistrar {
	
	private static final HashMap<String, Command> REGISTRAR = new HashMap<>();
	
	//commandName, permission
	private static final HashMap<String, String> PERMS_REGISTRAR = new HashMap<>();
	
	//default permissions
	private static final HashMap<String, Boolean> GLOBAL_NODES = new HashMap<>();
	
	//the hook registrar.
	private static final ArrayList<MessageHook> HOOK_REGISTRAR = new ArrayList<>();
	
	/**
	 * ...this used to be used for something...
	 * 
	 * @return the size of the registrar.
	 */
	static int registrarSize() {
		return REGISTRAR.size();
	}
	
	/**
	 * Register a command.
	 * @param commandName the name of the command.  Must match {@code ^[a-z0-9_-]*$} (alphanumerical
	 *                    lowercase only plus underscores and hyphens).   
	 * @param permission the permission the command uses.  Permissions are used for members
	 *                   or groups of members, they can only use the command if they have
	 *                   the needed permission.  Must match {@code ^[a-z]([a-z][.]?)+[a-z]$} convention is to start
	 *                   the permission with your plugin name, period, then the command (example:
	 *                   {@code coolplugin.command} or {@code coolplugin.category.command}.
	 *                   Do not have the permission end or start with a period.  Numbers and
	 *                   capital letters do not match.<br>
	 *                   <br>
	 *                   Special cases: you can pass "admin" as the string name to require the user to
	 *                   require the user to have administrator or a role with administrator.
	 * @param command the {@link Command} to use.  {@link Command#onCommand(String[], Message)}
	 *                will be executed when the command is called.
	 * @throws CommandAlreadyRegisteredException if the command already exists.
	 * @throws RuntimeException if the command does not match the regex.
	 */
	public static void register(@NotNull String commandName, @NotNull String permission, @NotNull Command command)
			throws CommandAlreadyRegisteredException {
		
		if(REGISTRAR.containsKey(commandName))
			throw new CommandAlreadyRegisteredException();
		
		if(!commandName.matches("^[a-z0-9_-]*$"))
			throw new RuntimeException("Command does not match regex!");
		if(!permission.matches("^[a-z]([a-z][.]?)+[a-z]$") && !permission.equals("admin") && !permission.equals("owner")) //this took too long to make...
			throw new RuntimeException("Command permission does not match regex!");
		
		REGISTRAR.put(commandName, command);
		PERMS_REGISTRAR.put(commandName, permission);
	}
	
	/**
	 * Run a command.  This can be invoked by plugins
	 * so there can be more creative plugins.
	 * 
	 * This is also the part that checks the permissions of the user.
	 * 
	 * @param commandName the name of the command to execute.
	 * @return the return String.  Returns {@code null} if the command was not found.
	 */
	public static @Nullable MessageEmbed runCommand(String commandName, String[] args, @NotNull Message m) {
		
		String permissionNode = getCommandPermissionNode(commandName);
		
		Logger.debug("Getting permission node " + permissionNode);
		
		//return null if the command does not exist.
		if(!REGISTRAR.containsKey(commandName))
			return null;
		
		//owner is a global super user and can access any commands on any servers
		if(m.getAuthor().getId().equals(Main.OWNER_ID))
			return REGISTRAR.get(commandName).onCommand(args, m);
		
		//if the user is a superuser, execute the command without checking the permission node.
		if(Objects.requireNonNull(m.getMember()).hasPermission(Permission.ADMINISTRATOR))
			return REGISTRAR.get(commandName).onCommand(args, m);
		
		//if the command is a global command
		if(GLOBAL_NODES.containsKey(getCommandPermissionNode(commandName)) &&
				   GLOBAL_NODES.get(getCommandPermissionNode(commandName)))
			return REGISTRAR.get(commandName).onCommand(args, m);
		
		Logger.debug("Permission " + permissionNode + " is not global");
		
		
		UserData.checkUserExists(m.getAuthor().getId(), m.getGuild().getId());
		UserData.checkUserExists(null, m.getGuild().getId());
		
		EmbedBuilder eb = new EmbedBuilder();
		EmbedDefaults.setEmbedDefaults(eb, m);
		
		try {
			//check user permissions and guild permissions at the same time.
			//this could be made faster.
			@SuppressWarnings("ConstantConditions")
			String userHas = Vault.getUserDataLocal(m.getAuthor().getId(), m.getGuild().getId(), permissionNode);
			
			Logger.debug("Checking the permission node of user...");
			
			if (userHas.equalsIgnoreCase("true"))
				return REGISTRAR.get(commandName).onCommand(args, m);
			
		} catch(NullPointerException ignored) {
			//may produce npe if the permission node does not exist.
		}
		
		try {
			String guildHas = Vault.getUserDataLocal(null, m.getGuild().getId(), permissionNode);
			
			Logger.debug("Guild node: " + guildHas);
			
			if (guildHas.equalsIgnoreCase("true"))
				return REGISTRAR.get(commandName).onCommand(args, m);
		} catch(NullPointerException ignored){}
		
		Logger.debug("Checking the permission node of role...");
		
		//since roles have snowflakes as well, they can be treated as users here.
		for(Role a : m.getMember().getRoles()) {
			try {
				UserData.checkUserExists(a.getId(), m.getGuild().getId());
				String roleProperties = Vault.getUserDataLocal(a.getId(), a.getGuild().getId(), permissionNode);
				if (roleProperties.equals("true"))
					return REGISTRAR.get(commandName).onCommand(args, m);
			} catch(NullPointerException ignored) {}
		}
		
		Logger.debug("User does not have permission");
		
		eb.setColor(Color.RED);
		
		if(permissionNode.equals("admin")) {
			eb.addField("untitled-bot", "This command requires the administrator permission on Discord.", false);
			return eb.build();
		} else if(permissionNode.equals("owner")) {
			eb.addField("untitled-bot", "Only the bot owner can use this command.", false);
			return eb.build();
		}
		
		eb.addField("untitled-bot",
				"You do not have permission to execute this command.\nIf this is an error, please have an" +
						" administrator on the server execute `setperms <@" + m.getAuthor().getId() + ">" +
						" " + getCommandPermissionNode(commandName) + " true`",
					false);
		return eb.build();
	}
	
	/**
	 * 
	 * @param command the command to check
	 * @return true if the command is registered, false otherwise.
	 */
	public static boolean hasCommand(String command) {
		return REGISTRAR.containsKey(command);
	}
	
	/**
	 * Get the permission node of a command.
	 * @param command the command
	 * @return the permission node, {@code null} if it is not registered.
	 */
	public static @Nullable String getCommandPermissionNode(String command) {
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
	 * it is aliasing.  You can easily register an alias manual page
	 * using
	 *
	 * @param command the command name.
	 * @param aliases the aliases to give the command.
	 */
	public static void registerAlias(@NotNull String command, @NotNull String... aliases) {
		for(String alias : aliases)
			register(alias, PERMS_REGISTRAR.get(command), REGISTRAR.get(command));
	}
	
	/**
	 * Copy a manual page from a command.  This does not need to be an
	 * alias but it is the intended usage of this method.
	 * 
	 * @param originalCommand the command to copy off of
	 * @param aliases the alias command
	 */
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
	
	
	/**
	 * Set a default permission for ALL users for a specific permission node.
	 * You do not have to be the owner of the node to run this command.  Use with caution.
	 * @param node the node to modify
	 * @param permission the permission
	 */
	public static void setDefaultPermissionForNode(String node, boolean permission) {
		GLOBAL_NODES.put(node, permission);
	}
}
