package dev.alexisok.untitledbot.data;

import dev.alexisok.untitledbot.Main;
import dev.alexisok.untitledbot.logging.Logger;
import dev.alexisok.untitledbot.modules.vault.Vault;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * 
 * Contains information on data of users.
 * SQL support may come soon, but for now everything
 * will be stored using .properties files.  The
 * reason for that is because I'm not good with SQL.
 * If you are good with SQL and know Java then
 * please make a pull request with the features.
 * Any help with SQL would be appreciated.
 * 
 * @author AlexIsOK
 * @since 0.0.1
 */
public class UserData {
	
	/**
	 * Get a specific KEY of a user.
	 * 
	 * @param userID the user's Discord userID snowflake
	 * @param key the KEY.
	 * @return the content of the key for the given user.  Returns {@code null} if the key cannot be found.
	 * @throws UserDataCouldNotBeObtainedException if there is an exception getting the user's data.
	 */
	public static @Nullable String getKey(String userID, String guildID, String key) throws UserDataCouldNotBeObtainedException {
		return Vault.getUserDataLocal(userID, guildID, key);
	}
	
	/**
	 * Set a specific KEY of a user.
	 * 
	 * To remove a key, specify a blank or {@code null} value.
	 * 
	 * @param userID the Discord ID snowflake of a user.
	 * @param key the KEY to get.
	 * @param value the value to set.
	 *              Can be either blank or {@code null} to
	 *              unset a key.
	 */
	public static void setKey(String userID, String guildID, String key, String value) {
		Vault.storeUserDataLocal(userID, guildID, key, value);
	}
	
	/**
	 * Check to see if the user is in the database, creates a new user
	 * if one does not exist.  Does not return a boolean or anything
	 * for that matter.
	 * @param userID the discord snowflake of the user.
	 * @param guildID the discord snowflake of the guild.   
	 */
	public static void checkUserExists(String userID, String guildID) {
		if(!new File(Main.parsePropertiesLocation(userID, guildID)).exists())
			createUserProfile(userID, guildID);
	}
	
	/**
	 * Create a user profile.  Should only be called by {@link UserData#checkUserExists(String, String)}.
	 * 
	 * @param userID the discord snowflake of the user.
	 * @param guildID the discord snowflake of the guild.   
	 */
	private static void createUserProfile(String userID, String guildID) {
		Logger.log("Creating a user profile for user <@" + userID + "> in " + guildID);
		try {
			//create the guild directory if it does not exist
			Files.createDirectories(Paths.get(Main.parsePropertiesLocation(null, guildID).replace(".properties", "")));
			
			//A file with the name does not exist, this was already checked.
			if(!new File(Main.parsePropertiesLocation(userID, guildID)).exists()) {
				try(PrintWriter pw = new PrintWriter(Main.parsePropertiesLocation(userID, guildID))) {
					pw.println("# this file was created by createUserProfile(String, String)");
				}
			}
		} catch (IOException e) {
			Logger.critical("Could not create a data!  " + userID + ", " + guildID + ".", -1, false);
			//to be caught and reported to the end user over Discord.
			e.printStackTrace();
			throw new UserDataFileCouldNotBeCreatedException();
		}
	}
	
}
