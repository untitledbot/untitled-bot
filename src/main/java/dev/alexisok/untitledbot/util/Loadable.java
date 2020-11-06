package dev.alexisok.untitledbot.util;

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
     * @param load the String to load.
     */
    void load(String load);

    /**
     * Store the data to the disk.
     * @return the data to be stored
     */
    String store();
}
