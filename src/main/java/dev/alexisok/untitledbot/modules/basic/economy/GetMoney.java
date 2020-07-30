package dev.alexisok.untitledbot.modules.basic.economy;

import dev.alexisok.untitledbot.command.CommandRegistrar;
import dev.alexisok.untitledbot.command.EmbedDefaults;
import dev.alexisok.untitledbot.command.Manual;
import dev.alexisok.untitledbot.modules.vault.Vault;
import dev.alexisok.untitledbot.plugin.UBPlugin;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

import static dev.alexisok.untitledbot.modules.basic.economy.UpdateMoney.CURRENCY_SYM;

/**
 * Return the amount of money the user has (or another user if they are mentioned).
 * 
 * @author AlexIsOK
 * @since 1.3
 */
public final class GetMoney extends UBPlugin {
    
    @Override
    public @NotNull MessageEmbed onCommand(String[] args, Message message) {
        EmbedBuilder eb = new EmbedBuilder();
        EmbedDefaults.setEmbedDefaults(eb, message);
    
        User get = message.getMentionedUsers().size() == 0
                           ? message.getMember().getUser()
                           : message.getMentionedUsers().get(0);
        
        String money = Vault.getUserDataLocal(message.getAuthor().getId(), message.getGuild().getId(), UpdateMoney.MONEY_MODULE);
    
        if(money == null) {
            eb.addField("Economy", "This user doesn't appear to have any money.  Maybe you should get them to talk in the server?", false);
            eb.setColor(Color.RED);
            
            return eb.build();
        }
        
        eb.addField("Economy", "Balance: " + CURRENCY_SYM + Long.valueOf(money), false);
        eb.setColor(Color.GREEN);
        
        return eb.build();
        
    }
    
    @Override
    public void onRegister() {
        CommandRegistrar.registerHook(new EconomyHook());
        CommandRegistrar.register("balance", "core.eco.balance", this);
        CommandRegistrar.setDefaultPermissionForNode("core.eco.balance", true);
        CommandRegistrar.registerAlias("balance", "bal", "eco", "bl", "money", "cha-ching");
        Manual.setHelpPage("balance", "Get the balance of yourself or another user.\n" +
                                              "Usage: `balance [user @]`\n");
    }
}
