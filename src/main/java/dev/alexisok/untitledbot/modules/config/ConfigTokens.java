package dev.alexisok.untitledbot.modules.config;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.CheckReturnValue;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Properties;

/**
 * Contains tokens for the easy configuration.
 * The tokens are stored as a {@link HashMap} and
 * only use the assigned map value when there is none provided for the command.
 * 
 * This is not for user configuration, it is only for server administrators
 * to use when they are configuring the server so they don't have to enter
 * a lot of different commands.
 * 
 * @author AlexIsOK
 * @since 1.3.21
 */
public final class ConfigTokens {
    
    private ConfigTokens(){}
    
    public static final String      COLON = "&#58";
    public static final String SEMI_COLON = "&#59";
    
    
    /**
     * TOKEN: Key, Default Value
     * 
     * The default value will only be used if it was not specified
     * by the command.
     * 
     * Note that characters such as {@code :} and {@code ;} will have to be escaped using
     * {@code &#58} for colon and {@code &#59} for semi-colon.
     */
    private static final HashMap<String, String> TOKENS = new HashMap<>();
    
    /**
     * KEYS: key, value
     * 
     * The key is the key thing used for the other two hashmaps but the
     * value for this is the vault key that is used to store data.
     */
    private static final HashMap<String, String> VAULT_KEYS = new HashMap<>();
    
    /**
     * Legal values for tokens.
     * 
     * For any value, use a single-element String array containing "ANY"
     * 
     * For regexp, use a double-element String array, the first containing "REGEX" and
     * the second containing the regexp to match.
     * 
     * Special values above are case-sensitive.
     * 
     */
    private static final HashMap<String, String[]> LEGAL_VALUES = new HashMap<>();
    
    /**
     * Transformation before the value is put in the vault and before it is checked against legal values.
     * 
     * Example, for the value, do String[]{"&#58", ":"} to replace anything matching "&#58" with ":".  Value must
     * be a non-zero string array with an even size.
     * 
     * This is optional.
     */
    private static final HashMap<String, String[]> TRANSFORM = new HashMap<>();
    
    private static void addValue(String key, String valueDefault, String vaultKey, String... legalValues) {
        TOKENS.put(key, valueDefault);
        LEGAL_VALUES.put(key, legalValues);
        VAULT_KEYS.put(key, vaultKey);
    }
    
    static {
        addValue("announceXPBoost", "true", "ranks-broadcast.boost", "true", "false");
        addValue("announceLevelUp", "current", "ranks-broadcast.rankup", "current", "none");
        addValue("workMin", "100", "work.limit.minimum", "REGEX", "[0-9]{1,4}");
        addValue("workMax", "500", "work.limit.maximum", "REGEX", "[0-9]{1,4}");
        addValue("workTimeout", "86400", "work.cooldown", "REGEX", "[0-9]{2,5}");
        addValue("prefix", ">", "guild.prefix", "REGEX", ".{1,5}");
        addValue("stealMin", "50", "steal.limit.minimum", "REGEX", "[0-9]{1,5}");
        addValue("stealMax", "300", "steal.limit.maximum", "REGEX", "[0-9]{1,5}");
        addValue("stealChance", "50", "steal.chance", "REGEX", "[0-9]{1,2}");
        addValue("stealTimeout", "86400", "steal.cooldown", "REGEX", "[0-9]{2,5}");
        TRANSFORM.put("prefix", new String[] {COLON, ":", SEMI_COLON, ";"});
    }
    
    /**
     * Get the {@link HashMap} of tokens.
     * @return the map.
     */
    @NotNull
    @Contract(pure = true)
    public static HashMap<String, String> getTokens() {
        return new HashMap<>(TOKENS);
    }
    
    /**
     * Get the {@link HashMap} of legal values.
     * @return the map.
     */
    @NotNull
    @Contract(pure = true)
    public static HashMap<String, String[]> getLegalValues() {
        return new HashMap<>(LEGAL_VALUES);
    }
    
    @NotNull
    @Contract(pure = true)
    public static HashMap<String, String[]> getTransform() {
        return new HashMap<>(TRANSFORM);
    }
    /**
     * Checks to see if a token is legal or not.
     * 
     * Please see the other fields in this file for documentation.  If you can't see them,
     * compile the JavaDoc with private level access.
     * 
     * @param token the token to check.
     * @param value the value to check.
     * @return {@code false} if the token is not in the map or the token is not valid, {@code true} if it is valid.
     * @throws ConfigTokenNotPresentException if there is a value for {@link #TOKENS} but no value for {@link #LEGAL_VALUES}.
     * @throws ArrayIndexOutOfBoundsException if the TRANSFORM element has errors.
     * @see ConfigTokens#LEGAL_VALUES
     * @see ConfigTokens#TOKENS
     * @see ConfigTokenNotPresentException
     */
    @CheckReturnValue
    @Contract(pure = true)
    protected static boolean isLegal(@NotNull String token, @NotNull String value) throws ConfigTokenNotPresentException, ArrayIndexOutOfBoundsException {
        if(!TOKENS.containsKey(token))
            return false;
        if(!LEGAL_VALUES.containsKey(token))
            throw new ConfigTokenNotPresentException();
        
        if(TRANSFORM.containsKey(token)) {
            String[] get = TRANSFORM.get(token);
            for(int i = 0; i < get.length; i += 2) {
                value = value.replace(get[i], get[i + 1]);
            }
        }
        
        String[] legalValues = LEGAL_VALUES.get(token);
        
        if(legalValues[0].equals("ANY"))
            return true;
        if(legalValues[0].equals("REGEX"))
            return value.matches(legalValues[1]);
    
        return Arrays.asList(legalValues).contains(value);
    }
    
    /**
     * Get the vault key of a specified token.
     * @param key the token.
     * @return the vault key, or the default value.
     */
    @NotNull
    @Contract(pure = true)
    public static String getVaultKey(String key) {
        return VAULT_KEYS.get(key);
    }
    
}
