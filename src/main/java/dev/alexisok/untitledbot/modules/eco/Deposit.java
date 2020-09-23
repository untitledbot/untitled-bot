package dev.alexisok.untitledbot.modules.eco;

import dev.alexisok.untitledbot.command.CommandRegistrar;
import dev.alexisok.untitledbot.command.EmbedDefaults;
import dev.alexisok.untitledbot.command.Manual;
import dev.alexisok.untitledbot.modules.rank.xpcommands.Shop;
import dev.alexisok.untitledbot.modules.vault.Vault;
import dev.alexisok.untitledbot.plugin.UBPlugin;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.awt.Color;

/**
 * Deposit UB$ into the user's account.
 * 
 * @author AlexIsOK
 * @since 1.4.0
 */
public final class Deposit extends UBPlugin {
    
    @NotNull
    @Override
    @Contract(pure = true)
    public MessageEmbed onCommand(String[] args, @NotNull Message message) {
    
        EmbedBuilder eb = new EmbedBuilder();
        EmbedDefaults.setEmbedDefaults(eb, message);
        
        long moneyPockets = Long.parseLong(Vault.getUserDataLocalOrDefault(message.getAuthor().getId(), message.getGuild().getId(), Shop.CURRENCY_VAULT_NAME, "0"));
        long moneyBank    = Long.parseLong(Vault.getUserDataLocalOrDefault(message.getAuthor().getId(), message.getGuild().getId(), Shop.BANK_VAULT_NAME, "0"));
        
        try {
            
            long amountToDeposit = Long.parseLong(args[1]);
            
            if(amountToDeposit > moneyPockets)
                throw new Exception();
            if(amountToDeposit < 1)
                throw new NumberFormatException();
            
            Vault.storeUserDataLocal(message.getAuthor().getId(),
                    message.getGuild().getId(),
                    Shop.CURRENCY_VAULT_NAME,
                    String.valueOf(moneyPockets - amountToDeposit));
            
            Vault.storeUserDataLocal(message.getAuthor().getId(),
                    message.getGuild().getId(),
                    Shop.BANK_VAULT_NAME,
                    String.valueOf(amountToDeposit + moneyBank));
    
            eb.addField("Deposit",
                    String.format(
                            "You have deposited UB$%d into your bank.  You now have UB$%d in your bank.",
                            amountToDeposit,
                            amountToDeposit + moneyBank
                    ),
                    false);
            eb.setColor(Color.GREEN);
            return eb.build();
        } catch(NumberFormatException ignored) {
            //if arg 1 is not a number or less than one or more than 9223372036854775807
            eb.addField("Deposit", "The amount of money to deposit must be a number and greater than zero.", false);
            eb.setColor(Color.RED);
            return eb.build();
        } catch(IndexOutOfBoundsException ignored) {
            //if arg[1] doesn't exist
            eb.addField("Deposit", "Usage: `deposit <amount>`\n" +
                                           "Deposited money is safe from theft and will not automatically be drawn out.", false);
            eb.setColor(Color.RED);
            return eb.build();
        } catch(Exception ignored) {
            //if amount is whatever something less
            eb.addField("Deposit", String.format("You cannot deposit UB$%s as you only have UB$%d!", args[1], moneyPockets), false);
            eb.setColor(Color.RED);
            return eb.build();
        }
        
    }
    
    @Override
    public void onRegister() {
        CommandRegistrar.register("deposit", this);
        Manual.setHelpPage("deposit", "Deposit UB$ into your bank account.\n" +
                                              "Usage: `deposit <amount>`\n" +
                                              "You can use the `withdraw` command to withdraw the money at any time.\n" +
                                              "Deposited money is safe from theft and will not automatically be drawn out.");
        CommandRegistrar.registerAlias("deposit", "dp", "d");
    }
}
