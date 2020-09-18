package dev.alexisok.untitledbot.modules.rpg.item;

/**
 * Describes types for items.
 * 
 * @author AlexIsOK
 * @since 1.4.0
 */
public enum ItemType {
    POTION(true, false),
    SWORD(false, true);
    
    private final boolean usable;
    private final boolean weapon;
    
    /**
     * Describes item types.
     * @param usable if the item is consumable (such as a potion or food)
     * @param weapon if the item is a weapon (such as a sword or bow)
     */
    ItemType(boolean usable, boolean weapon) {
        this.usable = usable;
        this.weapon = weapon;
    }
}
