package dev.alexisok.untitledbot.modules.rank.xpcommands;

import dev.alexisok.untitledbot.command.CommandRegistrar;
import dev.alexisok.untitledbot.command.EmbedDefaults;
import dev.alexisok.untitledbot.command.Manual;
import dev.alexisok.untitledbot.util.vault.Vault;
import dev.alexisok.untitledbot.plugin.UBPlugin;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.ArrayList;
import java.util.Objects;

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
    
    protected static final ArrayList<ShopItem> ITEMS = new ArrayList<>();
    
    public static final String CURRENCY_VAULT_NAME = "balance";
    public static final String BANK_VAULT_NAME     = "balance.bank";
    
    public static final String TOKEN_VAULT_NAME    = "tokens";
    
    /**
     * Get the amount of ITEMS.
     * @return the amount of items.
     */
    protected static int getItemSize() {
        return ITEMS.size();
    }
    
    @Override
    public @NotNull MessageEmbed onCommand(@NotNull String[] args, @NotNull Message message) {
        EmbedBuilder eb = new EmbedBuilder();
        EmbedDefaults.setEmbedDefaults(eb, message);
        
        if(args.length == 1)
            args = new String[]{"shop", "list"};
        
        if(args[1].equalsIgnoreCase("list")) {
            StringBuilder itemList = new StringBuilder();
            
            for(ShopItem si : ITEMS) {
                if(si.getCostInLevels() == -1L)
                    continue;
                if(si.getMaximum() != -1)
                    itemList.append(String.format("`%s` -- `TK %d` -- %s -- limit: `%d`%n%n",
                            si.getName(),
                            si.getCostInLevels(),
                            si.getDescription(),
                            si.getMaximum()));
                else
                    itemList.append(String.format("`%s` -- `TK %d` -- %s -- limit: `unlimited`%n%n",
                            si.getName(),
                            si.getCostInLevels(),
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
                        int userLevel;
                        
                        try {
                            userLevel = Integer.parseInt((Vault.getUserDataLocalOrDefault(message.getAuthor().getId(), null, TOKEN_VAULT_NAME, "0")));
                        } catch(NumberFormatException ignored) {
                            userLevel = 0;
                        }
                        
                        if(userLevel >= a.getCostInLevels()) {
                            
                            //current count of item in inv as str
                            String currentCountStr = Vault.getUserDataLocal(message.getAuthor().getId(), null, "shop.item." + a.getItemID());
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
                                    TOKEN_VAULT_NAME,
                                    String.valueOf(userLevel - a.getCostInLevels()));
                            
                            //store item
                            Vault.storeUserDataLocal(message.getAuthor().getId(),
                                    null,
                                    String.format("shop.item.%d", a.getItemID()),
                                    String.valueOf(currentCount + 1));
                            
                            eb.addField("Transaction Receipt",
                                    String.format("You have bought `%s` for %d tokens.%n" +
                                                          "Balance:%nTK %d ---> TK %d%n" +
                                                          "Quantity of item:%n%d ---> %d%n",
                                            a.getName(), a.getCostInLevels(), userLevel, userLevel - a.getCostInLevels(),
                                            currentCount, currentCount + 1),
                                    false);
                            
                            eb.setColor(Color.GREEN);
                            eb.setFooter("Note: all transactions are non-refundable.");
                        } else {
                            
                            eb.addField("Shop",
                                    String.format("Error: `%s` costs TK " + a.getCostInLevels() + ", but you only have TK " + userLevel + ".",
                                            a.getName()),
                                    false);
                            eb.setColor(Color.RED);
                            
                        }
                        
                        return eb.build();
                    }
                } //end foreach item
                
                eb.addField("Shop", "Could not find the item!  Use `shop list` for a list of all items.", false);
                eb.setColor(Color.RED);
                
                return eb.build();
                
            } catch(RuntimeException ignored) {
                eb.addField("Shop", "Usage: `shop buy <item name>`", false);
                eb.setColor(Color.RED);
                
                return eb.build();
            }
        } else {
            eb.addField("Shop", "Available sub-commands:\n" +
                                        "`list` - list all available items.\n" +
                                        "`buy <item name>` - buy a specific item.", false);
        }
        return eb.build();
    }
    
    /**
     * Get the name of a shop item by the ID.
     * @param ID the ID
     * @return the shop item name, or {@code null} if it was not found.
     */
    @Nullable
    @Contract(pure = true)
    public static String getItemNameByID(int ID) {
        return ITEMS
                       .stream()
                       .filter(s -> s.getItemID() == ID)
                       .findFirst()
                       .map(ShopItem::getName)
                       .orElse(null);
    
    }
    
    /**
     * Get the amount of items a user currently has in their inventory.
     * @param userID the ID of the user
     * @param item the item ID to search for
     * @return the number of items the user has, or zero if the user does not have the item.
     */
    protected static long getCountOfItemUserHas(String userID, int item) {
        return Long.parseLong(Vault.getUserDataLocalOrDefault(userID, null, "shop.item." + item, "0"));
    }
    
    @Override
    public void onRegister() {
        CommandRegistrar.register("shop", this);
        
        ITEMS.add(new ShopItem("One level", 10, "Gives you one level on this server.  Useful for people who are level 30+.", 1));
        ITEMS.add(new ShopItem("Five levels", 40, "Gives you five levels on this server.  Good way to get everyone to hate you as well.", 2));
        ITEMS.add(new ShopItem("1000 XP", 10, "Gives you 1000 XP.  Useful for people below level 10.", 3));
        ITEMS.add(new ShopItem("December 2020 User", 0, "Gives you the December 2020 User.  This does nothing and is probably worthless.", 4, 1));
        ITEMS.add(new ShopItem("UB$10000", 10, "Gives you UB$10000.", 5));
        
        ITEMS.add(new ShopItem("Moderator Role on the Support Server", Long.MAX_VALUE, "Gives you the \"moderator\" role on the support server.", 5));
        
        Manual.setHelpPage("shop", "" +
                                           "Available sub-commands:\n" +
                                           "```\n" +
                                           "list - list all available items.\n" +
                                           "buy <item name> - buy a specific item.\n" +
                                           "```" +
                                           "\n\n" +
                                           "Note: all items are purchased with your tokens, use the `%stokens` command to see your XP and level." +
                                           "\n\n" +
                                           "All purchases are non-refundable and are delivered immediately.");
    }
}
