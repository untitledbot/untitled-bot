package dev.alexisok.untitledbot.util.vault;

import org.jetbrains.annotations.NotNull;

public interface GetDataCompletion {
    
    void getData(String userID, String guildID, @NotNull String dataKey, String dataValue);
    
}
