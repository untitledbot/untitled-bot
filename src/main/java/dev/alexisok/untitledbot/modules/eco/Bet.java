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
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.Random;

/**
 * Registers the "bet" command.
 * @author AlexIsOK
 * @since 1.4.0
 */
public final class Bet extends UBPlugin {
    
    @NotNull
    @Override
    @Contract(pure = true)
    public MessageEmbed onCommand(@NotNull String[] args, @NotNull Message message) {
        EmbedBuilder eb = new EmbedBuilder();
        EmbedDefaults.setEmbedDefaults(eb, message);
        
        long moneyBank = Long.parseLong(Vault.getUserDataLocalOrDefault(message.getAuthor().getId(), message.getGuild().getId(), Shop.BANK_VAULT_NAME, "0"));
        
        if(args.length != 2) {
            eb.addField("Bet", "Please specify an amount of UB$ to bet.\n" +
                                       "Usage: `bet <amount>`.\n" +
                                       "Note: odds are 50/50 and the UB$ will be taken out of " +
                                       "your bank rather than your pockets, use `deposit` to deposit money " +
                                       "into your bank.", false);
            eb.setColor(Color.RED);
            return eb.build();
        }
        
        long amount;
        
        try {
            amount = Long.parseLong(args[1]);
            if(amount < 1 || amount > Integer.MAX_VALUE) {
                eb.addField("Bet", "The amount to bet must be between 1 and " + Integer.MAX_VALUE + " (inclusive).", false);
                eb.setColor(Color.RED);
                return eb.build();
            }
            if(moneyBank < amount) {
                eb.addField("Bet", String.format("To bet UB$%d, you need that amount in your bank!%nYou have UB$%d in your bank, use the " +
                                                         "`deposit` command to deposit money into your bank.", amount, moneyBank), false);
                eb.setColor(Color.RED);
                return eb.build();
            }
        } catch(Exception ignored) {
            eb.addField("Bet", "Please specify an amount of UB$ to bet.\n" +
                                       "Usage: `bet <amount>`.\n" +
                                       "Note: odds are 50/50 and the UB$ will be taken out of " +
                                       "your bank rather than your pockets, use `deposit` to deposit money " +
                                       "into your bank.", false);
            eb.setColor(Color.RED);
            return eb.build();
        }
        
        if(new Random(message.getIdLong()).nextBoolean()) {
            eb.addField("Bet", String.format("You bet UB$%d and got UB$%d in return!\nYou now have UB$%d.", amount, amount * 2, moneyBank + (amount * 2)), false);
            eb.setColor(Color.GREEN);
            moneyBank += (amount * 2);
        } else {
            eb.addField("Bet", String.format("You bet UB$%d but lost it!\nYou now have UB$%d in your bank.", amount, moneyBank - amount), false);
            eb.setColor(Color.RED);
            moneyBank -= amount;
        }
        Vault.storeUserDataLocal(message.getAuthor().getId(), message.getGuild().getId(), Shop.BANK_VAULT_NAME, String.valueOf(moneyBank));
        return eb.build();
    }
    
    @Override
    public void onRegister() {
        CommandRegistrar.register("bet", this);
        Manual.setHelpPage("bet", "Bet UB$.\n" +
                                          "Usage: `bet <amount>`\n" +
                                          "The odds of wining are 50/50.");
        CommandRegistrar.registerAlias("bet", "cf"); //cf = coin flip
    }
}
