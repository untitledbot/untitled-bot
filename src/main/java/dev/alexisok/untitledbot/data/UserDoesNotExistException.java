package dev.alexisok.untitledbot.data;

/**
 * 
 * Thrown if a user is not found in the {@link UserData} class.
 * 
 * @author AlexIsOK
 * @since 0.0.1
 */
public final class UserDoesNotExistException extends RuntimeException{
	public UserDoesNotExistException(String s){
		super(s);
	}
}
