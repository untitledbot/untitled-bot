package dev.alexisok.untitledbot.modules.rpg.item;

import dev.alexisok.untitledbot.Main;
import dev.alexisok.untitledbot.modules.rpg.RPGVaultKeys;
import dev.alexisok.untitledbot.modules.rpg.exception.ConsumableItemWasNotConsumedCorrectlyException;
import dev.alexisok.untitledbot.modules.vault.Vault;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Describes items for the RPG as Objects.
 * 
 * @author AlexIsOK
 * @since 1.4.0
 */
public final class RPGItem {
    
    /**
     * All of the items.  Do not access this or save to this directly.
     */
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
    private String imageLocation;
    
    @Getter@Setter
    private String description;
    
    @Getter@Setter
    private ItemProperties[] properties;
    
    @Getter@Setter
    private ItemClass itemClass;
    
    @Getter
    private final int itemID;
    
    @Getter@Setter
    private double accuracy;
    
    /**
     * Describes RPG items that can be used in the game.
     *
     * @param displayName The name as it is displayed to the user in-game.
     * @param name the name of the item as it is stored in the vault.
     * @param rarity the {@link ItemRarity} of the item, see enum.
     * @param clazz the {@link ItemClass} of the item, see enum.
     * @param imageLocation the location as it appears relative to the directory inside "./cards/media/"
     * @param description the description of the card as it is displayed to the user.
     * @param itemID the ID of the item.
     * @param accuracy the accuracy that the item will work as a decimal, from 0.00 (none) to 1.00 (guaranteed).
     * @param action the {@link ConsumerAction} to execute upon use.
     */
    @NotNull
    @Contract
    public RPGItem(@NotNull String displayName,
                   @NotNull String name,
                   @NotNull ItemRarity rarity,
                   @NotNull ItemClass clazz,
                   @NotNull String imageLocation,
                   @NotNull String description,
                   int itemID,
                   double accuracy,
                   @NotNull ConsumerAction action) {
        if(ITEM_IDS.contains(itemID))
            throw new RuntimeException(String.format("An item ID of %d is already registered, so %s cannot have it.", itemID, name));
        this.displayName = displayName;
        this.name = name;
        this.rarity = rarity;
        this.itemClass = clazz;
        this.imageLocation = imageLocation;
        this.description = description;
        this.itemID = itemID;
        this.accuracy = accuracy;
        this.action = action;
    }
    
    /**
     * Add an item to the total items {@link ArrayList}.
     * 
     * Do not add to the ITEMS array list directly.
     * 
     * @param item the {@link RPGItem} to add.
     */
    public static void addToItems(RPGItem item) {
        ITEMS.add(item);
    }
    
    static {
        //initialize all of the items.
        registerDevCards();
        registerTierOneCards();
        
    }
    
    private static void registerTierOneCards() {
        
        addToItems(new RPGItem(
                "Minor Wrath of Hamderforge",
                "wrath_of_hamderforge_i",
                ItemRarity.CRYSTAL,
                ItemClass.FIRE,
                "wrath_of_hamderforge_i",
                "Deal 25 to 35 " + ItemEmotes.FIRE + " damage.",
                1,
                0.75,
                (guildID, caster, friendlyUsers, enemyUsers, targets, item, casterClass) -> {
                    int rand = ThreadLocalRandom.current().nextInt(25, 36); //25 to 35
                    targets[0].setHealthCurrent(targets[0].getHealthCurrent() - rand);
                    return String.format("%s has dealt %d damage to %s using %s!", caster.getUsername(), rand, targets[0].getUsername(), item.getName());
                }
        ));
        
        //TODO
    }
    
    private static void registerDevCards() {
        addToItems(new RPGItem(
                "SMITE",
                "smite",
                ItemRarity.PAINITE,
                ItemClass.FIRE,
                "smite.png",
                "Deal " + Integer.MAX_VALUE + ItemEmotes.FROST + " damage.",
                -5,
                1.00,
                (guildID, caster, friendlyUsers, enemyUsers, targets, item, casterClass) -> {
                
                    targets[0].setHealthCurrent(targets[0].getHealthCurrent() - Integer.MAX_VALUE);
                
                    return "Thou hast been smitten.";
                }
        ));
        addToItems(new RPGItem(
                "Oopsie",
                "oopsie",
                ItemRarity.PAINITE,
                ItemClass.FROST,
                "oopsie.png",
                "Deal -1" + ItemEmotes.STORM + " damage",
                -4,
                1.00,
                (guildID, caster, friendlyUsers, enemyUsers, targets, item, casterClass) -> {
                    targets[0].setHealthCurrent(targets[0].getHealthCurrent() - -1);
                    return "imagine testing in production 4head";
                }
        ));
        addToItems(new RPGItem(
                "Uber Heal:TM:",
                "uber_heal",
                ItemRarity.PAINITE,
                ItemClass.DEATH,
                "uber_heal.png",
                "Heal " + Integer.MAX_VALUE + " health.",
                -3,
                1.00,
                (guildID, caster, friendlyUsers, enemyUsers, targets, item, casterClass) -> {
                    targets[0].setHealthCurrent(targets[0].getHealthCurrent() + Integer.MAX_VALUE);
                    return "lel";
                }
        ));
        addToItems(new RPGItem(
                "Am I Doing it Right?",
                "am_i_doing_it_right",
                ItemRarity.PAINITE,
                ItemClass.STORM,
                "am_i_doing_it_right.png",
                "Heal -500 health.",
                -2,
                1.00,
                (guildID, caster, friendlyUsers, enemyUsers, targets, item, casterClass) -> {
                    targets[0].setHealthCurrent(targets[0].getHealthCurrent() + -500);
                    return "you have been healed -500 health i guess";
                }
        ));
        addToItems(new RPGItem(
                "Balance Test Spell",
                "balance_test_spell",
                ItemRarity.PAINITE,
                ItemClass.BALANCE,
                "balance_test_spell",
                "Deal 1 " + ItemEmotes.BALANCE + " damage.",
                -1,
                1.00,
                (guildID, caster, friendlyUsers, enemyUsers, targets, item, casterClass) -> {
                    targets[0].setHealthCurrent(targets[0].getHealthCurrent() - 1);
                    return "1 damage has been dealt to " + targets[0].getUsername();
                }
        ));
        addToItems(new RPGItem(
                "Oliy's Blessing",
                "oliy_blessing",
                ItemRarity.PAINITE,
                ItemClass.FROST,
                "oliy_blessing.png",
                "Give a user resident in Oliy Island",
                -100,
                0.00, //this always fails lol
                (guildID, caster, friendlyUsers, enemyUsers, targets, item, casterClass) -> {
                    try {
                        Objects.requireNonNull(Main.jda.getGuildById(419422246168166400L))
                                .addRoleToMember(caster.getUserID(),
                                        Objects.requireNonNull(Main.jda.getRoleById(755412894794907728L)))
                                .queue();
                        return "Wow it actually worked!";
                    } catch(Throwable t) {
                        return "No resident 4 u";
                    }
                } 
        ));
    }
}
