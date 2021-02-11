package dev.alexisok.untitledbot.modules.dash;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

public final class DashHost {
    
    static void init() throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", 8080), 0);
        server.createContext("/api", new APIRequestHandler());
        server.createContext("/", new MainPageHandler());
        server.start();
    }
    
}
