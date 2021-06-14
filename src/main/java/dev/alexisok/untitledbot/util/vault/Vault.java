package dev.alexisok.untitledbot.util.vault;

import dev.alexisok.untitledbot.Main;
import dev.alexisok.untitledbot.data.UserData;
import dev.alexisok.untitledbot.data.UserDataCouldNotBeObtainedException;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.util.*;

/**
 * Note: this is a STATIC module, but it is the only static module.
 * It translates into I made this in the wrong package and I have all
 * of the JavaDoc pointing to this as a module, so I might as well
 * keep it here.<br>
 * 
 * This is a module (built-in plugin) for handling storage things.
 * Plugins should use this module for things like economy handling.
 * It can also be used for other things, so feel free to be
 * creative with this.
 * 
 * @author AlexIsOK
 * @since 0.0.1
 */
@SuppressWarnings("DuplicatedCode")
public final class Vault {
    
    private Vault(){}
    
    private static final HashMap<String, String> DEFAULT_DATA = new HashMap<>();
    
    private static final Object TIMER = new Object();
    
    /**
     * Get a new {@link HashMap} that has the same data as the default data.
     * @return the {@link HashMap}.
     */
    @NotNull
    @Contract(pure = true)
    public static HashMap<String, String> getDefaultData() {
        return new HashMap<>(DEFAULT_DATA);
    }
    
    /**
     * Set the default data for something.  Used when a new user file is created.
     * @param key the key.
     * @param data the data.
     */
    public static void addDefault(String key, String data) {
        DEFAULT_DATA.put(key, data);
    }
    
    /**
     * Store user data in the correct directory.
     * 
     * @param userID the ID of the user.  Can be {@code null} for global config.
     * @param guildID the ID of the guild (server).
     * @param dataKey the key of the data to store.
     * @param dataValue the data that will be stored under the key.
     * @throws UserDataCouldNotBeObtainedException if the user data could not be obtained.
     */
    public static void storeUserDataLocal(String userID, String guildID, @NotNull String dataKey, @NotNull String dataValue) throws UserDataCouldNotBeObtainedException {
        synchronized(TIMER) {
            UserData.checkUserExists(userID, guildID);
            Properties p = new Properties();
            try {
                p.load(new FileReader(Main.parsePropertiesLocation(userID, guildID)));
                p.setProperty(dataKey, dataValue);
                p.store(new FileOutputStream(Main.parsePropertiesLocation(userID, guildID)), "");
            } catch (IOException e) {
                e.printStackTrace();
                //to be caught and reported to the end user over Discord.
                throw new UserDataCouldNotBeObtainedException();
            }
        }
    }

    /**
     * Get the raw data from the file.
     * @param userID the ID of the user.
     * @param guildID the ID of the guild.
     * @return the raw Properties file.
     * @throws UserDataCouldNotBeObtainedException if the user data could not be obtained.
     */
    @NotNull
    @Contract(pure = true)
    public static Properties getRawData(String userID, String guildID) throws UserDataCouldNotBeObtainedException{
        synchronized(TIMER) {
            try {
                Properties p = new Properties();
                p.load(new FileInputStream(Main.parsePropertiesLocation(userID, guildID)));
                return p;
            } catch(IOException e) {
                e.printStackTrace();
                throw new UserDataCouldNotBeObtainedException();
            }
        }
    }
    
    /**
     * Get the data of a user or guild.
     * 
     * @param userID the user ID, pass {@code null} for guild only.
     * @param guildID the guild ID.
     * @param dataKey the key to use to get the data.
     * @see Properties#getProperty(String)
     * @throws UserDataCouldNotBeObtainedException if the user data could not be obtained.
     */
    public static String getUserDataLocal(String userID, String guildID, @NotNull String dataKey) throws UserDataCouldNotBeObtainedException {
        synchronized(TIMER) {
            UserData.checkUserExists(userID, guildID);
            Properties p = new Properties();
            try {
                p.load(new FileReader(Main.parsePropertiesLocation(userID, guildID)));
                return p.getProperty(dataKey);
            } catch (IOException e) {
                e.printStackTrace();
                throw new UserDataCouldNotBeObtainedException();
            }
        }
    }
    
    /**
     * Get the data of a user or guild, or fallback to the default value if it is not found.
     *
     * @param userID the user ID, pass {@code null} for guild only.
     * @param guildID the guild ID.
     * @param dataKey the key to use to get the data.
     * @param defaultValue value to be returned if the key was not found.
     * @see Properties#getProperty(String)
     * @throws UserDataCouldNotBeObtainedException if the user data could not be obtained.
     */
    public static String getUserDataLocalOrDefault(String userID, String guildID, @NotNull String dataKey, @NotNull String defaultValue)
            throws UserDataCouldNotBeObtainedException {
        synchronized(TIMER) {
            UserData.checkUserExists(userID, guildID);
            Properties p = new Properties();
            try {
                p.load(new FileReader(Main.parsePropertiesLocation(userID, guildID)));
                return p.getProperty(dataKey, defaultValue);
            } catch(IOException e) {
                e.printStackTrace();
                throw new UserDataCouldNotBeObtainedException();
            }
        }
    }
    
}
