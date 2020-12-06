package dev.alexisok.untitledbot.modules.rpg;

import dev.alexisok.untitledbot.modules.rpg.battle.RPGBattle;
import lombok.Data;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Data for the RPG.
 * 
 * @author AlexIsOK
 * @since 1.3.25
 */
@Data
public final class RPGData {
    
    @Contract(pure = true)
    protected RPGData(long userID, long guildID) {
        this.userID = userID;
        this.guildID = guildID;
    }
    
    private final long userID, guildID;
    
    private boolean isFleeing;
    
    @Nullable
    private RPGBattle battle;
    
    public boolean isInBattle() {
        return battle != null;
    }
}
