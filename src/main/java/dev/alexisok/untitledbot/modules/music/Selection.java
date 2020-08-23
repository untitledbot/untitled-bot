package dev.alexisok.untitledbot.modules.music;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

/**
 * contains data for selections using the music bot.
 * 
 * @author AlexIsOK
 * @since 1.0.1
 */
final class Selection {
    
    /**
     * Selected videos.
     * 
     * First String is the name, second is the URL.
     */
    private final HashMap<String, String> selectedVideos = new HashMap<>();
    
    /**
     * Selection constructor
     * @param names the names of the videos.
     * @param URLs the URLs for the videos.
     * @throws IllegalArgumentException if the length of the two parameters are not equal.
     */
    Selection(String @NotNull [] names, String @NotNull [] URLs) throws IllegalArgumentException {
        if (names.length != URLs.length) throw new IllegalArgumentException();
        
        for(int i = 0; i < names.length; i++) {
            selectedVideos.put(names[i], URLs[i]);
        }
    }
    
    /**
     * Get the selection
     * @return the selection
     */
    @NotNull
    HashMap<String, String> getSelection() {
        return this.selectedVideos;
    }
    
}
