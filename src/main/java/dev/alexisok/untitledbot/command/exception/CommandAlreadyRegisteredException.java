package dev.alexisok.untitledbot.command.exception;

import dev.alexisok.untitledbot.command.CommandRegistrar;

/**
 * Thrown if a command is already registered in {@link CommandRegistrar}
 * 
 * @author AlexIsOK
 * @since 0.0.1
 */
public final class CommandAlreadyRegisteredException extends RuntimeException {
    public CommandAlreadyRegisteredException() {}
}
