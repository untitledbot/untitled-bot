package dev.alexisok.untitledbot.command;

import dev.alexisok.untitledbot.Main;
import dev.alexisok.untitledbot.logging.Logger;
import dev.alexisok.untitledbot.modules.basic.atsomeone.AtSomeone;
import dev.alexisok.untitledbot.modules.basic.eightball.EightBall;
import dev.alexisok.untitledbot.modules.rank.Ranks;
import dev.alexisok.untitledbot.modules.vault.Vault;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.Arrays;
import java.util.Objects;

/**
 * @author AlexIsOK
 * @since 0.0.1
 */
public final class CoreCommands {
	
	/**
	 * Register all core commands.  This does not need to be private
	 * because commands can only be registered once.
	 */
	public static void registerCoreCommands() {
		Logger.log("Registering core commands.");
		
		//help command
		CommandRegistrar.register("help", "core.help", ((args, message) -> {
			EmbedBuilder eb = new EmbedBuilder();
			EmbedDefaults.setEmbedDefaults(eb, message);
			try {
				String returnString = Manual.getHelpPages(args[1]);
				String embedStr = returnString == null
						                  ? "Could not find the help page, did you make a typo?"
						                  : returnString;
				eb.setColor(returnString == null ? Color.RED : Color.GREEN);
				eb.addField("Help pages", embedStr, false);
				return eb.build();
			} catch(ArrayIndexOutOfBoundsException ignored) {
				eb.setColor(Color.GREEN);
				eb.addField("Help pages",
						"For help on commands, visit https://alexisok.dev/untitled-bot/commands.\nFor help with specific " + 
								"commands, do `help [command]`",
						false);
				return eb.build();
			}
		}));
		CommandRegistrar.registerAlias("help", "man", "halp");
		CommandRegistrar.register("status", "core.stats", (((args, message) -> {
			EmbedBuilder eb = new EmbedBuilder();
			EmbedDefaults.setEmbedDefaults(eb, message);
			String returnString = "";
			returnString += "JDA status: " + Main.jda.getStatus() + "\n";
			returnString += "Available memory: " + Runtime.getRuntime().freeMemory() + "\n";
			returnString += "Total memory: " + Runtime.getRuntime().totalMemory() + "\n";
			returnString += "Processors: " + Runtime.getRuntime().availableProcessors() + "\n";
			returnString += "Java version: " + Runtime.version() + "\n";
			
			eb.setColor(Color.GREEN);
			eb.addField("Status", returnString, false);
			return eb.build();
		})));
		
		//shutdown the bot.  use screen or pm2 if you want it to restart.
		CommandRegistrar.register("shutdown", "owner", (args, message) -> {
			EmbedBuilder eb = new EmbedBuilder();
			EmbedDefaults.setEmbedDefaults(eb, message);
			try {
				Logger.log("shutting down...\nThank you for using untitled-bot!");
				Logger.log("Exit caused by message " + Arrays.toString(args) + " by user ID " + message.getAuthor().getId());
				System.exit(Integer.parseInt(args[1]));
			} catch(ArrayIndexOutOfBoundsException ignored) {
				Logger.log("shutting down...\nThank you for using untitled-bot!");
				Logger.log("Exit caused by message " + Arrays.toString(args) + " by user ID " + message.getAuthor().getId());
				System.exit(0);
			}
			eb.setColor(Color.RED);
			eb.addField("SHUTDOWN", "Usage: `shutdown [code]`", false);
			return eb.build();
		});
		
		//the permissions command is very important.  It can be skipped for more security,
		//but you won't be able to modify command permissions.
		//Usage: `setperms <user ID|user @|role ID|role @|guild> <permission> <true|false>`
		CommandRegistrar.register("setperms", "admin", ((args, message) -> {
			EmbedBuilder eb = new EmbedBuilder();
			EmbedDefaults.setEmbedDefaults(eb, message);
			//pre command checks
			if(message.getAuthor().isBot()) {
				eb.setColor(Color.RED);
				eb.addField("Permissions", "Bot users are not allowed to execute this command.", false);
				return eb.build();
			}
			if(!Objects.requireNonNull(message.getMember()).hasPermission(Permission.ADMINISTRATOR)) {
				eb.setColor(Color.RED);
				eb.addField("Permissions", "You must be an administrator on the server to execute this command.", false);
				return eb.build();
			}
			try {
				//make sure that the permission is valid.
				if(!args[2].matches("^[a-z]([a-z][.]?)+[a-z]$")) {
					eb.setColor(Color.RED);
					eb.addField("Permissions", "Please enter a valid command permission.", false);
					return eb.build();
				}
				if(message.getMentionedMembers().size() == 1) {
					Member mentionedMember = message.getMentionedMembers().get(0);
					String permission = args[2];
					boolean allow = args[3].equalsIgnoreCase("true");
					
					Vault.storeUserDataLocal(
							mentionedMember.getId(),
							message.getGuild().getId(),
							permission,
							allow ? "true" : "false"
					);
					eb.setColor(Color.GREEN);
					eb.addField("Permissions", "Permissions updated.", false);
					return eb.build();
				} else if(message.getMentionedRoles().size() == 1) {
					String ID = message.getMentionedRoles().get(0).getId();
					return getMessageEmbed(args, message, eb, ID);
				} else if (args[1].matches("^[0-9]+$")) {
					String memberID = args[1];
					return getMessageEmbed(args, message, eb, memberID);
				} else if (args[1].equals("guild")) {
					String permission = args[2];
					boolean allow = args[3].equalsIgnoreCase("true");
					
					Vault.storeUserDataLocal(
							null,
							message.getGuild().getId(),
							permission,
							allow ? "true" : "false"
					);
					eb.setColor(Color.GREEN);
					eb.addField("Permissions", "Permissions updated.", false);
					return eb.build();
				} else {
					eb.setColor(Color.RED);
					eb.addField("Permissions",
							"Usage: `setperms **<user ID|user @|role ID|role @|guild>** <permission> <true|false|1|0>`",
							false);
					return eb.build();
				}
			} catch(ArrayIndexOutOfBoundsException ignored) {
				eb.setColor(Color.RED);
				eb.addField("Permissions",
						"Usage: `setperms <user ID|user @|role ID|role @|guild> <permission> <true|false|1|0>`",
						false);
				return eb.build();
			}
		}));
		
		//invite command
		CommandRegistrar.register("invite", "core.invite", (args, message) -> {
			EmbedBuilder eb = new EmbedBuilder();
			EmbedDefaults.setEmbedDefaults(eb, message);
			
			eb.setColor(Color.GREEN);
			
			//with the unique id, it should work for all three bots.
			eb.addField("Invite link",
					"You can invite the bot to the server using the invite link " +
							"https://discord.com/oauth2/authorize?client_id=" +
							Main.jda.getSelfUser().getId() + "" +
							"&scope=bot&permissions=2146958839\n\nYou can find the invite links for the other versions " +
							"of the bot in the Discord server: https://discord.gg/TVvKkad",
					false);
			
			return eb.build();
		});
		CommandRegistrar.register("about", "core.about", (args, message) -> {
			EmbedBuilder eb = new EmbedBuilder();
			EmbedDefaults.setEmbedDefaults(eb, message);
			
			String returnString = "Hello!  I am a bot made by AlexIsOK!  I do many things (you can see the full list with" +
					                      " the 'help' command) and I am constantly improving as well!  If you have any suggestions" +
					                      " or bugs to report, you can do so at https://github.com/alexisok/untitled-bot/issues\n" +
					                      "\n" +
					                      "For help with the bot, read the documentation, or if you're really confused you can" +
					                      " join the Discord server using the 'invite' command, it's not a very big community" +
					                      " as of now, but it would be great to have more people involved!";
			
			eb.setColor(Color.GREEN);
			eb.addField("", returnString, false);
			return eb.build();
		});
		CommandRegistrar.register("test", "test.test.a", (args, message) -> {
			EmbedBuilder eb = new EmbedBuilder();
			EmbedDefaults.setEmbedDefaults(eb, message);
			eb.setColor(Color.CYAN);
			eb.addField("title", "works i think", false);
			return eb.build();
		});
		CommandRegistrar.registerAlias("setperms", "permissions", "perms", "perm", "pr");
		Logger.log("Core commands have been registered.");
		registerHelp();
		setDefaults();
	}
	
	/**
	 * Get the message embed thing that does stuff.
	 * @param args the arguments for the command
	 * @param message the message itself
	 * @param eb the embed builder
	 * @param ID the ID of the user or role
	 * @return the new embed thing
	 */
	@NotNull
	private static MessageEmbed getMessageEmbed(String @NotNull [] args, @NotNull Message message, @NotNull EmbedBuilder eb, String ID) {
		String permission = args[2];
		boolean allow = args[3].equalsIgnoreCase("true");
		
		Vault.storeUserDataLocal(
				ID,
				message.getGuild().getId(),
				permission,
				allow ? "true" : "false"
		);
		eb.setColor(Color.GREEN);
		eb.addField("Permissions", "Permissions updated.", false);
		return eb.build();
	}
	
	/**
	 * Set default permission nodes.
	 */
	private static void setDefaults() {
		CommandRegistrar.setDefaultPermissionForNode("core.help", true);
		CommandRegistrar.setDefaultPermissionForNode("core.ranks", true);
		CommandRegistrar.setDefaultPermissionForNode("core.stats", true);
		CommandRegistrar.setDefaultPermissionForNode("core.invite", true);
		CommandRegistrar.setDefaultPermissionForNode("core.about", true);
		CommandRegistrar.setDefaultPermissionForNode("module.example.eightball", true);
	}
	
	/**
	 * Register core help pages.
	 */
	private static void registerHelp() {
		Manual.setHelpPage("help", "Get help with a specific command.\nUsage: `man <command>`.");
		Manual.setHelpPage("status", "Get the status of the bot and JVM.");
		Manual.setHelpPage("shutdown", "Shutdown the bot.\nUsage: `shutdown [code]` where code is the optional exit code.");
		Manual.setHelpPage("setperms", "Set the permissions of a user, role, or the entire guild.\nUsage: " +
				                               "setperms <user ID|user @|role ID|role @|guild> <permission> <true|false>");
		Manual.setHelpPage("invite", "Get the invite link for the bot.");
		Manual.setHelpPage("about", "much knowledge");
		Manual.setHelpPage("rank", "Get the current level and XP of a user.\nUsage: " +
				                           "rank [user @ | user ID]\n" +
				                           "leave argument blank for your own stats.");
		
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
