package dev.alexisok.untitledbot.modules.rpg.item;

import lombok.Getter;

/**
 * @author AlexIsOK
 * @since 1.4.0
 */
public enum ItemClass {
    
    BALANCE("Balance"),
    DEATH("Death"),
    FIRE("Fire"),
    FROST("Frost"),
    LIFE("Life"),
    STORM("Storm");
    
    @Getter
    private final String displayName;
    
    ItemClass(String displayName) {
        this.displayName = displayName;
    }
}
