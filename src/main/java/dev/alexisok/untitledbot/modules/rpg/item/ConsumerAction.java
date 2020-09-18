package dev.alexisok.untitledbot.modules.rpg.item;

/**
 * Callback for when an item is used.
 * 
 * @author AlexIsOK
 * @since 1.4.0
 */
public interface ConsumerAction {
    
    /**
     * Called when an {@link RPGItem} is used.
     * @param userID the Discord ID of the user.
     * @param guildID the Discord ID of the guild.
     * @param targetID the Discord ID of the target.
     * @param item the {@link RPGItem}
     * @return the String that will be displayed to the user on use.
     */
    String onItemUse(String userID, String guildID, String targetID, RPGItem item);
    
}
