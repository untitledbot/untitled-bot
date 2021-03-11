package dev.alexisok.untitledbot.modules.dash.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dev.alexisok.untitledbot.logging.Logger;
import dev.alexisok.untitledbot.modules.dash.DashHost;
import dev.alexisok.untitledbot.modules.dash.util.Cookies;
import org.intellij.lang.annotations.Language;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;

/**
 * Handles the /logout endpoint.
 * 
 * Deletes a token and removes it from the cache
 * 
 */
public class LogoutHandler implements HttpHandler {
    
    @Language("HTML")
    private static final String MAIN_PAGE_REDIRECT = "<html><head><script>window.location.href='https://untitled-bot.xyz/'</script><meta http-equiv='refresh' content='0; url=https://untitled-bot.xyz/'></head><body></body></html>";
    
    @Override
    public void handle(HttpExchange e) throws IOException {
        try {
            Logger.log("Logging out a user");
            long userID = Cookies.getSessionID(e).getIdLong();
            DashHost.invalidateToken(Cookies.getSessionID(e).getIdLong(), DashHost.getOAuthData(userID).getSessionToken());
            Logger.log(userID + " has been logged out");
        } catch(Exception ignored) {
            Logger.critical("Could not log out a user!");
        }
        e.sendResponseHeaders(200, MAIN_PAGE_REDIRECT.length());
        e.getResponseBody().write(MAIN_PAGE_REDIRECT.getBytes(StandardCharsets.UTF_8));
        e.getResponseBody().flush();
        e.getResponseBody().close();
    }
}
