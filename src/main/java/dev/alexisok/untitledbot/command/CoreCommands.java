package dev.alexisok.untitledbot.command;

import dev.alexisok.untitledbot.Main;
import dev.alexisok.untitledbot.logging.Logger;
import dev.alexisok.untitledbot.modules.basic.atsomeone.AtSomeone;
import dev.alexisok.untitledbot.modules.basic.eightball.EightBall;
import dev.alexisok.untitledbot.modules.rank.Ranks;
import dev.alexisok.untitledbot.modules.vault.Vault;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;

import java.util.Arrays;
import java.util.Objects;

/**
 * @author AlexIsOK
 * @since 0.0.1
 */
public class CoreCommands {
	
	/**
	 * Register all core commands.  This does not need to be private
	 * because commands can only be registered once.
	 */
	public static void registerCoreCommands() {
		Logger.log("Registering core commands.");
		
		//help command
		CommandRegistrar.register("help", "core.help", ((args, message) -> {
			try {
				String returnString = Manual.getHelpPages(args[1]);
				return returnString == null ? "Could not find the help page, did you make a typo?" : returnString;
			} catch(ArrayIndexOutOfBoundsException ignored) {
				return "For help on commands, visit https://alexisok.dev/untitled-bot/commands.\nFor help with specific " +
						       "commands, do `help [command]`";
			}
		}));
		CommandRegistrar.registerAlias("help", "man", "halp");
		CommandRegistrar.register("status", "core.stats", (((args, message) -> {
			String returnString = "";
			returnString += "JDA status: " + Main.jda.getStatus() + "\n";
			returnString += "Available memory: " + Runtime.getRuntime().freeMemory() + "\n";
			returnString += "Total memory: " + Runtime.getRuntime().totalMemory() + "\n";
			returnString += "Processors: " + Runtime.getRuntime().availableProcessors() + "\n";
			returnString += "Java version: " + Runtime.version() + "\n";
			
			return returnString;
		})));
		
		//shutdown the bot.  use screen or pm2 if you want it to restart.
		CommandRegistrar.register("shutdown", "owner", (args, message) -> {
			try {
				Logger.log("shutting down...\nThank you for using untitled-bot!");
				Logger.log("Exit caused by message " + Arrays.toString(args) + " by user ID " + message.getAuthor().getId());
				System.exit(Integer.parseInt(args[1]));
			} catch(ArrayIndexOutOfBoundsException ignored) {
				Logger.log("shutting down...\nThank you for using untitled-bot!");
				Logger.log("Exit caused by message " + Arrays.toString(args) + " by user ID " + message.getAuthor().getId());
				System.exit(0);
			}
			return "Usage: `shutdown [code]`";
		});
		
		//the permissions command is very important.  It can be skipped for more security,
		//but you won't be able to modify commands.
		//Usage: `setperms <user ID|user @|role ID|role @|guild> <permission> <true|false>`
		CommandRegistrar.register("setperms", "admin", ((args, message) -> {
			//pre command checks
			if(message.getAuthor().isBot())
				return "Bot users are not allowed to execute this command.";
			if(!Objects.requireNonNull(message.getMember()).hasPermission(Permission.ADMINISTRATOR))
				return "You must be an administrator on the server to execute this command.";
			try {
				//make sure that the permission is valid.
				if (!args[2].matches("^[a-z]([a-z][.]?)+[a-z]$"))
					return "Please enter a valid command permission.";
				if (message.getMentionedMembers().size() == 1) {
					Member mentionedMember = message.getMentionedMembers().get(0);
					String permission = args[2];
					boolean allow = args[3].equalsIgnoreCase("true");
					
					Vault.storeUserDataLocal(
							mentionedMember.getId(),
							message.getGuild().getId(),
							permission,
							allow ? "true" : "false"
					);
					return "Permissions updated.";
				} else if (args[1].matches("^[0-9]+$")) {
					String memberID = args[1];
					String permission = args[2];
					boolean allow = args[3].equalsIgnoreCase("true");
					
					Vault.storeUserDataLocal(
							memberID,
							message.getGuild().getId(),
							permission,
							allow ? "true" : "false"
					);
					return "Permissions updated.";
				} else if (args[1].equals("guild")) {
					String permission = args[2];
					boolean allow = args[3].equalsIgnoreCase("true");
					
					Vault.storeUserDataLocal(
							null,
							message.getGuild().getId(),
							permission,
							allow ? "true" : "false"
					);
					return "Permissions updated.";
				} else {
					return "Usage: `setperms **<user ID|user @|role ID|role @|guild>** <permission> <true|false|1|0>`";
				}
			} catch(ArrayIndexOutOfBoundsException ignored) {
				return "Usage: `setperms <user ID|user @|role ID|role @|guild> <permission> <true|false|1|0>`";
			}
		}));
		CommandRegistrar.registerAlias("setperms", "permissions", "perms", "perm", "pr");
		Logger.log("Core commands have been registered.");
		registerHelp();
		setDefaults();
	}
	
	private static void setDefaults() {
		CommandRegistrar.setDefaultPermissionForNode("help", true);
		CommandRegistrar.setDefaultPermissionForNode("man", true);
		CommandRegistrar.setDefaultPermissionForNode("halp", true);
		CommandRegistrar.setDefaultPermissionForNode("rank", true);
		CommandRegistrar.setDefaultPermissionForNode("help", true);
		CommandRegistrar.setDefaultPermissionForNode("help", true);
		CommandRegistrar.setDefaultPermissionForNode("help", true);
		CommandRegistrar.setDefaultPermissionForNode("help", true);
	}
	
	/**
	 * Register core help pages.
	 */
	private static void registerHelp() {
		Manual.setHelpPage("help", "Get help with a specific command.  Usage: `man <command>`.");
		Manual.setHelpPage("status", "Get the status of the bot and JVM.");
		Manual.setHelpPage("shutdown", "Shutdown the bot.  Usage: `shutdown [code]` where code is the optional exit code.");
		Manual.setHelpPage("setperms", "Set the permissions of a user, role, or the entire guild.  Usage: " +
				                               "`setperms <user ID|user @|role ID|role @|guild> <permission> <true|false>`");
		CommandRegistrar.registerAliasManual("shutdown", "stop", "exit");
		CommandRegistrar.registerAliasManual("help", "man", "halp");
		CommandRegistrar.registerAliasManual("setperms", "permissions", "perms", "perm", "pr");
	}
	
	public static void registerModules() {
		Logger.log("Registering modules.");
		new EightBall().onRegister();
		new AtSomeone().onRegister();
		new Ranks().onRegister();
		Logger.log("Modules have been registered.");
	}
}
