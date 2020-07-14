package dev.alexisok.untitledbot.modules.moderation.logging;

/**
 * This defines log types to be used by {@link AddRemoveLogTypes}.
 * 
 * @author AlexIsOK
 * @since 0.0.1
 */
public class LogTypes {
    
    public static final long MESSAGE_DELETE, MESSAGE_EDIT, MESSAGE_UPDATE_GENERIC,
    
    USER_AVATAR_UPDATE, USER_TAG_UPDATE, USER_NAME_UPDATE, USER_NICKNAME_UPDATE, USER_ROLE_UPDATE, USER_UPDATE_GENERIC,
    USER_JOIN, USER_LEAVE, USER_BANNED, USER_UNBANNED, USER_MUTED, USER_UNMUTED,
    
    VOICE_CHANNEL_JOIN, VOICE_CHANNEL_LEAVE, VOICE_CHANNEL_SWITCH,
    
    SERVER_MODIFY_GENERIC,
    
    EMOJI_ADD, EMOJI_UPDATE, EMOJI_DELETE,
    
    ROLE_ADD, ROLE_UPDATE, ROLE_DELETE,
    
    CHANNEL_CREATE, CHANNEL_UPDATE, CHANNEL_DELETE;
    
    static {
        MESSAGE_DELETE         =         1L;
        MESSAGE_EDIT           =         2L;
        MESSAGE_UPDATE_GENERIC =         4L;
        USER_AVATAR_UPDATE     =         8L;
        USER_TAG_UPDATE        =        16L;
        USER_NAME_UPDATE       =        32L;
        USER_NICKNAME_UPDATE   =        64L;
        USER_ROLE_UPDATE       =       128L;
        USER_UPDATE_GENERIC    =       256L;
        USER_JOIN              =       512L;
        USER_LEAVE             =      1024L;
        USER_BANNED            =      2048L;
        USER_UNBANNED          =      4096L;
        USER_MUTED             =      8192L;
        USER_UNMUTED           =     16384L;
        VOICE_CHANNEL_JOIN     =     32768L;
        VOICE_CHANNEL_LEAVE    =     65536L;
        VOICE_CHANNEL_SWITCH   =    131072L;
        SERVER_MODIFY_GENERIC  =    262144L;
        EMOJI_ADD              =    524288L;
        EMOJI_UPDATE           =   1048576L;
        EMOJI_DELETE           =   2097152L;
        ROLE_ADD               =   4194304L;
        ROLE_UPDATE            =   8388608L;
        ROLE_DELETE            =  16777216L;
        CHANNEL_CREATE         =  33554432L;
        CHANNEL_UPDATE         =  67108864L;
        CHANNEL_DELETE         = 134217728L;
    }
    
}
