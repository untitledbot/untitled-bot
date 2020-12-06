package dev.alexisok.untitledbot.modules.eco;

import dev.alexisok.untitledbot.command.CommandRegistrar;
import dev.alexisok.untitledbot.command.EmbedDefaults;
import dev.alexisok.untitledbot.command.Manual;
import dev.alexisok.untitledbot.modules.rank.xpcommands.Shop;
import dev.alexisok.untitledbot.util.vault.Vault;
import dev.alexisok.untitledbot.plugin.UBPlugin;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

/**
 * @author AlexIsOK
 * @since 1.4.0
 */
public final class Withdraw extends UBPlugin {
    
    /**
     * Yeah don't mind the variable names i copied this from {@link Deposit}
     * @param args arguments for the command.
     *             The first argument is always the name of
     *             the command.  Arguments are the discord
     *             message separated by spaces.
     * @param message the {@link Message} that can be used
     *                to get information from the user
     * @return stuff
     */
    @NotNull
    @Override
    public MessageEmbed onCommand(String[] args, @NotNull Message message) {
        EmbedBuilder eb = new EmbedBuilder();
        EmbedDefaults.setEmbedDefaults(eb, message);
    
        long moneyPockets = Long.parseLong(Vault.getUserDataLocalOrDefault(message.getAuthor().getId(), message.getGuild().getId(), Shop.CURRENCY_VAULT_NAME, "0"));
        long moneyBank    = Long.parseLong(Vault.getUserDataLocalOrDefault(message.getAuthor().getId(), message.getGuild().getId(), Shop.BANK_VAULT_NAME, "0"));
    
        try {
        
            long amountToDeposit = Long.parseLong(args[1]);
        
            if(amountToDeposit > moneyBank)
                throw new Exception();
            if(amountToDeposit < 1)
                throw new NumberFormatException();
            
            if(moneyPockets + amountToDeposit < 0) {
                eb.addField("Uhm", "Somehow, you reached the maximum amount of money...", false);
                eb.setColor(Color.RED);
                return eb.build();
            }
            
            Vault.storeUserDataLocal(message.getAuthor().getId(),
                    message.getGuild().getId(),
                    Shop.CURRENCY_VAULT_NAME,
                    String.valueOf(moneyPockets + amountToDeposit));
        
            Vault.storeUserDataLocal(message.getAuthor().getId(),
                    message.getGuild().getId(),
                    Shop.BANK_VAULT_NAME,
                    String.valueOf(moneyBank - amountToDeposit));
        
            eb.addField("Withdraw",
                    String.format(
                            "You have withdrawn UB$%d from your bank.  You now have UB$%d in your bank.",
                            amountToDeposit,
                            moneyBank - amountToDeposit
                    ),
                    false);
            eb.setColor(Color.GREEN);
            return eb.build();
        } catch(NumberFormatException ignored) {
            //if arg 1 is not a number or less than one or more than 9223372036854775807
            eb.addField("Withdraw", "The amount of money to withdraw must be a number and greater than zero.", false);
            eb.setColor(Color.RED);
            return eb.build();
        } catch(IndexOutOfBoundsException ignored) {
            //if arg[1] doesn't exist
            eb.addField("Withdraw", "Usage: `withdraw <amount>`\n" +
                                           "Withdrawn money is able to be stolen using the `steal` command!.", false);
            eb.setColor(Color.RED);
            return eb.build();
        } catch(Exception ignored) {
            //if amount is whatever something less
            eb.addField("Withdraw", String.format("You cannot withdraw UB$%s as you only have UB$%d in your bank!", args[1], moneyBank), false);
            eb.setColor(Color.RED);
            return eb.build();
        }
    
    }
    
    @Override
    public void onRegister() {
        CommandRegistrar.register("withdraw", this);
        Manual.setHelpPage("withdraw", "Withdraw UB$ from the bank.\n" +
                                               "Usage: `withdraw <amount>`");
        CommandRegistrar.registerAlias("withdraw", "w", "wd");
    }
}
