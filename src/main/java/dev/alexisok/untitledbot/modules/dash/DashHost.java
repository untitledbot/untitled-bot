package dev.alexisok.untitledbot.modules.dash;

import com.sun.net.httpserver.HttpServer;
import dev.alexisok.untitledbot.modules.dash.handlers.*;
import net.dv8tion.jda.api.entities.User;

import java.io.FileReader;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Properties;

public final class DashHost {
    
    //                 userID, login data
    static final HashMap<Long, OAuthData> SESSIONS = new HashMap<>();
    
    //              sessionID, userID
    static final HashMap<Long, Long> SESSION_MAP = new HashMap<>();
    
    static final String CLIENT_SECRET;
    
    static {
        Properties p = new Properties();
        
        String s = "";
        try {
            p.load(new FileReader("./secrets.properties"));
            s = p.getProperty("client-secret");
            if(s.isBlank())
                throw new Exception();
        } catch(Exception e) {
            e.printStackTrace();
        }
        CLIENT_SECRET = s;
    }
    
    public static OAuthData getOAuthData(long userID) {
        return SESSIONS.get(userID);
    }
    
    /**
     * Get the user ID from the session token.
     * @param session the sessionID
     * @return the userID, or {@code -1} if they are non-existent.
     */
    public static long getIDFromSession(long session) {
        return SESSION_MAP.getOrDefault(session, -1L);
    }
    
    static void init() throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", 8080), 2);
        server.createContext("/api", new APIRequestHandler());
        server.createContext("/logout", new LogoutHandler());
        server.createContext("/servers", new ServerSelectPage());
        server.createContext("/pub", new PubHandler());
        server.createContext("/statistics", new StatisticsHandler());
        server.createContext("/music", new MusicHandler());
        server.createContext("/", new MainPageHandler());
        server.start();
    }
    
    public static void invalidateToken(long userID, long sessionID) {
        SESSION_MAP.remove(sessionID);
        SESSIONS.remove(userID);
    }
}
