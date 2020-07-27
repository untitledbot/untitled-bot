package dev.alexisok.untitledbot.modules.vault;

import dev.alexisok.untitledbot.Main;
import dev.alexisok.untitledbot.data.UserData;
import dev.alexisok.untitledbot.data.UserDataCouldNotBeObtainedException;
import dev.alexisok.untitledbot.logging.Logger;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
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
    
    private static final HashMap<String, String> DEFAULT_DATA = new HashMap<>();
    
    private static volatile ArrayList<VaultOperation> OPERATIONS = new ArrayList<>();
    
    private static boolean running = false;
    
    /**
     * Nested class for the operations to finish up.
     */
    public static class OperationHook extends Thread {
        @Override
        public void run() {
            Logger.log("Cleaning up...");
            Logger.log("VAULT: there are " + OPERATIONS.size() + " items left in the queue.");
            
            while(OPERATIONS.size() != 0) {
                if(running)
                    return;
                if(OPERATIONS.size() == 0)
                    return;
                running = true;
    
                try {
                    VaultOperation va = OPERATIONS.get(0);
                    OPERATIONS.remove(0);
                    storeUserDataPiped(va);
                } catch(Throwable t) {
                    t.printStackTrace();
                }
                running = false;
            }
        }
    }
    
    /**
     * Register the operation scheduler.
     * THIS SHOULD NOT BE RUN BY ANYTHING BUT THE MAIN METHOD.
     */
    public static void operationScheduler() {
        Thread t = new OperationHook();
        Runtime.getRuntime().addShutdownHook(t);
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                if(running)
                    return;
                if(OPERATIONS.size() == 0)
                    return;
                running = true;
                
                try {
                    VaultOperation va = OPERATIONS.get(0);
                    OPERATIONS.remove(0);
                    storeUserDataPiped(va);
                } catch(Throwable t) {
                    t.printStackTrace();
                }
                running = false;
            }
        };
    
        new Timer().schedule(task, 0L, 20); //20 ms
    }
    
    /**
     * Get a new {@link HashMap} that has the same data as the default data.
     * @return the {@link HashMap}.
     */
    @Contract(" -> new")
    public static @NotNull HashMap<String, String> getDefaultData() {
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
    public static void storeUserDataLocal(String userID, String guildID, @NotNull String dataKey, @NotNull String dataValue)
            throws UserDataCouldNotBeObtainedException {
        if(!shutdownFileInPlace())
            OPERATIONS.add(new VaultOperation(userID, guildID, dataKey, dataValue));
        else {
            //noinspection ResultOfMethodCallIgnored
            new File("shutdown.ub").delete();
            long time = System.currentTimeMillis();
            while(OPERATIONS.size() != 0) {
                if(time > System.currentTimeMillis() + 10000L)
                    break;
            }
            Runtime.getRuntime().exit(0);
        }
    }
    
    /**
     * @return true if the shutdown file exists.
     */
    private static boolean shutdownFileInPlace() {
        return new File("shutdown.ub").exists();
    }
    
    /**
     * Piped data
     * @param ve the vault operation
     */
    private static void storeUserDataPiped(@NotNull VaultOperation ve) {
        UserData.checkUserExists(ve.userID, ve.guildID);
        Properties p = new Properties();
        try {
            p.load(new FileReader(Main.parsePropertiesLocation(ve.userID, ve.guildID)));
            p.setProperty(ve.dataKey, ve.dataValue);
            p.store(new FileOutputStream(Main.parsePropertiesLocation(ve.userID, ve.guildID)), new Date().toString());
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
    public static String getUserDataLocal(String userID, String guildID, @NotNull String dataKey) throws UserDataCouldNotBeObtainedException {
        while(OPERATIONS.size() != 0); //this is so bad i hate myself for writing it
        UserData.checkUserExists(userID, guildID);
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
