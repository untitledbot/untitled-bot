package dev.alexisok.untitledbot.modules.dash;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dev.alexisok.untitledbot.logging.Logger;

import java.io.IOException;

/**
 * Handles all / endpoints besides /api
 */
public class MainPageHandler implements HttpHandler {
    
    @Override
    public void handle(HttpExchange e) throws IOException {
        Logger.log(e.getRemoteAddress().toString());
        Logger.log(e.getRequestURI().toString());
        
        if(e.getRequestURI().toString().startsWith("/?code=")) {
            Logger.log("Incoming code request.");
            String token = e.getRequestURI().toString().substring(7);
            
            
        }
    }
    
}
