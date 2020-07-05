package dev.alexisok.untitledbot.modules.vault;

import dev.alexisok.untitledbot.Main;
import dev.alexisok.untitledbot.data.UserData;
import dev.alexisok.untitledbot.data.UserDataCouldNotBeObtainedException;

import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import static dev.alexisok.untitledbot.data.UserData.checkUserExists;

/**
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
     * Store local user data.  Local data is user data that is stored under a guild,
     * and should only be accessed if the request originated in the same guild.
     * 
     * @param userID the ID of the user.
     * @param guildID the ID of the guild (server).
     * @param dataKey the key of the data to store.
     * @param dataValue the data that will be stored under the key.
     */
    public static void storeUserDataLocal(String userID, String guildID, String dataKey, String dataValue) {
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
    
}
