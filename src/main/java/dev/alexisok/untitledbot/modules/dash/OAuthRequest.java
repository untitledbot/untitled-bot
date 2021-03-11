package dev.alexisok.untitledbot.modules.dash;

import dev.alexisok.untitledbot.logging.Logger;
import netscape.javascript.JSObject;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

public final class OAuthRequest {
    
    private OAuthRequest() {}
    
    private static final String BASE_URL = "https://discord.com/api";

    /**
     * Get the bearer's master ID
     * @param bearer the bearer token
     * @return the ID of the master
     * @throws IOException if there is an error reading the stream.
     */
    public static long getUserID(@NotNull String bearer) throws IOException {
        URL u = new URL(BASE_URL + "/users/@me");
        
        HttpsURLConnection c = (HttpsURLConnection) u.openConnection();
        
        c.setRequestMethod("GET");
        c.setRequestProperty("Accept", "application/json");
        c.setRequestProperty("Authorization", "Bearer " + bearer);
        c.setDoInput(true);
        c.setDoOutput(true);
        
        String result;
        long userID;
        
        try(InputStream stream = c.getInputStream()) {
            result = new String(stream.readAllBytes());
            Logger.log(result);
            userID = Long.parseLong(new JSONObject(result).getString("id"));
        }
        return userID;
    }
    
}
