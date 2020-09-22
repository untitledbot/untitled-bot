package dev.alexisok.untitledbot.modules.rpg;

import dev.alexisok.untitledbot.modules.vault.Vault;
import lombok.Getter;
import lombok.Setter;

/**
 * Describes users of the RPG.
 * 
 * @author AlexIsOK
 * @since 1.4.0
 */
public final class RPGUser {
    
    @Getter
    private final String userID;
    
    @Getter
    private final String guildID;
    
    @Getter
    private final String username;
    
    @Getter
    private long healthCurrent;
    
    @Getter
    private long healthMax;
    
    @Getter
    private long manaCurrent;
    
    @Getter
    private long manaMax;
    
    
    public RPGUser(String userID, String guildID, String username, long healthCurrent, long healthMax, long manaCurrent, long manaMax) {
        this.userID = userID;
        this.guildID = guildID;
        this.username = username;
        this.healthCurrent = healthCurrent;
        this.healthMax = healthMax;
        this.manaCurrent = manaCurrent;
        this.manaMax = manaMax;
    }
    
    /**
     * Set the health current.  This will also modify the VAULT value.
     *
     * @param newValue the new value to set.
     */
    public void setHealthCurrent(long newValue) {
        this.healthCurrent = Math.min(newValue, this.healthMax);
        Vault.storeUserDataLocal(this.userID, this.guildID, RPGVaultKeys.HEALTH_CURRENT, String.valueOf(healthCurrent));
    }
    
    /**
     * Set the health maximum.  This will also modify the VAULT value.
     *
     * @param newValue the new value to set.
     */
    public void setHealthMaximum(long newValue) {
        this.healthMax = newValue;
        Vault.storeUserDataLocal(this.userID, this.guildID, RPGVaultKeys.HEALTH_MAXIMUM, String.valueOf(newValue));
    }
    
    /**
     * Set the mana current.  This will also modify the VAULT value.
     *
     * @param newValue the new value to set.
     */
    public void setManaCurrent(long newValue) {
        this.manaCurrent = newValue;
        Vault.storeUserDataLocal(this.userID, this.guildID, RPGVaultKeys.MANA_CURRENT, String.valueOf(newValue));
    }
    
    /**
     * Set the mana maximum.  This will also modify the VAULT value.
     *
     * @param newValue the new value to set.
     */
    public void setManaMax(long newValue) {
        this.manaMax = newValue;
        Vault.storeUserDataLocal(this.userID, this.guildID, RPGVaultKeys.MANA_MAXIMUM, String.valueOf(newValue));
    }
    
    
}
