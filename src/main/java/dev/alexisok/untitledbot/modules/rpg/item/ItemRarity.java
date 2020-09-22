package dev.alexisok.untitledbot.modules.rpg.item;

/**
 * Enumeration for item rarity, along with their shorter name.
 * 
 * From worst to best, the rarity is D, C, B, A, S, SS.
 * 
 * @author AlexIsOK
 * @since 1.4.0
 */
public enum ItemRarity {
    
    CRYSTAL("D"), EMERALD("C"), RUBY("B"), SAPPHIRE("A"), PLATINUM("S"), DIAMOND("SS"), PAINITE("DEV");
    
    private final String name;
    
    ItemRarity(String name) {
        this.name = name;
    }
}
