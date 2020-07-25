package dev.alexisok.untitledbot.modules.games.mafia;

import net.dv8tion.jda.api.entities.User;

import java.util.ArrayList;

/**
 * Hosts information about a mafia game.
 * 
 * @author AlexIsOK
 * @since 1.3
 */
final class MafiaGame {
    
    private ArrayList<MafiaPlayer> playersTotal = new ArrayList<>();
    private int day;
    
    MafiaGame() {
        
    }
    
}
