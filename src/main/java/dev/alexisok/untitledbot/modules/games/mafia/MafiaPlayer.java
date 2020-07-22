package dev.alexisok.untitledbot.modules.games.mafia;

import net.dv8tion.jda.api.entities.User;

/**
 * Contains information about players
 * 
 * @author AlexIsOK
 * @since 1.3
 */
final class MafiaPlayer {
    
    private final User PLAYER;
//    privat
    
    protected MafiaPlayer(User u) {
        this.PLAYER = u;
    }
    
}
