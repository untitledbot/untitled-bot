package dev.alexisok.untitledbot.modules.games.mafia;

import static dev.alexisok.untitledbot.modules.games.mafia.Alliance.*;

/**
 * Roles for mafia
 * 
 * @author AlexIsOK
 * @since 1.3
 */
enum MafiaRole {;
    
//    DETECTIVE("Detective", new Alliance[] {COMMON, TOWN}),
//    SPY("Spy", new Alliance[] {COMMON, TOWN}),
//    SHERIFF("Sheriff", new Alliance[] {COMMON, TOWN}),
//    VIGILANTE("Vigilante", new Alliance[] {COMMON, TOWN}),
//    VETERAN("Veteran", new Alliance[] {COMMON, TOWN}),
//    DOCTOR("Doctor", new Alliance[] {COMMON, TOWN}),
//    BODYGUARD("Bodyguard", new Alliance[] {COMMON, TOWN}),
//    GODFATHER("Godfather", new Alliance[] {MAFIA, EVIL}),
//    MAFIOSO("Mafioso", new Alliance[] {MAFIA}),
//    MAYOR("Mayor", new Alliance[] {COMMON, TOWN}),
//    JANITOR("Janitor", new Alliance[] {MAFIA}),
//    WEREWOLF_LEADER("Werewolf (adult)", new Alliance[] {SUPERNATURAL, EVIL}),
//    WEREWOLF_TEEN("Werewolf (young)", new Alliance[] {}),
//    VAMPIRE_LEADER("Vampire (adult)", new Alliance[] {}),
//    VAMPIRE_TEEN("Vampire (young)", new Alliance[] {}),
//    DENTIST("Dentist", new Alliance[] {MAFIA}),
//    ARSONIST("Arsonist", new Alliance[] {EVIL, SUPERNATURAL, MAFIA}),
//    COMMONER("Commoner", new Alliance[] {COMMON, TOWN, MAFIA}),
//    DRUNK("Drunk", new Alliance[] {COMMON, TOWN, MAFIA}),
//    EXECUTIONER("Executioner", new Alliance[] {EVIL, MAFIA, SUPERNATURAL});
    
    private final String NAME;
    
    //1: normal
    //2: mafia immune
    //3: town immune
    //4: vampire and werewolf immune
    //always able to be lynched
    private final int DEFENSE;
    
    //1: 
    private final int ATTACK;
    
    private final Alliance[] ALLIANCES;
    
    MafiaRole(String name, Alliance[] alliances, int defense, int attack) {
        this.NAME = name;
        this.ALLIANCES = alliances.clone();
        this.DEFENSE = defense;
        this.ATTACK = attack;
    }
}
