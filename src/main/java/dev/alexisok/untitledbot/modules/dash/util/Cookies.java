package dev.alexisok.untitledbot.modules.dash.util;

import com.sun.net.httpserver.HttpExchange;
import dev.alexisok.untitledbot.logging.Logger;
import dev.alexisok.untitledbot.modules.dash.DashHost;
import dev.alexisok.untitledbot.modules.dash.OAuthData;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

/**
 * Contains cookie utilities
 */
public final class Cookies {

    /**
     * Get the User from a session ID
     * @return the {@link net.dv8tion.jda.api.entities.User}, or {@code null} if they are not logged in.
     */
    @Nullable
    @Contract(pure = true)
    public static User getSessionID(HttpExchange e) {
        try {
            String cookie = e.getRequestHeaders().get("Cookie").get(0);
            String[] args = cookie.split("; ");
            String session = null;
            for(String a : args) {
                String[] q = a.split("=");
                if (q[0].equals("session"))
                    session = q[1];
            }
            if(session == null)
                return null;
            Logger.log("User has session " + session);
            long userID = DashHost.getIDFromSession(Long.parseLong(session));
            
            Logger.log("User ID: " + userID);
            
            OAuthData data = DashHost.getOAuthData(userID);
            
//            if(!e.getRemoteAddress().toString().equals(data.getIPAddress())) {
//                Logger.log(String.format("AN ILLEGAL ACCESS ERROR happened when comparing ips %s and %s", data.getIPAddress(), e.getRemoteAddress().toString()));
//                return null;
//            }
            
            return data.getUser();
        } catch(Exception ignored) {
            return null;
        }
    }
    
}
