package dev.alexisok.untitledbot.util;

import java.io.File;
import java.util.HashMap;

/**
 * Implies that the object can be loaded and stored to
 * and from a file on disk.
 * 
 * @author AlexIsOK
 * @since 1.3.23
 */
public interface Loadable {

    /**
     * Load data from disk as a String.
     * @param load the file to load.
     */
    String load(File load);
    
    /**
     * Store the data to the disk.
     * @return the data to be stored
     */
    String store();
    
}
