package dev.alexisok.untitledbot.modules.music.util;

import org.jetbrains.annotations.NotNull;

/**
 * @author AlexIsOK
 * @since 1.3.23
 */
public interface QueryCallback {
    
    void onResults(@NotNull String results);
    
    void onError(@NotNull Throwable error);
    
}
