package dev.alexisok.untitledbot.modules.rpg.item;

/**
 * Describes types for items.
 * This may be removed in the future.
 * 
 * @author AlexIsOK
 * @since 1.4.0
 */
public enum ItemType {
    POTION(true),
    SPELL(true);
    
    private final boolean usable;
    
    /**
     * Describes item types.
     * @param usable if the item is consumable (such as a potion or food)
     */
    ItemType(boolean usable) {
        this.usable = usable;
    }
}
