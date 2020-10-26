package dev.alexisok.untitledbot.modules.rpg.item;

import dev.alexisok.untitledbot.Main;
import dev.alexisok.untitledbot.logging.Logger;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Objects;
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
    
    @Getter
    private final int maximumItemCount;
    
    @Getter
    private final ItemUses use;
    
    @Getter@Setter
    private double accuracy;
    
    /**
     * Describes RPG items that can be used in the game.
     *
     * @param displayName The name as it is displayed to the user in-game.
     * @param name the name of the item as it is stored in the vault.
     * @param rarity the {@link ItemRarity} of the item, see enum.
     * @param clazz the {@link ItemClass} of the item, see enum.
     * @param imageLocation the location as it appears relative to the directory inside "./cards/media/".
     *                      This is NOT the location where the card is stored.
     * @param description the description of the card as it is displayed to the user.  This will automatically
     *                    change the names of {@link ItemClass}es to be the colour they need to be (such as
     *                    red for FIRE, but the names of the classes must be IN ALL CAPS...)
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
                   @NotNull ItemUses use,
                   int itemID,
                   double accuracy,
                   int maximumItemCount,
                   @NotNull ConsumerAction action) {
        if(ITEM_IDS.contains(itemID))
            throw new RuntimeException(String.format("An item ID of %d is already registered, so %s cannot have it.", itemID, name));
        this.displayName = displayName;
        this.name = name;
        this.rarity = rarity;
        this.itemClass = clazz;
        this.imageLocation = imageLocation;
        this.description = description;
        this.use = use;
        this.itemID = itemID;
        this.accuracy = accuracy;
        this.maximumItemCount = maximumItemCount;
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
        registerFireCards();
        
        //do this last
        generateAllImages();
    }
    
    private static void registerFireCards() {
        Logger.log("Registering fire cards");
        addToItems(new RPGItem(
                "Minor Wrath of Hamderforge",
                "wrath_of_hamderforge_i",
                ItemRarity.CRYSTAL,
                ItemClass.FIRE,
                "wrath_of_hamderforge_i",
                "Deal 25 to 35 FIRE damage.",
                ItemUses.BATTLE,
                1,
                0.75,
                900,
                (guildID, caster, friendlyUsers, enemyUsers, targets, item, casterClass) -> {
                    int rand = ThreadLocalRandom.current().nextInt(25, 36); //25 to 35
                    targets[0].setHealthCurrent(targets[0].getHealthCurrent() - rand);
                    return String.format("%s has dealt %d damage to %s using %s!", caster.getUsername(), rand, targets[0].getUsername(), item.getName());
                }
        ));
        addToItems(new RPGItem(
                "TODO", //TODO
                "todo",
                ItemRarity.EMERALD,
                ItemClass.FIRE,
                "todo",
                "Deal 1 FIRE damage, then deal 40 FIRE damage.",
                ItemUses.BATTLE,
                2,
                0.75,
                700,
                (guildID, caster, friendlyUsers, enemyUsers, targets, item, casterClass) -> {
                    targets[0].setHealthCurrent(targets[0].getHealthCurrent() - 1);
                    targets[0].setHealthCurrent(targets[0].getHealthCurrent() - 40);
                    return ""; //FIXME
                }
        ));
    }
    
    private static void registerDevCards() {
        Logger.log("Registering dev cards that do nothing i guess");
        addToItems(new RPGItem(
                "SMITE",
                "smite",
                ItemRarity.PAINITE,
                ItemClass.FIRE,
                "smite.png",
                "Deal " + Integer.MAX_VALUE + " STORM damage.",
                ItemUses.BATTLE,
                -5,
                1.00,
                0,
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
                ItemUses.BATTLE,
                -4,
                1.00,
                0,
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
                "HEAL " + Integer.MAX_VALUE + " health.",
                ItemUses.NOT_DIALOGUE,
                -3,
                1.00,
                0,
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
                "HEAL -500 health.",
                ItemUses.NOT_DIALOGUE,
                -2,
                1.00,
                0,
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
                ItemUses.BATTLE,
                -1,
                1.00,
                0,
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
                "Give a user the Oliy and Friends role in Oliy Island.\nThis is only usable by Oliy.",
                ItemUses.ANYTIME,
                -100,
                1.00,
                1,
                (guildID, caster, friendlyUsers, enemyUsers, targets, item, casterClass) -> {
                    if(!caster.getUserID().equals("129908908096487424"))
                        return "Error: only Oliy is allowed to use this item.  Become him and try again.";
                    try {
                        Objects.requireNonNull(Main.jda.getGuildById(419422246168166400L))
                                .addRoleToMember(targets[0].toString(),
                                        Objects.requireNonNull(Main.jda.getRoleById(755412615605518469L)))
                                .queue();
                        return "Wow it actually worked!";
                    } catch(Throwable t) {
                        return "<@" + targets[0] + "> is not Oliy's friend :(";
                    }
                } 
        ));
    }
    
    /**
     * Renders all the cards into images to be used.
     */
    public static void generateAllImages() {
        Logger.log("Generating images, this might take a while");
        RPGCardDrawer.renderCards();
        Logger.log("Done!");
    }
    
    /**
     * toString method.
     * 
     * Get the name of the card, this is the same
     * as {@link RPGItem#getName()}.
     * 
     * @return the name of the card.
     */
    @Override
    public String toString() {
        return this.name;
    }
    
    /**
     * Check to see if two RPGItems are equal.
     * 
     * Checks to see if the Object is the same type
     * then compares the names as described in
     * {@link RPGItem#getName()} and compares it with
     * {@code this}.
     * 
     * @param o the item to compare
     * @return {@code true} if the two items have the same name, {@code false} otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if(!(o instanceof RPGItem))
            return false;
        return ((RPGItem) o).name.equals(this.name);
    }
    
}
