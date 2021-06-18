package dev.alexisok.untitledbot.dashboard.util;

import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Cookie utilities for dashboard
 */
public final class CookieUtils {
    
    private static final Map<Long, Session> USERS = new HashMap<>();
    
    @Contract(pure = true)
    private static long getSessionID(@NotNull String cookie) {
        return 0; //TODO
    }
    
    /**
     * Get a User from a specific cookie.
     * @param cookie the cookies.
     * @return the User, or {@code null} if there is no user.
     */
    @Nullable
    @Contract(pure = true)
    public static User getUserFromCookie(@NotNull String cookie) {
        Session s = USERS.get(getSessionID(cookie));
        
        if(s == null)
            return null;
        
        //remove if expired
        if(s.getValidUntil() < System.currentTimeMillis()) {
            USERS.remove(s.getSessionID());
            return null;
        }
        
        return s.getUser();
    }
    
    /**
     * Generate a new session cookie for the user.
     * @param user the user to generate the cookie for.
     * @return the generated session.
     */
    public static long generateNewCookie(@NotNull User user) {
        long random = new Random().nextLong();
        USERS.put(random, new Session(random, user, System.currentTimeMillis() + 259200000L)); //3 days
        return random;
    }
    
    /**
     * Logout a user.
     * @param cookie the cookies.
     */
    public static void logoutUser(@NotNull String cookie) {
        long sid = getSessionID(cookie);
        USERS.remove(sid);
    }
    
}
