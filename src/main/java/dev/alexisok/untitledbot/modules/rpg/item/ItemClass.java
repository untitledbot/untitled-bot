package dev.alexisok.untitledbot.modules.rpg.item;

import lombok.Getter;

/**
 * @author AlexIsOK
 * @since 1.4.0
 */
public enum ItemClass {
    
    FIRE("Fire"), FROST("Frost"), LIFE("Life"), DEATH("Death"), STORM("Storm"), BALANCE("Balance"), ALL("All");
    
    @Getter
    private final String displayName;
    
    ItemClass(String displayName) {
        this.displayName = displayName;
    }
}
