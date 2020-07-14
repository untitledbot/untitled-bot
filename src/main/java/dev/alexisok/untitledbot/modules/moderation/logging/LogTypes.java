package dev.alexisok.untitledbot.modules.moderation.logging;

/**
 * This defines log types to be used by {@link AddRemoveLogTypes}.
 * 
 * @author AlexIsOK
 * @since 0.0.1
 */
public enum LogTypes {
    
    USER_NICKNAME_UPDATE, USER_ROLE_UPDATE,
    
    VOICE_CHANNEL_JOIN, VOICE_CHANNEL_LEAVE, VOICE_CHANNEL_SWITCH,
    
    ROLE_ADD, ROLE_UPDATE, ROLE_DELETE
    
}
