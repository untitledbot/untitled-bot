package dev.alexisok.untitledbot.dashboard.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dev.alexisok.untitledbot.logging.Logger;

import java.io.IOException;
import java.util.List;

public class RootPageHandler implements HttpHandler {
    
    @Override
    public void handle(HttpExchange e) throws IOException {
        Logger.log("Connection");
        List<String> header = e.getRequestHeaders().get("Cookie");
        
        header.forEach(h -> {
            System.out.printf("h: %s%n", h);
        });
        
        e.sendResponseHeaders(200, 0);
        e.close();
    }
}
