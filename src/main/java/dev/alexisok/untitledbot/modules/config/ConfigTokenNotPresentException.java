package dev.alexisok.untitledbot.modules.config;

/**
 * Thrown by {@link ConfigTokens} if there is a value present
 * for ConfigTokens#TOKENS but no value present for ConfigTokens#LEGAL_VALUES.
 * 
 * 
 * @author AlexIsOK
 * @since 1.3.21
 */
public final class ConfigTokenNotPresentException extends RuntimeException {}
