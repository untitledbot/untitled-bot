package dev.alexisok.untitledbot.modules.vault;

/**
 * Vault operations to reduce the risk of corruption i guess
 * 
 * @author AlexIsOK
 * @since 1.3
 */
final class VaultOperation {
    
    //not private
    String userID, guildID, dataKey, dataValue;
    GetDataCompletion vc;
    
    VaultOperation(String userID, String guildID, String dataKey, String dataValue, GetDataCompletion vc) {
        this.userID = userID;
        this.guildID = guildID;
        this.dataKey = dataKey;
        this.dataValue = dataValue;
        this.vc = vc;
    }
}
