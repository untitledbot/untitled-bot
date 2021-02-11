package dev.alexisok.untitledbot.modules.dash;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dev.alexisok.untitledbot.logging.Logger;

/**
 * Handles all /api requests.
 */
public final class APIRequestHandler implements HttpHandler {
    
    @Override
    public void handle(HttpExchange e) {
        Logger.log("API request from " + e.getRemoteAddress().toString());
        
        
    }
}
