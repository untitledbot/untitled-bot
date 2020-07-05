package dev.alexisok.untitledbot.data;

import dev.alexisok.untitledbot.Main;
import dev.alexisok.untitledbot.annotation.ToBeRemoved;
import dev.alexisok.untitledbot.logging.Logger;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.util.Properties;

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
		checkUserExists(userID);
		Properties p = new Properties();
		try {
			p.load(new FileReader(Main.parsePropertiesLocation(userID, guildID)));
			if(p.containsKey(key))
				return p.getProperty(key);
			else
				return null;
		} catch (IOException e) {
			e.printStackTrace();
			//to be caught and reported to the end user over Discord.
			throw new UserDataCouldNotBeObtainedException();
		}
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
		checkUserExists(userID);
		Properties p = new Properties();
		try {
			p.load(new FileReader(Main.parsePropertiesLocation(userID, guildID)));
			p.setProperty(key, value);
			p.store(new FileOutputStream(Main.parsePropertiesLocation(userID, guildID)), null);
		} catch (IOException e) {
			e.printStackTrace();
			//to be caught and reported to the end user over Discord.
			throw new UserDataCouldNotBeObtainedException();
		}
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
			//A file with the name does not exist, this was already checked.
			new File(Main.parsePropertiesLocation(userID, guildID)).createNewFile();
		} catch (IOException e) {
			Logger.critical("Could not create a data directory!", -1, false);
			//to be caught and reported to the end user over Discord.
			e.printStackTrace();
			throw new UserDataFileCouldNotBeCreatedException();
		}
	}
	
	/**
	 * This is now deprecated, as guild IDs will be used instead.
	 * @deprecated
	 * @see UserData#checkUserExists(String, String) 
	 * @param ID the user ID
	 */
	@Deprecated(since="0.0.1")
	@ToBeRemoved("2.0.0")
	public static void checkUserExists(String ID) {
		throw new RuntimeException();
	}
	
	/**
	 * Create a user profile.  Should only be called by {@link UserData#checkUserExists(String)}.
	 * @see UserData#createUserProfile(String, String) 
	 * @param ID the Discord ID snowflake of the user.
	 */
	@ToBeRemoved("2.0.0")
	@Deprecated(since="0.0.1")
	@SuppressWarnings("ResultOfMethodCallIgnored")
	private static void createUserProfile(String ID) {
		throw new RuntimeException();
	}
	
}
