package dev.alexisok.untitledbot.hook;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import dev.alexisok.untitledbot.BotClass;
import dev.alexisok.untitledbot.logging.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Properties;

/**
 * @author AlexIsOK
 * @since 1.3.25
 */
public final class Webhook implements HttpHandler {

    //the total amount of votes passed through this
    private static int totalVotes = 0;

    //authentication token sent by top.gg so we know it's real
    private static final String AUTH;

    //the port to run the server on.
    private static final int PORT = 21626;

    static {
        Properties p = new Properties();
        try {
            p.load(new FileReader("./secrets.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        AUTH = p.getProperty("webhook.auth");
    }

    /**
     * Get the total votes that have gone through the webhook.
     * @return the total votes.
     */
    public static int getTotalVotes() {
        return totalVotes;
    }

    /**
     * Start the HTTP server.
     *
     * This is an HTTP server because that is the protocol top.gg is using.
     *
     */
    protected static void startServer() {
        Logger.log("Starting webhook server...");
        try {
            HttpServer serv = HttpServer.create(new InetSocketAddress(PORT), 0);
            serv.createContext("/", new Webhook());
            serv.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Handle the incoming HTTP request.
     * @param e the {@link HttpExchange} that is received.
     * @throws IOException if there is an {@link IOException}.
     */
    @Override
    public void handle(HttpExchange e) throws IOException {
        Logger.log("Incoming request from " + e.getRemoteAddress().getHostName());

        try {
            //get the auth sent by top.gg
            String auth = e.getRequestHeaders().getFirst("Authorization");

            //if the authentication doesn't match, it's a fake request.  best to log it here.
            if(!auth.equals(AUTH)) {
                e.sendResponseHeaders(400, 0);
                Logger.critical("An illegal webhook access has occurred!  From " + e.getRemoteAddress().toString() + " with auth " + auth);
                e.close();
                return;
            }
            //200 OK
            e.sendResponseHeaders(200, 0);

            //read all the bytes that the client sends (DBL) into a string.
            StringBuilder s = new StringBuilder();
            for(byte b : e.getRequestBody().readAllBytes()) {
                s.append((char) b);
            }

            //for debugging
            Logger.log("Reading " + s.toString());

            //since the bytes are in JSON, the reply should be obtained here.
            JSONObject j = (JSONObject) new JSONParser().parse(s.toString());
            String votedUser = (String) j.get("user");

            //the user that voted for the bot.
            Logger.log("user: " + votedUser);

            e.close();

            //run the vote stuff and increment the counter.
            BotClass.onVote(Long.parseLong(votedUser));
            totalVotes++;

        } catch(IndexOutOfBoundsException | ParseException ignored) {}
        e.close();

    }

}
