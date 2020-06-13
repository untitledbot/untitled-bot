package dev.alexisok.untitledbot.data;

import dev.alexisok.untitledbot.Main;
import dev.alexisok.untitledbot.logging.Logger;

import java.io.File;

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
	
	public static void getKey(String ID, String key) {
		
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
	
	private static void createUserProfile(String ID) {
		Logger.log("Creating a user profile for user ID <" + ID + ">");
	}
	
}
