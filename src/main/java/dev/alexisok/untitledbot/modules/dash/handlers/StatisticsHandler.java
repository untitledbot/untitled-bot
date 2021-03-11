package dev.alexisok.untitledbot.modules.dash.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dev.alexisok.untitledbot.logging.Logger;
import dev.alexisok.untitledbot.modules.basic.status.Status;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Random;

/**
 * Handles the /statistics endpoint
 */
public class StatisticsHandler implements HttpHandler {
    
    @Override
    public void handle(HttpExchange e) throws IOException {
        Logger.log("Getting the statistics");
        try {
            String stats = getStats(Status.getStats(null));
            e.sendResponseHeaders(200, stats.length());
            OutputStream os = e.getResponseBody();
            os.write(stats.getBytes(StandardCharsets.UTF_8));
            os.flush();
            os.close();
        } catch(Exception e2) {e2.printStackTrace();}
    }
    
    private static String getStats(String message) {
        return String.format("" +
                "<!DOCTYPE HTML>" +
                "<html>" +
                "<head>" +
                "<meta charset='UTF-8'>" +
                "<style>" +
                "body{background-color:black;}p,h1{color:white;}" +
                "</style>" +
                "</head>" +
                "<body>" +
                "<h1>Statistics for untitled-bot</h1>" +
                "<p>%s</p>" +
                "</body>" +
                "</html>", message.replace("\n", "<br>").replaceFirst("```", "<code>").replaceFirst("```", "</code>"));
    }
}
