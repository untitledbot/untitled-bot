package dev.alexisok.untitledbot.modules.rank.rankcommands;

import dev.alexisok.untitledbot.command.CommandRegistrar;
import dev.alexisok.untitledbot.command.EmbedDefaults;
import dev.alexisok.untitledbot.command.Manual;
import dev.alexisok.untitledbot.logging.Logger;
import dev.alexisok.untitledbot.modules.vault.Vault;
import dev.alexisok.untitledbot.plugin.UBPlugin;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.Color;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

import static dev.alexisok.untitledbot.Main.jda;

/**
 * @author AlexIsOK
 * @since 1.3
 */
public class RankRoleSet extends UBPlugin {
    
    @Override
    public @Nullable MessageEmbed onCommand(String[] args, @NotNull Message message) {
        EmbedBuilder eb = new EmbedBuilder();
        EmbedDefaults.setEmbedDefaults(eb, message);
        if(args.length < 3) {
            eb.addField("Rank Roles", "Usage: `rank-role <level> <role @ | role ID | role name | none>`", false);
            eb.setColor(Color.RED);
            return eb.build();
        }
        try {
            if(Integer.parseInt(args[1]) > 65536 || Integer.parseInt(args[1]) <= 0)
                throw new NumberFormatException();
        } catch(NumberFormatException ignored) {
            eb.addField("Error", "The first argument must be a number between 1 and 65536 (inclusive)", false);
            eb.setColor(Color.RED);
            return eb.build();
        }
        Role r;
        
        if(args[2].matches("[0-9]+")) { //id
            r = message.getGuild().getRoleById(args[2]);
            if (r == null) {
                eb.addField("Error", "Could not find a role with that ID!  Do I have access to see it?", false);
                eb.setColor(Color.RED);
                return eb.build();
            }
        } else if(message.getMentionedRoles().size() == 1) { //@mention
            r = message.getMentionedRoles().get(0);
        } else if(args[2].equals("none")) { //none
            r = null;
        } else { //by name
            String searchName = String.join(" ", Arrays.copyOfRange(args, 2, args.length));
            List<Role> roles = message.getGuild().getRolesByName(searchName, true);
            if(roles.size() == 0) {
                eb.addField("Error", "Could not find any roles with that name, is it spelt correctly?", false);
                eb.setColor(Color.RED);
                return eb.build();
            } else if(roles.size() != 1) {
                eb.addField("Error", "I found multiple roles with this name, could you narrow it down or provide the " +
                                             "[ID]" +
                                             "(https://support.discord.com/hc/en-us/articles/206346498-Where-can-I-find-my-User-Server-Message-ID-)" +
                                             "?", false);
                eb.setColor(Color.RED);
                return eb.build();
            } else {
                r = roles.get(0);
            }
        }
    
        if (r == null) {
            Vault.storeUserDataLocal(null, message.getGuild().getId(), "role.reward." + args[1], "none");
            eb.addField("Rank Roles", String.format("Users will no longer get a role for rank %s.", args[1]), false);
            eb.setColor(Color.GREEN);
            return eb.build();
        }
        
        try {
            if(!Objects.requireNonNull(message.getGuild().getMemberById(jda.getSelfUser().getId())).getRoles().get(0).canInteract(r)) {
                eb.addField("Error", "Looks like I can't do anything with this role!  Please make sure I have the `manage-roles` permission " +
                                             "and my top role is higher than the role you want to give.", false);
                eb.setColor(Color.RED);
                return eb.build();
            }
        } catch(NullPointerException ignored) {
            eb.addField("Oops!!!", "Looks like something went wrong with untitled-bot!  Please report this using the `bug-report` command if this continues...\n" +
                                           "Error: RANK_ROLE_SET_PRODUCED_NPE_FOR_SELF_ROLE_CHECK", false);
            eb.setColor(Color.RED);
            Logger.critical("RANK_ROLE_SET_PRODUCED_NPE_FOR_SELF_ROLE_CHECK", -1, false);
            return eb.build();
        }
        
        if(message.mentionsEveryone()) {
            eb.addField("Error", "Cannot assign a reward for at everyone or at here.", false);
            eb.setColor(Color.RED);
            return eb.build();
        }
        
        Vault.storeUserDataLocal(null, message.getGuild().getId(), "role.reward." + args[1], r.getId());
        
        eb.addField("Rank Roles", String.format("The role <@&%s> will be assigned to users once they reach level %s.", r.getId(), args[1]), false);
        eb.setColor(Color.GREEN);
        
        return eb.build();
    }
    
    @Override
    public void onRegister() {
        CommandRegistrar.register("rank-role", "admin", this);
        Manual.setHelpPage("rank-role", "Set the role to give a user when they level up!\n" +
                                                "Usage: `rank-role <level> <role @ | role ID | role name | none>`\n" +
                                                "To clear a role reward, use `rank-role <level> none`\n" +
                                                "Note: the bot will try to guess the role by name, but if there is more than one result, " +
                                                "it will return an error.\n" +
                                                "For ID copying, see " +
                                                "[this Discord support page]" +
                                                "(https://support.discord.com/hc/en-us/articles/206346498-Where-can-I-find-my-User-Server-Message-ID-).");
    }
}
