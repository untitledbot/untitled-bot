package dev.alexisok.untitledbot.modules.rpg.item;

import dev.alexisok.untitledbot.modules.rpg.RPGVaultKeys;
import dev.alexisok.untitledbot.modules.rpg.exception.ConsumableItemWasNotConsumedCorrectlyException;
import dev.alexisok.untitledbot.modules.vault.Vault;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Describes items for the RPG as Objects.
 * 
 * @author AlexIsOK
 * @since 1.4.0
 */
public final class RPGItem {
    
    @Getter
    private static final ArrayList<RPGItem> ITEMS = new ArrayList<>();
    
    private static final ArrayList<Integer> ITEM_IDS = new ArrayList<>();
    
    @Getter
    private final ConsumerAction action;
    
    @Getter@Setter
    private String displayName;
    
    @Getter@Setter
    private String name;
    
    @Getter@Setter
    private ItemRarity rarity;
    
    @Getter@Setter
    private ItemType type;
    
    @Getter@Setter
    private String emoteName;
    
    @Getter@Setter
    private ItemProperties[] properties;
    
    @Getter@Setter
    private ItemClass itemClass;
    
    @Getter
    private final int itemID;
    
    /**
     * Describes RPG items that can be used in the game.
     *
     * @param displayName The name as it is displayed to the user in-game.
     * @param name the name of the item as it is stored in the vault.
     * @param rarity the {@link ItemRarity} of the item, see enum.
     * @param type the {@link ItemType} of the item, see enum.
     * @param clazz the {@link ItemClass} of the item, see enum.
     * @param emoteName the emote to display alongside the item.
     * @param itemID the ID of the item.
     * @param action the {@link ConsumerAction} to execute upon use.
     */
    @NotNull
    @Contract
    public RPGItem(@NotNull String displayName,
                   @NotNull String name,
                   @NotNull ItemRarity rarity,
                   @NotNull ItemType type,
                   @NotNull ItemClass clazz,
                   @NotNull String emoteName,
                   int itemID,
                   @NotNull ConsumerAction action) {
        if(ITEM_IDS.contains(itemID))
            throw new RuntimeException(String.format("An item ID of %d is already registered, so %s cannot have it.", itemID, name));
        this.displayName = displayName;
        this.name = name;
        this.rarity = rarity;
        this.type = type;
        this.emoteName = emoteName;
        this.itemID = itemID;
        this.action = action;
        this.itemClass = clazz;
    }
    
    /**
     * Add an item to the total items {@link ArrayList}.
     * @param item the {@link RPGItem} to add.
     */
    public static void addToItems(RPGItem item) {
        ITEMS.add(item);
    }
    
    static {
        //initialize all of the items.
        ITEMS.add(new RPGItem("Healing Potion I",
                "healing_potion_1",
                ItemRarity.CRYSTAL,
                ItemType.POTION,
                ItemClass.ALL,
                ItemEmotes.HEALING_POTION_1,
                1,
                (userID, guildID, targetID, item) -> {
                    final int AMOUNT = 25;
                    int healthCurrent = Integer.parseInt(Objects.requireNonNull(Vault.getUserDataLocal(userID, guildID, RPGVaultKeys.HEALTH_CURRENT)));
                    int healthMaximum = Integer.parseInt(Objects.requireNonNull(Vault.getUserDataLocal(userID, guildID, RPGVaultKeys.HEALTH_MAXIMUM)));
                    
                    int store = healthCurrent + AMOUNT;
                    if(store > healthMaximum)
                        store = healthMaximum;
                    
                    Vault.storeUserDataLocal(userID, guildID, RPGVaultKeys.HEALTH_CURRENT, String.valueOf(store));
                    return store == healthMaximum
                            ? "Your health has been fully restored."
                            : String.format("You have replenished %d health, your current health is %d out of %d.", AMOUNT, store, healthMaximum);
                })
        );
        ITEMS.add(new RPGItem("Healing Potion II",
                "healing_potion_2",
                ItemRarity.EMERALD,
                ItemType.POTION,
                ItemClass.ALL,
                ItemEmotes.HEALING_POTION_2,
                2,
                (userID, guildID, targetID, item) -> {
                    final int AMOUNT = 100;
                    int healthCurrent = Integer.parseInt(Objects.requireNonNull(Vault.getUserDataLocal(userID, guildID, RPGVaultKeys.HEALTH_CURRENT)));
                    int healthMaximum = Integer.parseInt(Objects.requireNonNull(Vault.getUserDataLocal(userID, guildID, RPGVaultKeys.HEALTH_MAXIMUM)));
    
                    int store = healthCurrent + AMOUNT;
                    if(store > healthMaximum)
                        store = healthMaximum;
    
                    Vault.storeUserDataLocal(userID, guildID, RPGVaultKeys.HEALTH_CURRENT, String.valueOf(store));
                    return store == healthMaximum
                                   ? "Your health has been fully restored."
                                   : String.format("You have replenished %d health, your current health is %d out of %d.", AMOUNT, store, healthMaximum);
                    }
        ));
        ITEMS.add(new RPGItem("POW+ Potion I",
                "power_up_potion_1",
                ItemRarity.EMERALD,
                ItemType.POTION,
                ItemClass.ALL,
                ItemEmotes.POWER_UP_POTION,
                3,
                (userID, guildID, targetID, item) -> { 
                    if(!Vault.getUserDataLocalOrDefault(userID, guildID, RPGVaultKeys.IN_BATTLE, "false").equals("true"))
                        throw new ConsumableItemWasNotConsumedCorrectlyException("You must be in a battle to use this item!");
                    
                    Vault.storeUserDataLocal(userID, guildID, RPGVaultKeys.BATTLE_POTION, item.getDisplayName()); //i love lombok
                    Vault.storeUserDataLocal(userID, guildID, RPGVaultKeys.POTION_MODIFIER, "+POW_0.15"); //pow *= 0.15 for this battle
                    
                    return "Your power has been increased by 15%!";
                }
        ));
        ITEMS.add(new RPGItem("DEF+ Potion I",
                "defense_up_potion",
                ItemRarity.EMERALD,
                ItemType.POTION,
                ItemClass.ALL,
                ItemEmotes.DEFENSE_UP_POTION,
                4,
                (userID, guildID, targetID, item) -> {
                    if(!Vault.getUserDataLocalOrDefault(userID, guildID, RPGVaultKeys.IN_BATTLE, "false").equals("true"))
                        throw new ConsumableItemWasNotConsumedCorrectlyException("You must be in a battle to use this item!");
                    
                    Vault.storeUserDataLocal(userID, guildID, RPGVaultKeys.BATTLE_POTION, item.getDisplayName());
                    Vault.storeUserDataLocal(userID, guildID, RPGVaultKeys.POTION_MODIFIER, "+DEF_0.10");
                    
                    return "Your defense has been increased by 10%!";
                }
        ));
        
    }
}
