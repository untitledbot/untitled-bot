package dev.alexisok.untitledbot.modules.rpg;

import dev.alexisok.untitledbot.modules.rpg.exception.RPGDataFileHasAlreadyBeenInitializedException;
import dev.alexisok.untitledbot.modules.vault.Vault;
import org.jetbrains.annotations.NotNull;

import static dev.alexisok.untitledbot.modules.rpg.RPGVaultKeys.*;

/**
 * Initialize the RPG file for someone's data file.
 * 
 * @author AlexIsOK
 * @since 1.4.0
 */
final class Init {
    
    /**
     * Initialize the RPG file for a data file.
     * 
     * Adds all the needed vault keys that the user will need to start playing.
     * 
     * @param userID the Discord ID of the user as a String.
     * @param guildID the Discord ID of the guild as a String.
     * @throws RPGDataFileHasAlreadyBeenInitializedException if the file has been initialized already.
     */
    protected static void init(@NotNull String userID, @NotNull String guildID) throws RPGDataFileHasAlreadyBeenInitializedException {
        if(Vault.getUserDataLocalOrDefault(userID, guildID, "rpg.started", "false").equals("true"))
            throw new RPGDataFileHasAlreadyBeenInitializedException();
        
        //oh god this is going to eat my disk alive i need to start using sql
        Vault.storeUserDataLocal(userID, guildID, STARTED, "true");
        Vault.storeUserDataLocal(userID, guildID, LEVEL, "1");
        Vault.storeUserDataLocal(userID, guildID, XP, "0");
        Vault.storeUserDataLocal(userID, guildID, POW, "5");
        Vault.storeUserDataLocal(userID, guildID, DEF, "5");
        Vault.storeUserDataLocal(userID, guildID, HEALTH_CURRENT, "100");
        Vault.storeUserDataLocal(userID, guildID, HEALTH_MAXIMUM, "100");
        Vault.storeUserDataLocal(userID, guildID, GOLD, "50");
        Vault.storeUserDataLocal(userID, guildID, SILVER, "300");
    }
    
}
