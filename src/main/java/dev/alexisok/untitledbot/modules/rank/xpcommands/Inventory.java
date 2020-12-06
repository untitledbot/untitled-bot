package dev.alexisok.untitledbot.modules.rank.xpcommands;

import dev.alexisok.untitledbot.command.EmbedDefaults;
import dev.alexisok.untitledbot.plugin.UBPlugin;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

/**
 * Lists the contents of a user's inventory
 * 
 * @author AlexIsOK
 * @since 1.3.21
 */
public final class Inventory extends UBPlugin {
    
    @NotNull
    @Override
    @Contract(pure = true)
    public MessageEmbed onCommand(String[] args, @NotNull Message message) {
        EmbedBuilder eb = new EmbedBuilder();
        EmbedDefaults.setEmbedDefaults(eb, message);
        
        StringBuilder builder = new StringBuilder();
        
        for(int i = 0; i <= Shop.getItemSize(); i++) {
            long count = Shop.getCountOfItemUserHas(message.getAuthor().getId(), message.getGuild().getId(), i);
            if(count == 0)
                continue;
            
            builder.append(String.format(
                    "%dx %s%n",
                    count,
                    Shop.getItemNameByID(i)
            ));
        }
        if(!builder.toString().isEmpty()) {
            eb.addField("Inventory", "Your inventory\n\n" + builder.toString(), false);
            eb.setColor(Color.GREEN);
        } else {
            eb.addField("Inventory", "Hm... it seems like your inventory is empty...\n" +
                                             "Use the `shop` command to see what you can buy!", false);
            eb.setColor(Color.RED);
        }
        
        return eb.build();
    }
    
    @Override
    public void onRegister() {
//        CommandRegistrar.register("inv", this);
//        Manual.setHelpPage("inv", "Get your inventory.  You cannon get the inventory of another user.");
    }
}
