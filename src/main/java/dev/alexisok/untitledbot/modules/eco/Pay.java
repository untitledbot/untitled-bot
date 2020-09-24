package dev.alexisok.untitledbot.modules.eco;

import dev.alexisok.untitledbot.command.CommandRegistrar;
import dev.alexisok.untitledbot.command.EmbedDefaults;
import dev.alexisok.untitledbot.command.Manual;
import dev.alexisok.untitledbot.modules.rank.xpcommands.Shop;
import dev.alexisok.untitledbot.modules.vault.Vault;
import dev.alexisok.untitledbot.plugin.UBPlugin;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

/**
 * Pay command to give UB$
 * 
 * @author AlexIsOK
 * @since 1.4.0
 */
public final class Pay extends UBPlugin {
    
    @NotNull
    @Override
    public MessageEmbed onCommand(String[] args, @NotNull Message message) {
        EmbedBuilder eb = new EmbedBuilder();
        EmbedDefaults.setEmbedDefaults(eb, message);
        
        try {
    
            Member m = message.getMentionedMembers().get(0);
            if(m == null)
                throw new ArrayIndexOutOfBoundsException();
            
            if(m.getId().equals(message.getAuthor().getId())) {
                eb.addField("Pay", "You cannot pay yourself!", false);
                eb.setColor(Color.RED);
                return eb.build();
            }
            
            if(m.getUser().isBot()) {
                eb.addField("Pay", "You cannot pay bots!", false);
                eb.setColor(Color.RED);
                return eb.build();
            }
            
            long amountToGive = Long.parseLong(args[2]);
            long balanceUser = Long.parseLong(Vault.getUserDataLocalOrDefault(message.getAuthor().getId(), message.getGuild().getId(), Shop.BANK_VAULT_NAME, "0"));
            long balanceTarget = Long.parseLong(Vault.getUserDataLocalOrDefault(message.getAuthor().getId(), message.getGuild().getId(), Shop.BANK_VAULT_NAME, "0"));
            
            if(amountToGive < 0)
                throw new ArrayIndexOutOfBoundsException();
            
            if(amountToGive > 65536) {
                eb.addField("Pay", "You cannot give more than UB$65,536 per transaction.", false);
                eb.setColor(Color.RED);
                return eb.build();
            }
            
            if(balanceUser - amountToGive < 0) {
                eb.addField("Pay", "You don't have the funds to pay this person!\n" +
                                           "You have UB$" + balanceUser + "\n" +
                                           "You need UB$" + amountToGive, false);
                eb.setColor(Color.RED);
                return eb.build();
            }
            
            if(balanceTarget + amountToGive < 0 || balanceTarget + amountToGive > Long.MAX_VALUE - 2000) {
                eb.addField("Yikes!", "This user can't be given more money!  How did you even do this?!?!", false);
                eb.setColor(Color.RED);
                return eb.build();
            }
            
            Vault.storeUserDataLocal(message.getAuthor().getId(), message.getGuild().getId(), Shop.BANK_VAULT_NAME, String.valueOf(balanceUser - amountToGive));
            Vault.storeUserDataLocal(m.getId(), message.getGuild().getId(), Shop.BANK_VAULT_NAME, String.valueOf(balanceTarget + amountToGive));
            
            eb.addField("Pay", "You have paid UB$" + amountToGive + " to <@" + m.getId() + ">.", false);
            eb.setColor(Color.GREEN);
            return eb.build();
            
        } catch(ArrayIndexOutOfBoundsException ignored) {
            eb.addField("Pay", "Usage: `pay <user @> <amount>`", false);
            eb.setColor(Color.RED);
            return eb.build();
        }
    }
    
    @Override
    public void onRegister() {
        CommandRegistrar.register("pay", this);
        Manual.setHelpPage("pay", "Pay someone UB$.\n" +
                                          "Usage: `pay <user @> <amount>`\n" +
                                          "Note: funds are withdrawn from your bank, not your pocket..");
        CommandRegistrar.registerAlias("pay", "give", "transfer");
    }
}
