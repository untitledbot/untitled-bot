package dev.alexisok.untitledbot.util.hook;

/**
 * Run when a user votes for the bot on top.gg
 * 
 * @author AlexIsOK
 * @since 1.0
 */
public interface VoteHook {
    
    void onVote(long userID);
    
}
