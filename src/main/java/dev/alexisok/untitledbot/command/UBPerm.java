package dev.alexisok.untitledbot.command;

/**
 * Describes untitled-bot permissions.
 * 
 * I was going to use JDA's permissions, but I needed some (such as bot owner) which is not there.
 * 
 * @author AlexIsOK
 * @since 1.3.25
 */
public enum UBPerm {
    OWNER("Bot Owner"), ADMIN("Administrator"), MANAGE_SERVER("Manage Server"), MANAGE_MESSAGES("Manage Messages"), EVERYONE("Global");
    
    public final String niceName;
    
    UBPerm(String s) {
        this.niceName = s;
    }
}
