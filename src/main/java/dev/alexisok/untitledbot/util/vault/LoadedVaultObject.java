package dev.alexisok.untitledbot.util.vault;

import com.google.errorprone.annotations.MustBeClosed;
import dev.alexisok.untitledbot.Main;
import dev.alexisok.untitledbot.data.UserData;
import dev.alexisok.untitledbot.data.UserDataCouldNotBeObtainedException;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.util.Properties;
import java.util.function.BiConsumer;

/**
 * Describes an object that can be loaded by the
 * Vault to read/write to a lot of times without
 * pinging the disk a lot of times.  Pensive.
 */
public class LoadedVaultObject implements AutoCloseable, Flushable {

    private final String userID;
    
    private final String guildID;
    
    private boolean flushed = false;
    
    private final Properties data = new Properties();
    
    /**
     * Constructor for the Vault Object.
     * 
     * @param userID the ID of the user.
     * @param guildID the ID of the guild.
     * @throws UserDataCouldNotBeObtainedException if there is an error.
     */
    @Contract
    @MustBeClosed
    public LoadedVaultObject(String userID, String guildID) throws UserDataCouldNotBeObtainedException, IOException {
        this.userID = userID;
        this.guildID = guildID;
        this.data.load(new FileReader(Main.parsePropertiesLocation(userID, guildID)));
    }
    
    /**
     * Save the data to the disk.  This is not needed
     * if the object is not used for writing data to
     * the properties file, otherwise written changes
     * will not be written to disk if this is not called.
     */
    @Override
    public void flush() {
        if(!this.flushed) {
            this.flushed = true;
            
            //write the data
            UserData.checkUserExists(this.userID, this.guildID);
            try {
                this.data.store(new FileOutputStream(Main.parsePropertiesLocation(this.userID, this.guildID)), "");
            } catch (IOException e) {
                e.printStackTrace();
                //to be caught and reported to the end user over Discord.
                throw new UserDataCouldNotBeObtainedException();
            }
        }
    }
    
    /**
     * Close and flush the object.  This is a convenience
     * method for try with resource blocks.
     */
    @Override
    public void close() {
        this.flush();
    }
    
    /**
     * Store user data.
     * @param key the key to store
     * @param value the value to store
     */
    @Contract
    public void storeUserDataLocal(@NotNull String key, @NotNull String value) {
        this.data.setProperty(key, value);
    }
    
    /**
     * Get user data.
     * @param key the key to get.
     * @return the value, or {@code null} if it does not exist.
     */
    @Nullable
    @Contract(pure = true)
    public String getUserDataLocal(@NotNull String key) {
        return this.data.getProperty(key);
    }
    
    /**
     * Get user data.
     * @param key the key to get.
     * @param defaultValue the default value.
     * @return the value, or {@code defaultValue} if it does not exist.
     */
    @NotNull
    @Contract(pure = true)
    public String getUserDataLocalOrDefault(@NotNull String key, @NotNull String defaultValue) {
        return this.data.getProperty(key, defaultValue);
    }
    
    @NotNull
    @Contract(pure = true)
    public Properties getData() {
        return this.data;
    }
}
