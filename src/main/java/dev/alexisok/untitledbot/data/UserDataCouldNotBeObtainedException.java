package dev.alexisok.untitledbot.data;

import dev.alexisok.untitledbot.modules.vault.Vault;

/**
 * Thrown if user data could not be obtained.
 * Thrown by some methods in {@link Vault}
 * 
 * @author AlexIsOK
 * @since 0.0.1
 */
public final class UserDataCouldNotBeObtainedException extends RuntimeException {
	public UserDataCouldNotBeObtainedException() {super();}
}
