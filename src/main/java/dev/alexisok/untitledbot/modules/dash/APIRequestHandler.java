package dev.alexisok.untitledbot.modules.dash;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dev.alexisok.untitledbot.logging.Logger;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

/**
 * Handles all /api requests.
 */
public final class APIRequestHandler implements HttpHandler {
    
    @Override
    public void handle(HttpExchange e) throws IOException {
        Logger.log("API request from " + e.getRemoteAddress().toString());
        
        if(e.getRequestHeaders().get("Authorization") == null) {
            e.sendResponseHeaders(401, "Unauthorized".length());
            OutputStream os = e.getResponseBody();
            os.write("Unauthorized".getBytes(StandardCharsets.UTF_8));
            os.flush();
            os.close();
        }
        
    }
}
