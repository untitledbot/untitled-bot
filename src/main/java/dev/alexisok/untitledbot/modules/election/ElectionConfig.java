package dev.alexisok.untitledbot.modules.election;

import dev.alexisok.untitledbot.command.CommandRegistrar;
import dev.alexisok.untitledbot.command.EmbedDefaults;
import dev.alexisok.untitledbot.command.Manual;
import dev.alexisok.untitledbot.command.UBPerm;
import dev.alexisok.untitledbot.plugin.UBPlugin;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

/**
 * @author AlexIsOK
 * @since 1.3.23
 */
public final class ElectionConfig extends UBPlugin {
    
    @Override
    public @NotNull MessageEmbed onCommand(String[] args, @NotNull Message message) {
        EmbedBuilder eb = new EmbedBuilder();
        EmbedDefaults.setEmbedDefaults(eb, message);
        
        if(args.length == 1 || args.length == 2) {
            eb.addField("Election Config", "Please select a sub-command through `help election-config` or " +
                    "use the website for a list with examples: https://untitled-bot.xyz/elections.html", false);
            eb.setColor(Color.RED);
            return eb.build();
        }
        
        switch(args[1]) {
            case "reset":
                if(!ElectionKernel.INSTANCE.hasElection(message.getGuild()))
                    return eb.addField("Election Config", "There isn't an ongoing election.", false).build();
                if(!ElectionKernel.INSTANCE.isStopped(message.getGuild().getId()))
                    return eb.addField("Election Config", "The current election is still running, do `election-config stop` to stop it.", false).build();
                if(args.length != 3 || !args[2].equalsIgnoreCase("confirm"))
                    return eb.addField("Election Config", "Are you sure you want to reset the election?\n" +
                            "Do `election-config reset confirm` to reset.\n" +
                            "**NOTE**: this will delete ALL DATA for this server's election.\n" +
                            "THIS ACTION CANNOT BE UNDONE!!!!!  MAKE SURE YOU'RE 100% SURE YOU WANT TO DO THIS!", false).build();
                if(ElectionKernel.INSTANCE.resetElection(message.getGuild()))
                    return eb.addField("Election Config", "Done!  Election data for this server has been erased.", false).build();
                return eb.addField("Election Config", "Could not delete the election data, please try again.", false).build();
                
            case "vote-change":
                if(args.length != 3 || !args[2].matches("[0-9]{1,2}"))
                    return eb.addField("Election Config", "Usage: `election-config vote-change <number 0-99>`", false).build();
                if(!ElectionKernel.INSTANCE.hasElection(message.getGuild()))
                    return eb.addField("Election Config", "There isn't an ongoing election.", false).build();
                if(ElectionKernel.INSTANCE.setChangeCount(message.getGuild(), Integer.parseInt(args[2])))
                    return eb.addField("Election Config", "The vote change count has been set to " + args[2] + ".", false).build();
                return eb.addField("Election Config", "Could not save to the file.  If this happens again, please report it.", false).build();
                
            case "closed": case "close":
                if(args.length != 3 || !args[2].matches("(?i)(true|false)"))
                    return eb.addField("Election Config", "Usage: `election-config closed <true/false>`", false).build();
                if(!ElectionKernel.INSTANCE.hasElection(message.getGuild()))
                    return eb.addField("Election Config", "There isn't an ongoing election.", false).build();
                if(ElectionKernel.INSTANCE.close(message.getGuild(), Boolean.parseBoolean(args[2])))
                    return eb.addField("Election Config", "The election has been closed!  No more candidates can join!\nTo stop people from voting, use `election-config end true`.", false).build();
                return eb.addField("Election Config", "Could not save to the file.  If this happens again, please report it.", false).build();
                
            case "end":
                if(args.length != 3 || !args[2].matches("(?i)(true|false)"))
                    return eb.addField("Election Config", "Usage: `election-config end <true/false>`", false).build();
                if(!ElectionKernel.INSTANCE.hasElection(message.getGuild()))
                    return eb.addField("Election Config", "There isn't an ongoing election.", false).build();
                if(ElectionKernel.INSTANCE.close(message.getGuild(), Boolean.parseBoolean(args[2])))
                    return eb.addField("Election Config", "The eleciton has been ended, no more votes can be cast!\n" +
                            "Use `election-config stop` to stop the election, then `eleciton-results` to get the results.", false).build();
                return eb.addField("Election Config", "Could not save to the file.  If this happens again, please report it.", false).build();
                
            case "stop":
                if(args.length != 3 || !args[2].equalsIgnoreCase("confirm"))
                    return eb.addField("Election Config", "Are you sure you want to stop the election?  You will have to reset the election " +
                            "if you want to start a new one.  Use `election-config stop confirm` to confirm this action.", false).build();
                if(ElectionKernel.INSTANCE.stop(message.getGuild()))
                    return eb.addField("Election Config", "Election has been stopped!  Use `election-results` to view the results of the election.", false).build();
                return eb.addField("Election Config", "Election could not be stopped, please try again.", false).build();
                
            case "allowed-join":
                if(args.length < 4 || !args[2].matches("(?i)(true|false)"))
                    return eb.addField("Election Config", "Usage: `election-config allowed-join <true/false> <role name | role @ | everyone>", false).build();
                {
                Role target = null;
                    
                if (args[3].equalsIgnoreCase("everyone")) {
                    ElectionKernel.INSTANCE.saveElectionData("role.everyone", args[2], message.getGuild().getId());
                    if(args[2].equalsIgnoreCase("true"))
                        return eb.addField("Election Config", "Done!  Everyone is allowed to be a candidate in this election.\n" +
                                "Do `election-config allowed-join everyone false` to reverse this.", false).build();
                    else
                        return eb.addField("Election Config", "Got it!  Not everyone is allowed to join the election, only those" +
                                " with the specified role(s).\nDo `election-config list` to list the configuration.", false).build();
                }
                    
                //id
                if (args[3].matches("[0-9]+"))
                    target = message.getGuild().getRoleById(args[3]);
                    
                //mention
                //check if there is a mentioned role AND if the role exists in this guild
                if (target == null && message.getMentionedRoles().size() == 1 && message.getGuild().getRoleById(message.getMentionedRoles().get(0).getId()) != null) {
                    target = message.getGuild().getRoleById(message.getMentionedRoles().get(0).getId());
                }
                    
                //name
                if (target == null) {
                    //election-config allowed-join true role
                    String[] nameArgs = args.clone();
                    ArrayUtils.shift(nameArgs, 3);
                    java.util.List<Role> roles = message.getGuild().getRolesByName(String.join(" ", nameArgs), true);
                    if (roles.size() != 0)
                        target = roles.get(0);
                }
                    
                if (target == null) {
                    return eb.addField("Election Config", "Could not find a role by that name, mention, or ID.  Are you sure it exists?\n" +
                            "Note: roles by name MUST be the entire name, not portions of it.\n" +
                            "Use the `roles` command to get a list of roles and their IDs.", false).build();
                }
                ElectionKernel.INSTANCE.saveElectionData("role." + target.getId(), args[2], message.getGuild().getId());
                eb.addField("Election Config", String.format("The role %s can%s join this election as a candidate.\n" +
                                "Note: this action is a block > allow action, meaning no users but the allowed users are allowed to join.",
                        target.getAsMention(),
                        args[2].equalsIgnoreCase("true") ? "" : "not"), false);
                return eb.build();
                }
                
            case "allowed-vote":
                if(args.length < 4 || !args[2].matches("(?i)(true|false)"))
                    return eb.addField("Election Config", "Usage: `election-config allowed-vote <true/false> <role name | role @ | everyone>", false).build();
                Role target = null;
                
                if(args[3].equalsIgnoreCase("everyone")) {
                    ElectionKernel.INSTANCE.saveElectionData("vote.everyone", args[2], message.getGuild().getId());
                    if(args[2].equalsIgnoreCase("true"))
                        return eb.addField("Election Config", "Done!  Everyone is allowed to vote in this election.\n" +
                                "Do `election-config allowed-join everyone false` to reverse this.", false).build();
                    else
                        return eb.addField("Election Config", "Got it!  Not everyone is allowed to vote in this election, only those" +
                                " with the specified role(s).\nDo `election-config list` to list the configuration.", false).build();
                }
                
                //id
                if(args[3].matches("[0-9]+"))
                    target = message.getGuild().getRoleById(args[3]);
                
                //mention
                //check if there is a mentioned role AND if the role exists in this guild
                if(target == null && message.getMentionedRoles().size() == 1 && message.getGuild().getRoleById(message.getMentionedRoles().get(0).getId()) != null) {
                    target = message.getGuild().getRoleById(message.getMentionedRoles().get(0).getId());
                }
                
                //name
                if(target == null) {
                    //election-config allowed-join true role
                    String[] nameArgs = args.clone();
                    ArrayUtils.shift(nameArgs, 3);
                    java.util.List<Role> roles = message.getGuild().getRolesByName(String.join(" ", nameArgs), true);
                    if(roles.size() != 0)
                        target = roles.get(0);
                }
                
                if(target == null) {
                    return eb.addField("Election Config", "Could not find a role by that name, mention, or ID.  Are you sure it exists?\n" +
                            "Note: roles by name MUST be the entire name, not portions of it.\n" +
                            "Use the `roles` command to get a list of roles and their IDs.", false).build();
                }
                ElectionKernel.INSTANCE.saveElectionData("role." + target.getId(), args[2], message.getGuild().getId());
                eb.addField("Election Config", String.format("The role %s can%s vote in this election.\n" +
                                "Note: this action is a allow > block action, meaning everyone is allowed to vote unless otherwise specified.\n" +
                                "To disallow everyone to vote except roles you choose, do `election-config allowed-vote true everyone`.",
                        target.getAsMention(),
                        args[2].equalsIgnoreCase("true") ? "" : "not"), false);
                return eb.build();
                
            case "remove":
                if(message.getMentionedMembers().size() != 1)
                    return eb.addField("Election Config", "Usage: `election-config remove <user @>`", false).build();
                ElectionKernel.INSTANCE.saveElectionData("false" ,"candidate." + message.getMentionedMembers().get(0).getId(), message.getGuild().getId());
                ElectionKernel.INSTANCE.saveElectionData("0" ,"candidate." + message.getMentionedMembers().get(0).getId() + ".votes", message.getGuild().getId());
                return eb.addField("Election Config", "Done!  This user has been removed from the election and their votes have been voided.", false).build();
                
                //TODO list command
        }
        return eb.addField("Election Config", "Unknown command " + args[1] + ", do `help election-config` for help.", false).build();
    }
    
    @Override
    public void onRegister() {
        CommandRegistrar.register("election-config", UBPerm.ADMIN, this);
        Manual.setHelpPage("election-config", "Configure an election.\n" +
                "Sub-commands:\n" +
                "`election-config reset` - reset the election statistics (only works for elections that have ended).\n" +
                "`election-config vote-change <number>` - set the amount of times users can change their vote.\n" +
                "`election-config closed <true/false>` - set if the election should be closed or not (if more people can join).\n" +
                "`election-config end <true/false>` - if people can still vote; election must be closed to use this.\n" +
                "`election-config stop` - stop an election and make the data immutable." +
                "`election-config allowed-join <true/false> <role name | @role | everyone>` - set users who are allowed to join by their role.\n" +
                "`election-config allowed-vote <true/false> <role name | @role | everyone>` - set users who are allowed to vote in the election.\n" +
                "`election-config remove <user @>` - remove a user from being a candidate.\n" +
                "`election-config list` - list all config for this election.\n\n" +
                "For detailed help with examples, check the official site: https://untitled-bot.xyz/elections.html");
    }
}
