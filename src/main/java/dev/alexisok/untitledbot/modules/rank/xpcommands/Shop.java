package dev.alexisok.untitledbot.modules.rank.xpcommands;

import dev.alexisok.untitledbot.command.CommandRegistrar;
import dev.alexisok.untitledbot.command.EmbedDefaults;
import dev.alexisok.untitledbot.command.Manual;
import dev.alexisok.untitledbot.modules.vault.Vault;
import dev.alexisok.untitledbot.plugin.UBPlugin;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.ArrayList;

/**
 * Class that handles usage with the shop.  Also handles the {@code shop} command.
 * 
 * Stores the items for the shop as well, they are hard-coded into the program rather than
 * being in a properties file.  Better to do it that way as the shop won't be changing much.
 * 
 * @author AlexIsOK
 * @since 1.3
 */
public final class Shop extends UBPlugin {
    
    private static final ArrayList<ShopItem> ITEMS = new ArrayList<>();
    
    @Override
    public @NotNull MessageEmbed onCommand(@NotNull String[] args, @NotNull Message message) {
        EmbedBuilder eb = new EmbedBuilder();
        EmbedDefaults.setEmbedDefaults(eb, message);
        
        if(args.length == 1)
            args = new String[]{"shop", "list"};
        
        if(args[1].equalsIgnoreCase("list")) {
            StringBuilder itemList = new StringBuilder();
            
            for(ShopItem si : ITEMS) {
                if(si.getMaximum() != -1)
                    itemList.append(String.format("`%s` -- `%d` level%s -- %s -- limit: `%d`%n%n",
                            si.getName(),
                            si.getCostInLevels(),
                            si.getCostInLevels() != 1 ? "s" : "",
                            si.getDescription(),
                            si.getMaximum()));
                else
                    itemList.append(String.format("`%s` -- `%d` level%s -- %s -- limit: `unlimited`%n%n",
                            si.getName(),
                            si.getCostInLevels(),
                            si.getCostInLevels() != 1 ? "s" : "",
                            si.getDescription()));
            }
            
            eb.addField("Current items", itemList.toString(), false);
            eb.setColor(Color.GREEN);
            
            return eb.build();
            
        } else if(args[1].equalsIgnoreCase("buy")) {
            try {
                String itemToBuy = String.join(" ", args);
                itemToBuy = itemToBuy.substring(9);
                itemToBuy = itemToBuy.toLowerCase();
                
                for(ShopItem a : ITEMS) {
                    //start the buying of the item
                    if(a.getName().equalsIgnoreCase(itemToBuy)) {
                        int userLevel = Integer.parseInt(Vault.getUserDataLocal(message.getAuthor().getId(), message.getGuild().getId(), "ranks-level"));
                        if(userLevel >= a.getCostInLevels()) {
                            
                            //current count of item in inv as str
                            String currentCountStr = Vault.getUserDataLocal(message.getAuthor().getId(), message.getGuild().getId(), "shop.item." + a.getItemID());
                            int currentCount = currentCountStr == null ? 0 : Integer.parseInt(currentCountStr);
                            if(a.getMaximum() != -1 && currentCount >= a.getMaximum()) {
                                eb.addField("Shop",
                                        "Error: you already have the maximum amount of this item.",
                                        false);
                                eb.setColor(Color.RED);
                                
                                return eb.build();
                            }
                            
                            //subtract balance
                            Vault.storeUserDataLocal(
                                    message.getAuthor().getId(),
                                    message.getGuild().getId(),
                                    "ranks-level",
                                    String.valueOf(userLevel - a.getCostInLevels()));
                            //store item
                            Vault.storeUserDataLocal(message.getAuthor().getId(),
                                    message.getGuild().getId(),
                                    String.format("shop.item.%d", a.getItemID()),
                                    String.valueOf(currentCount + 1));
                            
                            eb.addField("Transaction Receipt",
                                    String.format("You have bought `%s` for %d level%s.%n" +
                                                          "Level:%n%d ---> %d%n" +
                                                          "Quantity of item:%n%d ---> %d%n",
                                            a.getName(), a.getCostInLevels(), a.getCostInLevels() != 1 ? "s" : "",
                                            userLevel, userLevel - a.getCostInLevels(),
                                            currentCount, currentCount + 1),
                                    false);
                            eb.setColor(Color.GREEN);
                            eb.setFooter("Note: all transactions are non-refundable.");
                        } else {
                            eb.addField("Shop",
                                    String.format("Error: `%s` costs %d levels, but you are level %d.",
                                            a.getName(),
                                            a.getCostInLevels(),
                                            userLevel),
                                    false);
                            eb.setColor(Color.RED);
                            
                        }
                        return eb.build();
                    }
                } //end foreach item
                
                eb.addField("Shop", "Could not find the item!  Use `shop list` for a list of all items.", false);
                eb.setColor(Color.RED);
                
                return eb.build();
                
            } catch(ArrayIndexOutOfBoundsException ignored) {
                eb.addField("Shop", "Usage: `shop buy <item name>`", false);
                eb.setColor(Color.RED);
                
                return eb.build();
            }
        }
        return eb.build();
    }
    
    @Override
    public void onRegister() {
        CommandRegistrar.register("shop", this);
//        ITEMS.add(new ShopItem("RPG Item I", 10, "A claimable item for when the RPG releases (low-tier random item).  Can be used once the RPG is out.", 1, 10));
//        ITEMS.add(new ShopItem("RPG Item II", 20, "A claimable item for when the RPG releases (mid-tier random item).  Can be used once the RPG is out.", 2, 5));
//        ITEMS.add(new ShopItem("RPG Item III", 30, "A claimable item for when the RPG releases (high-tier random item).  Can be used once the RPG is out.", 3, 3));
        
        ITEMS.add(new ShopItem("RPG Early Supporter Badge", 0, "A badge that shows you were using the bot before the RPG released!  Doesn't do anything in the RPG, will show when users use the `profile` command on you.", 4, 1));
        
        Manual.setHelpPage("shop", "" +
                                           "Available sub-commands:\n" +
                                           "```\n" +
                                           "list - list all available items.\n" +
                                           "buy <item name> - buy a specific item.\n" +
                                           "```" +
                                           "\n\n" +
//                                           "Note: all items are purchased with your xp in this guild, use the `rank` command to see your XP and level." +
                                           "\n\n" +
                                           "All purchases are non-refundable and are delivered immediately.");
    }
}
