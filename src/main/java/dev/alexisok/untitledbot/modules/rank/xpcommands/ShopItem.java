package dev.alexisok.untitledbot.modules.rank.xpcommands;

import org.jetbrains.annotations.NotNull;

/**
 * Describes shop item objects.  See {@link Shop#onRegister()} source for the items.
 * 
 * @author AlexIsOK
 * @since 1.3
 */
final class ShopItem {
    
    private final String name;
    private final String description;
    
    private final long costInLevels;
    
    private final int itemID;
    
    //max a user can have in their inventory, -1 is infinite
    private long maximum = -1;
    
    /**
     * Constructor for shop items.
     * @param name the name of the item.
     * @param costInLevels the cost in levels.
     * @param description the description.
     * @param itemID id of the item.
     * @throws AssertionError if itemID is less than 0 or costInLevels is less than 0.
     */
    protected ShopItem(@NotNull String name, long costInLevels, @NotNull String description, int itemID) {
        assert itemID >= 0;
        assert costInLevels >= 0;
        this.name = name;
        this.description = description;
        this.costInLevels = costInLevels;
        this.itemID = itemID;
    }
    
    /**
     * Second constructor for shop items.
     * @param name the name of the item.
     * @param costInLevels the cost in levels.
     * @param description the description.
     * @param itemID id of the item.
     * @param limit maximum amount of the item a user can have.  use -1 for unlimited.
     *              
     * @throws IllegalArgumentException if itemID is less than 0 or costInLevels is less than 0.
     */
    protected ShopItem(@NotNull String name, long costInLevels, @NotNull String description, int itemID, long limit) throws IllegalArgumentException {
        assert itemID >= 0;
        assert costInLevels >= 0;
        this.name = name;
        this.description = description;
        this.costInLevels = costInLevels;
        this.itemID = itemID;
        this.maximum = limit;
    }
    
    protected String getName() {
        return this.name;
    }
    
    protected String getDescription() {
        return this.description;
    }
    
    protected long getCostInLevels() {
        return this.costInLevels;
    }
    
    protected int getItemID() {
        return this.itemID;
    }
    
    protected long getMaximum() {return this.maximum;}
    
    protected void setMaximum(int maximum) {this.maximum = maximum;}
    
}
