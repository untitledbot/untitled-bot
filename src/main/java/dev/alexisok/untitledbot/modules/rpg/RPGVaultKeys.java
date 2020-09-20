package dev.alexisok.untitledbot.modules.rpg;

/**
 * Contains vault keys.
 * 
 * where is the @Data annotation i need the dependency
 * 
 * @author AlexIsOK
 * @since 1.4
 */
public final class RPGVaultKeys {
    private RPGVaultKeys(){}
    public static final String STARTED         = "rpg.started";
    public static final String LEVEL           = "rpg.stat.level";
    public static final String XP              = "rpg.stat.xp";
    public static final String POW             = "rpg.stat.pow";
    public static final String DEF             = "rpg.stat.def";
    public static final String HEALTH_CURRENT  = "rpg.stat.hp.current";
    public static final String HEALTH_MAXIMUM  = "rpg.stat.hp.max";
    public static final String GOLD            = "rpg.stat.gold";
    public static final String SILVER          = "rpg.stat.silver";
    public static final String IN_BATTLE       = "rpg.battle"; //true/false
    public static final String BATTLE_TURN     = "rpg.battle.isMyTurn"; //is it the turn of the person whose data was obtained true/false
    public static final String BATTLE_POTION   = "rpg.battle.potion"; //you can only have one non-instant potion active at a time. (`none` for none)
    public static final String POTION_MODIFIER = "rpg.battle.potion.modifier";
    public static final String CLASS           = "rpg.class";
}
