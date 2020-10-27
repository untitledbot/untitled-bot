package dev.alexisok.untitledbot.modules.rpg.item;

import dev.alexisok.untitledbot.modules.rpg.RPGUser;
import org.jetbrains.annotations.NotNull;

/**
 * Callback for when an item is used.
 * 
 * @author AlexIsOK
 * @since 1.4.0
 */
public interface ConsumerAction {
    
    String onItemUse(@NotNull String guildID,
                     @NotNull RPGUser caster,
                     @NotNull RPGUser[] friendlyUsers,
                     @NotNull RPGUser[] enemyUsers,
                     @NotNull RPGUser[] targets,
                     @NotNull RPGItem item,
                     @NotNull ItemClass casterClass);
    
}
