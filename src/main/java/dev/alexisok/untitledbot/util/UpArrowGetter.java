package dev.alexisok.untitledbot.util;

import net.dv8tion.jda.api.entities.Message;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Get message above by counting like ^^^ for three messages up
 * 
 * @author AlexIsOK
 * @since 1.3.23
 */
public final class UpArrowGetter {
    
    private UpArrowGetter() {}
    
    
    @Nullable
    @Contract(pure = true)
    public static synchronized Message getAboveMessage(@NotNull Message base, int count) throws IllegalArgumentException {
        
    }
    
    @Nullable
    @Contract(pure = true)
    public static synchronized Message getAboveMessage(@NotNull String messageID, int count) throws IllegalArgumentException {
        
    }
    
}
