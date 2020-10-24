package dev.alexisok.untitledbot.modules.rpg.exception;

import dev.alexisok.untitledbot.modules.rpg.item.RPGItem;

/**
 * 
 * Thrown if a consumable {@link RPGItem} was not used correctly.
 * 
 * The message parameter for the constructor inherited by {@link RuntimeException} is required.
 * 
 * @author AlexIsOK
 * @since 1.4.0
 * @see RuntimeException
 */
public final class ConsumableItemWasNotConsumedCorrectlyException extends RuntimeException {
    
    /**
     * Thrown when an {@link RPGItem} was not used correctly or was denied for some reason
     * (e.g., only specific room, needs other item, etc.)  The message parameter will be displayed
     * to the user along with a red message embed.
     * @param message the message to be displayed to the user.
     */
    public ConsumableItemWasNotConsumedCorrectlyException(String message) {
        super(message);
    }
    
}
