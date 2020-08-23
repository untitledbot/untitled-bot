package dev.alexisok.untitledbot.modules.basic.economy;

import dev.alexisok.untitledbot.modules.vault.Vault;

import java.util.concurrent.ThreadLocalRandom;

/**
 * @author AlexIsOK
 * @since 1.3
 */
public final class UpdateMoney {
    
    private static final long MONEY_PER_MESSAGE_MIN = 0L;
    private static final long MONEY_PER_MESSAGE_MAX = 32L;
    
    public static final String MONEY_MODULE = "economy.money";
    public static final String CURRENCY_SYM = "\u20BF";
    
    protected static void update(String userID, String guildID) {
        String current = Vault.getUserDataLocal(userID, guildID, MONEY_MODULE);
        
        if(current == null) {
            Vault.storeUserDataLocal(userID, guildID, MONEY_MODULE, "0");
            return;
        }
    
        Vault.storeUserDataLocal(userID,
                guildID,
                MONEY_MODULE,
                String.valueOf(Long.parseLong(current)) + ThreadLocalRandom.current().nextLong(MONEY_PER_MESSAGE_MIN,
                        MONEY_PER_MESSAGE_MAX + 1L)
        );
        
    }
    
}
