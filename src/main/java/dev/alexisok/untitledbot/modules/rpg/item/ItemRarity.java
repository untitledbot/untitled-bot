package dev.alexisok.untitledbot.modules.rpg.item;

/**
 * Enumeration for item rarity, along with their shorter name.
 * 
 * @author AlexIsOK
 * @since 1.4.0
 */
public enum ItemRarity {
    
    CRYSTAL("D"), EMERALD("C"), RUBY("B"), SAPPHIRE("A"), PLATINUM("S"), DIAMOND("SS");
    
    private final String name;
    
    ItemRarity(String name) {
        this.name = name;
    }
}
