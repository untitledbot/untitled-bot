package dev.alexisok.untitledbot.modules.eco;

import dev.alexisok.untitledbot.Main;
import dev.alexisok.untitledbot.command.CommandRegistrar;
import dev.alexisok.untitledbot.command.EmbedDefaults;
import dev.alexisok.untitledbot.command.Manual;
import dev.alexisok.untitledbot.modules.rank.xpcommands.Shop;
import dev.alexisok.untitledbot.modules.vault.Vault;
import dev.alexisok.untitledbot.plugin.UBPlugin;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Get the balance of a user.
 * 
 * @author AlexIsOK
 * @since 1.3.21
 */
public final class Balance extends UBPlugin {
    
    @NotNull
    @Override
    @Contract(pure = true)
    public MessageEmbed onCommand(String[] args, @NotNull Message message) {
        
        EmbedBuilder eb = new EmbedBuilder();
        EmbedDefaults.setEmbedDefaults(eb, message);
        
        String bal;
        boolean another = false;
        
        try {
            User m = args[1].matches("[0-9]+")
                             ? Objects.requireNonNull(Main.jda.getUserById(args[1]))
                             : message.getMentionedMembers().get(0).getUser();
            
            bal = Vault.getUserDataLocal(m.getId(), message.getGuild().getId(), Shop.CURRENCY_VAULT_NAME);
            if(bal == null) bal = "0";
            another = true;
        } catch(Exception ignored) {
            bal = Vault.getUserDataLocal(message.getAuthor().getId(), message.getGuild().getId(), Shop.CURRENCY_VAULT_NAME);
            if(bal == null) bal = "0";
        }
        
        if(another) {
            eb.addField("Balance", "This user has UB$" + bal + ".", false);
        } else {
            eb.addField("Balance", "Your balance: UB$" + bal + ".", false);
        }
        
        return eb.build();
    }
    
    @Override
    public void onRegister() {
        CommandRegistrar.register("balance", this);
        Manual.setHelpPage("balance", "Get the balance of yourself or another user.\n" +
                                              "Usage: `bal [user @ | user ID]`");
        CommandRegistrar.registerAlias("balance", "bal", "b");
    }
}
