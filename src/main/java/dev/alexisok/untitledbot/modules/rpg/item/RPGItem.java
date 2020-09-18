package dev.alexisok.untitledbot.modules.rpg.item;

import dev.alexisok.untitledbot.modules.rpg.RPGVaultKeys;
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
    
    @Getter
    private ConsumerAction action;
    
    @NotNull
    @Contract
    public RPGItem(@NotNull String displayName, @NotNull String name, @NotNull ItemRarity rarity, @NotNull ItemType type, @NotNull String emoteName, @NotNull ConsumerAction action) {
        this.displayName = displayName;
        this.name = name;
        this.rarity = rarity;
        this.type = type;
        this.emoteName = emoteName;
    }
    
    static {
        //initialize all of the items.
        ITEMS.add(new RPGItem("Healing Potion I",
                "healing_potion_1",
                ItemRarity.CRYSTAL,
                ItemType.POTION,
                ItemEmotes.HEALING_POTION_1,
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
        ITEMS.add(new RPGItem("Healing Potion II", "healing_potion_2", ItemRarity.CRYSTAL, ItemType.POTION, ItemEmotes.HEALING_POTION_2));
        ITEMS.add(new RPGItem("POW+ Potion I", "power_up_potion_1", ItemRarity.EMERALD, ItemType.POTION, ItemEmotes.POWER_UP_POTION));
        ITEMS.add(new RPGItem("DEF+ Potion I", "defense_up_potion"))
    }
}
