package dev.alexisok.untitledbot.modules.vault;

import dev.alexisok.untitledbot.Main;
import dev.alexisok.untitledbot.data.UserData;
import dev.alexisok.untitledbot.data.UserDataCouldNotBeObtainedException;
import org.jetbrains.annotations.NotNull;

import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import static dev.alexisok.untitledbot.data.UserData.checkUserExists;

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
 * {@link UserData} can be used for these things as well, but it
 * only supports storing data for users directly.
 * 
 * @author AlexIsOK
 * @since 0.0.1
 */
public class Vault {

    /**
     * Store user data in the correct directory.
     * 
     * @param userID the ID of the user.  Can be {@code null} for global config.
     * @param guildID the ID of the guild (server).
     * @param dataKey the key of the data to store.
     * @param dataValue the data that will be stored under the key.
     * @throws UserDataCouldNotBeObtainedException if the user data could not be obtained.
     */
    public static void storeUserDataLocal(String userID, @NotNull String guildID, @NotNull String dataKey, @NotNull String dataValue)
            throws UserDataCouldNotBeObtainedException {
        checkUserExists(userID, guildID);
        Properties p = new Properties();
        try {
            p.load(new FileReader(Main.parsePropertiesLocation(userID, guildID)));
            p.setProperty(dataKey, dataValue);
            p.store(new FileOutputStream(Main.parsePropertiesLocation(userID, guildID)), null);
        } catch (IOException e) {
            e.printStackTrace();
            //to be caught and reported to the end user over Discord.
            throw new UserDataCouldNotBeObtainedException();
        }
    }
    
    /**
     * Get the data of a user or guild.
     * 
     * @param userID the user ID, pass {@code null} for guild only.
     * @param guildID the guild ID.
     * @param dataKey the key to use to get the data.
     * @return the user's data, or {@code null} if it was not found.
     * @see Properties#getProperty(String)
     * @throws UserDataCouldNotBeObtainedException if the user data could not be obtained.
     */
    public static String getUserDataLocal(String userID, @NotNull String guildID, @NotNull String dataKey)
            throws UserDataCouldNotBeObtainedException{
        checkUserExists(userID, guildID);
        Properties p = new Properties();
        try {
            p.load(new FileReader(Main.parsePropertiesLocation(userID, guildID)));
            return p.getProperty(dataKey);
        } catch(IOException e) {
            e.printStackTrace();
            throw new UserDataCouldNotBeObtainedException();
        }
    }
    
}
