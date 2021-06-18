package dev.alexisok.untitledbot.dashboard;

import com.sun.net.httpserver.HttpServer;
import dev.alexisok.untitledbot.dashboard.handlers.RootPageHandler;
import lombok.SneakyThrows;

import java.net.InetSocketAddress;

/**
 * Serves dashboard content
 */
public final class DashboardServer {
    
    private DashboardServer() {}
    
    @SneakyThrows
    protected static void init() {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/", new RootPageHandler());
        server.start();
    }

    public static void main(String[] args) {
        init();
    }
}
