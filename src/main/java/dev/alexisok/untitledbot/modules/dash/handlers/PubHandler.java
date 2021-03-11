package dev.alexisok.untitledbot.modules.dash.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public final class PubHandler implements HttpHandler {
    
    private static final String DASHBOARD_CSS;
    
    static {
        String s = "";
        try {
            s = new String(Files.readAllBytes(new File("./pub/dash/dashboardPage.css").toPath()));
        } catch(IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        DASHBOARD_CSS = s;
    }
    
    @Override
    public void handle(HttpExchange e) throws IOException {
        if(e.getRequestURI().toString().equals("/pub/dashboardPage.css")) {
            e.sendResponseHeaders(200, DASHBOARD_CSS.length());
            OutputStream os = e.getResponseBody();
            os.write(DASHBOARD_CSS.getBytes(StandardCharsets.UTF_8));
            os.flush();
            os.close();
            e.close();
        }
    }
}
