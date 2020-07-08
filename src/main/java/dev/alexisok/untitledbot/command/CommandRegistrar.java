package dev.alexisok.untitledbot.command;

import dev.alexisok.untitledbot.Main;
import dev.alexisok.untitledbot.data.UserData;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;
import java.util.Properties;

/**
 * @author AlexIsOK
 * @since 0.0.1
 */
public class CommandRegistrar {
	
	private static final HashMap<String, Command> REGISTRAR = new HashMap<>();
	
	//commandName, permission
	private static final HashMap<String, String> PERMS_REGISTRAR = new HashMap<>();
	
	/**
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
		if(!permission.matches("^[a-z]([a-z][.]?)+[a-z]$") && !permission.equals("admin")) //this took too long to make...
			throw new RuntimeException("Command permission does not match regex!");
		
		REGISTRAR.put(commandName, command);
		PERMS_REGISTRAR.put(commandName, permission);
	}
	
	/**
	 * Run a command.  This can be invoked by plugins
	 * so there can be more creative plugins.
	 * 
	 * @param commandName the name of the command to execute.
	 * @return the return String.  Returns {@code null} if the command was not found.
	 */
	public static @Nullable String runCommand(String commandName, String[] args, Message m) {
		
		//return null if the command does not exist.
		if(!REGISTRAR.containsKey(commandName))
			return null;
		
		//if the user is a superuser, execute the command without checking the permission node.
		if(Objects.requireNonNull(m.getMember()).hasPermission(Permission.ADMINISTRATOR))
			return REGISTRAR.get(commandName).onCommand(args, m);
		
		Properties userProps = new Properties();
		Properties guildProps = new Properties();
		String permissionNode = getCommandPermissionNode(commandName);
		
		UserData.checkUserExists(m.getAuthor().getId(), m.getGuild().getId());
		
		try {
			
			//check user permissions and guild permissions at the same time.
			//TODO cache this or something to make it faster...
			guildProps.load(new FileInputStream(Main.DATA_PATH + m.getGuild().getId() + ".properties"));
			userProps.load(new FileInputStream(Main.parsePropertiesLocation(m.getAuthor().getId(), m.getGuild().getId())));
			if(userProps.getProperty(permissionNode).equalsIgnoreCase("true") || guildProps.getProperty(permissionNode).equalsIgnoreCase("true"))
				return REGISTRAR.get(commandName).onCommand(args, m);
		} catch(IOException e) {
			e.printStackTrace();
			return "There was an IOException while obtaining your data.  Please report this.";
		}
		
		//since roles have snowflakes as well, they can be treated as users here.
		try {
			for(Role a : m.getMember().getRoles()) {
				Properties roleProperties = new Properties();
				roleProperties.load(new FileInputStream(Main.parsePropertiesLocation(a.getId(), a.getGuild().getId())));
				if(roleProperties.getProperty(permissionNode).equals("true"))
					return REGISTRAR.get(commandName).onCommand(args, m);
				
			}
		} catch(IOException ignored) {}
		
		return "You do not have permission to execute this command.\nIf this is an error, please have an" +
				       " administrator on the server execute `setperms <@" + m.getAuthor().getId() + ">" +
				       " " + getCommandPermissionNode(commandName) + " true`";
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
		for(String alias : aliases) {
//			register(alias, (PERMS_REGISTRAR.get(command)),
//					((args, message) -> CommandRegistrar.runCommand(alias, args, message)));
			register(alias, PERMS_REGISTRAR.get(command), REGISTRAR.get(command));
		}
	}
	
	/**
	 * Copy a manual page from a command.  This does not need to be an
	 * alias but it is the intended usage of this method.
	 * 
	 * @param originalCommand the command to copy off of
	 * @param aliases the alias command
	 */
	public static void registerAliasManual(@NotNull String originalCommand, @NotNull String...aliases) {
		for(String alias : aliases) {
			Manual.setHelpPage(alias, Manual.getHelpPagesRaw(originalCommand));
		}
	}
	
}
