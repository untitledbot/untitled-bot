package dev.alexisok.untitledbot.data;

import dev.alexisok.untitledbot.Main;
import dev.alexisok.untitledbot.logging.Logger;

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
	 * @param ID the user's Discord ID snowflake
	 * @param key the KEY.
	 * @return the content of the key for the given user.  Returns {@code null} if the key cannot be found.
	 * @throws UserDataCouldNotBeObtainedException if there is an exception getting the user's data.
	 */
	public static String getKey(String ID, String key) throws UserDataCouldNotBeObtainedException {
		checkUserExists(ID);
		Properties p = new Properties();
		try {
			p.load(new FileReader(Main.DATA_PATH + ID + ".properties"));
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
	 * @param ID the Discord ID snowflake of a user.
	 * @param key the KEY to get.
	 * @param value the value to set.
	 *              Can be either blank or {@code null} to
	 *              unset a key.
	 */
	public static void setKey(String ID, String key, String value) {
		checkUserExists(ID);
		Properties p = new Properties();
		try {
			p.load(new FileReader(Main.DATA_PATH + ID + ".properties"));
			p.setProperty(key, value);
			p.store(new FileOutputStream(Main.DATA_PATH + ID + ".properties"), null);
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
	 */
	private static void checkUserExists(String ID) {
		if(!new File(Main.DATA_PATH + ID + ".properties").exists())
			createUserProfile(ID);
	}
	
	/**
	 * Create a user profile.  Should only be called by {@link UserData#checkUserExists(String)}.
	 * @param ID the Discord ID snowflake of the user.
	 */
	@SuppressWarnings("ResultOfMethodCallIgnored")
	private static void createUserProfile(String ID) {
		Logger.log("Creating a user profile for user <@" + ID + ">");
		Logger.log("User was created " + (Long.parseLong(ID) >> 22) + 1420070400000L);
		try {
			//A file with the name does not exist, this was already checked.
			new File(Main.DATA_PATH + ID + ".properties").createNewFile();
		} catch (IOException e) {
			Logger.critical("Could not create a data directory!", -1, false);
			//to be caught and reported to the end user over Discord.
			e.printStackTrace();
			throw new UserDataFileCouldNotBeCreatedException();
		}
	}
	
}
