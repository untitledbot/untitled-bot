package dev.alexisok.untitledbot.modules.dash;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dev.alexisok.untitledbot.Main;
import dev.alexisok.untitledbot.logging.Logger;
import dev.alexisok.untitledbot.modules.dash.util.Cookies;
import net.dv8tion.jda.api.entities.User;
import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Handles root (/) requests
 */
public class MainPageHandler implements HttpHandler {
    
    @Language("HTML")
    private static final String ON_GOOD_CODE = "<!DOCTYPE HTML><html><head></head></html>";
    
    @Language("HTML")
    private static final String COOKIE_BANNER =
            "<style>" +
            ".header-card {" +
            "position: fixed;" +
            "bottom: 0;" +
            "left: 0;" +
            "right: 0;" +
            "background: rgba(0, 0, 0, 0.5);" +
            "color: white;" +
            "}</style><div class='header-card'><h1>This site uses cookies to keep you logged in.  We do not collect/sell your data or track you.</h1></div>";
    
    private static final byte[] FAVICON;
    
    static {
        byte[] g = new byte[0];
        try {
            g = Files.readAllBytes(new File("favicon.ico").toPath());
        } catch(Throwable t) {
            t.printStackTrace();
            System.exit(1);
        }
        FAVICON = g;
    }
    
    @Override
    public void handle(HttpExchange e) throws IOException {
        Logger.log(e.getRemoteAddress().toString());
        Logger.log(e.getRequestURI().toString());
        
        if(e.getRequestURI().toString().equals("/favicon.ico")) {
            e.sendResponseHeaders(200, FAVICON.length);
            OutputStream os = e.getResponseBody();
            os.write(FAVICON);
            os.flush();
            os.close();
            e.close();
            return;
        }
        
        try {
            if(e.getRequestURI().toString().startsWith("/?code=")) {
                Logger.log("Incoming code request.");
                String token = e.getRequestURI().toString().substring(7);
                
                String data = String.format("client_id=%s&client_secret=%s&grant_type=authorization_code&code=%s&redirect_uri=%s&scope=identify+guilds",
                        "769780021120073748",
                        DashHost.CLIENT_SECRET,
                        token,
                        "http%3a%2f%2f127.0.0.1%3a8080");

                URL u = new URL("https://discord.com/api/oauth2/token");

                HttpsURLConnection c = (HttpsURLConnection) u.openConnection();

                c.setRequestMethod("POST");
                c.setRequestProperty("Accept", "application/json");
                c.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                c.setDoInput(true);
                c.setDoOutput(true);

                try (OutputStream os = c.getOutputStream(); OutputStreamWriter osw = new OutputStreamWriter(os, StandardCharsets.UTF_8)) {
                    osw.write(data);
                    osw.flush();
                }

                try (InputStream is = c.getInputStream()) {
                    byte[] stream = is.readAllBytes();
                    JSONObject j = new JSONObject(new String(stream));

                    //401 if the user does not have the proper scopes
                    if (!j.getString("scope").equals("identify guilds")) {
                        fourZeroOne(e);
                        return;
                    }
                    
                    //bearer token
                    String bearer = j.getString("access_token");
                    
                    User user = Main.getUserById(OAuthRequest.getUserID(bearer));
                    
                    if(user == null) {
                        fourZeroOne(e);
                        return;
                    }
                    
                    OAuthData oa = new OAuthData(bearer, j.getString("scope"), ThreadLocalRandom.current().nextLong(), user, e.getRemoteAddress().getAddress().toString());
                    DashHost.SESSIONS.put(user.getIdLong(), oa);
                    DashHost.SESSION_MAP.put(oa.getSessionToken(), user.getIdLong());
                    
                    String response = (String.format("<!DOCTYPE html><html><script>document.cookie='session=%s';window.location='/servers'</script><body></body></html>", oa.getSessionToken()));
                    e.sendResponseHeaders(200, response.length());
                    OutputStream os = e.getResponseBody();
                    os.write(response.getBytes(StandardCharsets.UTF_8));
                    os.flush();
                    os.close();
                }
            } else if(Cookies.getSessionID(e) != null) {
                StringBuilder response = new StringBuilder();
                response.append("<!DOCTYPE html><html><script>window.location='/music'</script><body></body></html>");
                e.sendResponseHeaders(200, response.toString().length());
                OutputStream os = e.getResponseBody();
                os.write(response.toString().getBytes(StandardCharsets.UTF_8));
                os.flush();
                os.close();
            } else {
                //
                StringBuilder response = new StringBuilder();
                response.append("<!DOCTYPE html><html><script>window.location='https://discord.com/oauth2/authorize?client_id=769780021120073748&redirect_uri=http%3A%2F%2F127.0.0.1%3A8080&response_type=code&scope=identify+guilds'</script><body></body></html>");
                e.sendResponseHeaders(200, response.toString().length());
                OutputStream os = e.getResponseBody();
                os.write(response.toString().getBytes(StandardCharsets.UTF_8));
                os.flush();
                os.close();
            }
        } catch(Exception e3) {
            e3.printStackTrace();
            String response = ErrorHandler.serverError();
            e.sendResponseHeaders(500, response.length());
            OutputStream clientOS = e.getResponseBody();
            clientOS.write(response.getBytes(StandardCharsets.UTF_8));
            clientOS.flush();
            clientOS.close();
        } finally {
            e.close();
        }
    }

    /**
     * 401 error convenience method
     * @param e the exchange
     * @throws IOException if there is an error
     */
    private static void fourZeroOne(@NotNull HttpExchange e) throws IOException {
        String response = ErrorHandler.unauthorized();
        e.sendResponseHeaders(401, response.length());
        OutputStream clientOS = e.getResponseBody();
        clientOS.write(response.getBytes(StandardCharsets.UTF_8));
        clientOS.flush();
        clientOS.close();
    }
    
}
