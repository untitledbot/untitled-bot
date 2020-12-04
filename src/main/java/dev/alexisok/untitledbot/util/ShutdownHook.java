package dev.alexisok.untitledbot.util;

import dev.alexisok.untitledbot.BotClass;

/**
 * Whenever the shutdown command is run, this is called.
 * 
 * Make sure to register the hook with {@link BotClass#registerShutdownHook(ShutdownHook)}
 * 
 * @author AlexIsOK
 * @since 1.3.24
 */
public interface ShutdownHook {
    
    void onShutdown();
    
}
