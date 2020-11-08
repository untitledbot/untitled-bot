package dev.alexisok.untitledbot.modules.music.util;

import org.jetbrains.annotations.NotNull;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Gets search results from a YouTube query
 * 
 * @author AlexIsOK
 * @since 1.3.23
 */
public final class YTUtil {
    
    private YTUtil() {}
    
    /**
     * Search YouTube and return results.
     * 
     * @param query string to search for on yt
     * @param callback the callback
     */
    public static synchronized void searchYouTube(@NotNull String query, @NotNull QueryCallback callback) {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                
            }
        }, 0);
    }
    
}
