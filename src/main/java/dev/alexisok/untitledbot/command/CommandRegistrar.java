package dev.alexisok.untitledbot.command;

import dev.alexisok.untitledbot.Main;
import dev.alexisok.untitledbot.command.enums.UBPerm;
import dev.alexisok.untitledbot.command.exception.CommandAlreadyRegisteredException;
import dev.alexisok.untitledbot.data.UserData;
import dev.alexisok.untitledbot.logging.Logger;
import dev.alexisok.untitledbot.util.OnCommandReturn;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

/**
 * This is where all commands are registered.  To register the command, you must do the following:
 * 
 * <ul>
 *     <li>
 *         Run {@link CommandRegistrar#register(String, UBPerm, Command)} to register the command.
 *     </li>
 *     <li>
 *         (optional but encouraged) Add the help page: {@link Manual#setHelpPage(String, String)}
 *     </li>
 *     <li>
 *         (optional) Add aliases for the command: {@link CommandRegistrar#registerAlias(String, String...)}<br>
 *     </li>
 * </ul>
 * 
 * All command names (what the user types in on Discord) must match {@code ^[a-z0-9_-]*$}.
 * 
 * @author AlexIsOK
 * @since 0.0.1
 */
public class CommandRegistrar {
	
	private static final HashMap<String, Command> REGISTRAR = new HashMap<>();
	
	//commandName, permission
	private static final HashMap<String, UBPerm> PERMS_REGISTRAR = new HashMap<>();
	
	//the hook registrar.
	private static final ArrayList<MessageHook> HOOK_REGISTRAR = new ArrayList<>();
	
	//command, cooldown in seconds
	private static final HashMap<String, String> COOLDOWN = new HashMap<>();
	
	private static long commandsSent = 0L;
	
	static {
		Properties p = new Properties();
		try {
			p.load(new FileInputStream("./cooldowns.properties"));
			p.forEach((key, value) -> COOLDOWN.put(key.toString(), value.toString()));
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Get the cooldown of a command
	 * @param commandName the name of the command as the user executes it.
	 * @return the cooldown or -1 if the command does not exist/does not have a cooldown.
	 */
	@Contract(pure = true)
	public static String getCommandCooldown(@NotNull String commandName) {
		return COOLDOWN.getOrDefault(commandName, "-1");
	}
	
	/**
	 * Set the cooldown of a specific command.
	 * @param commandName the name of the command as the user executes it.
	 * @param time the time in seconds of the cooldown.
	 */
	public static void setCommandCooldown(@NotNull String commandName, String time) {
		COOLDOWN.put(commandName, time);
		storeCommandCooldown();
	}
	
	private static void storeCommandCooldown() {
		Properties p = new Properties();
		try {
			p.load(new FileInputStream("./cooldowns.properties"));
			p.putAll(COOLDOWN);
			p.store(new FileOutputStream("./cooldowns.properties"), "#cooldown");
		} catch(IOException ignored) {
			Logger.critical("Could not save cooldown to properties file!");
		}
	}
	
	/**
	 * Get the size of the register.  Includes alias commands.
	 * 
	 * @return the size of the registrar.
	 */
	@Contract(pure = true)
	public static int registrarSize() {
		return REGISTRAR.size();
	}
	
	/**
	 * Get the total amount of commands sent while the bot was active.
	 * @return the amount of commands sent through this bot.
	 */
	@Contract(pure = true)
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
	 *                   to execute the command, or {@code owner} for the owner of the bot.  Any other nodes will be ignored.<br>
	 *                   Use {@link CommandRegistrar#register(String, Command)} to register a command that everyone can run.
	 *                   <br>
	 *                   Special cases: you can pass "admin" as the string name to require the user to
	 *                   require the user to have administrator or a role with administrator.
	 * @param command the {@link Command} to use.  {@link Command#onCommand(String[], Message)}
	 *                will be executed when the command is called.
	 * @throws IllegalArgumentException if the command does not match the regex.
	 */
	public static void register(@NotNull String commandName, @NotNull UBPerm permission, @NotNull Command command)
			throws IllegalArgumentException {
		
		if(REGISTRAR.containsKey(commandName))
			throw new CommandAlreadyRegisteredException();
		
		if(!commandName.matches("^[a-z0-9_-]*$"))
			throw new IllegalArgumentException("Command does not match regex!");
		
		REGISTRAR.put(commandName, command);
		PERMS_REGISTRAR.put(commandName, permission);
	}
	
	/**
	 * Register a command.
	 * 
	 * If no permission node is specified, {@link UBPerm#EVERYONE} will be used.
	 * 
	 * @param commandName the name of the command.  Must match {@code ^[a-z0-9_-]*$} (alphanumerical
	 *                    lowercase only plus underscores and hyphens).
	 * @param command the {@link Command} to use.  {@link Command#onCommand(String[], Message)}
	 *                will be executed when the command is called.
	 * @throws CommandAlreadyRegisteredException if the command already exists.
	 * @throws IllegalArgumentException if the command does not match the regex.
	 */
	public static void register(@NotNull String commandName, @NotNull Command command)
			throws CommandAlreadyRegisteredException, IllegalArgumentException {
		
		if(REGISTRAR.containsKey(commandName))
			throw new CommandAlreadyRegisteredException();
		
		if(!commandName.matches("^[a-z0-9_-]*$"))
			throw new IllegalArgumentException("Command does not match regex!");
		
		REGISTRAR.put(commandName, command);
		PERMS_REGISTRAR.put(commandName, UBPerm.EVERYONE);
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
	 * @param r what to run on completion   
	 */
	public static synchronized void runCommand(@NotNull String commandName, @NotNull String[] args, @NotNull Message m, @NotNull OnCommandReturn r) {
		
		commandsSent++;
		TimerTask t = new TimerTask() {
			@Override
			public void run() {
				UBPerm permissionNode = getCommandPermissionNode(commandName);
				Logger.debug("Getting permission node " + permissionNode);
				//return null if the command does not exist.
				if(!REGISTRAR.containsKey(commandName) || permissionNode == null) {
					Logger.debug(String.format("Rejecting key %s because it was either null or did not exist in the registrar.", commandName));
					r.onReturn(null);
					return;
				}
				//owner is a global super user and can access any commands on any servers
				if(m.getAuthor().getId().equals(Main.OWNER_ID)) {
					r.onReturn(REGISTRAR.get(commandName).onCommand(args, m));
					return;
				}
				EmbedBuilder eb = new EmbedBuilder();
				EmbedDefaults.setEmbedDefaults(eb, m);
				
				//don't say anything if the command is owner only
				if(permissionNode.equals(UBPerm.OWNER)) {
					return;
				}
				
				//if the user is an admin, execute the command without checking the permission node.
				if(Objects.requireNonNull(m.getMember()).hasPermission(Permission.ADMINISTRATOR)) {
					r.onReturn(REGISTRAR.get(commandName).onCommand(args, m));
					return;
				}
				
				//manage server
				if(permissionNode.equals(UBPerm.MANAGE_SERVER) && m.getMember().hasPermission(Permission.MANAGE_SERVER)) {
					r.onReturn(eb.build());
					return;
				}
				
				//manage messages
				if(permissionNode.equals(UBPerm.MANAGE_MESSAGES) && m.getMember().hasPermission(Permission.MESSAGE_MANAGE)) {
					r.onReturn(eb.build());
					return;
				}
				
				//global commands
				if(!permissionNode.equals(UBPerm.EVERYONE)) {
					eb.setDescription("You must have the \"" + permissionNode.niceName + "\" permission to run this command.");
					eb.setColor(Color.RED);
					r.onReturn(eb.build());
					return;
				}
				UserData.checkUserExists(m.getAuthor().getId(), m.getGuild().getId());
				UserData.checkUserExists(null, m.getGuild().getId());
				r.onReturn(REGISTRAR.get(commandName).onCommand(args, m));
			}
		};
		
		new Timer().schedule(t, 0L);
		
	}
	
	/**
	 * Check to see if the registrar has a command.
	 * 
	 * @param command the command to check
	 * @return true if the command is registered, false otherwise.
	 */
	@Contract(pure = true)
	public static boolean hasCommand(String command) {
		return REGISTRAR.containsKey(command);
	}
	
	/**
	 * Get the permission node of a command.
	 * 
	 * As of 1.3.22, this is unused.
	 * 
	 * @param command the command
	 * @return the permission node, {@code null} if it is not registered.
	 */
	@Nullable
	@Contract(pure = true)
	public static UBPerm getCommandPermissionNode(String command) {
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
		Arrays.stream(aliases).forEachOrdered(alias -> register(alias, PERMS_REGISTRAR.get(command), REGISTRAR.get(command)));
		Arrays.stream(aliases).forEachOrdered(alias -> Manual.setHelpPage(alias, Manual.getHelpPagesRaw(command)));
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
	public static void runMessageHooks(GuildMessageReceivedEvent event) {
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
	 * Get the {@link Class} of a command.
	 * @param command the command to get
	 * @return the Class
	 */
	@Nullable
	@Contract(pure = true)
	public static Class<? extends Command> getClassOfCommand(String command) {
		return REGISTRAR.get(command).getClass();
	}
	
}
