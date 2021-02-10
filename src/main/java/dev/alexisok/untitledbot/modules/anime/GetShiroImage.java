package dev.alexisok.untitledbot.modules.anime;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * Get an image from shiro.gg
 */
public final class GetShiroImage {
    
    /**
     * Get the full URL from an endpoint
     * 
     * @param endpoint the endpoint to get with a leading slash.
     * @return the full URL to be returned to Discord.
     */
    @NotNull
    @Contract(pure = true)
    public static String get(String endpoint) {
        if(!endpoint.startsWith("/"))
            endpoint = "/" + endpoint;
        
        String builtURL = "https://shiro.gg/api/images" + endpoint;
        
        try {
            
            //read the string into memory
            BufferedReader br = new BufferedReader(new InputStreamReader(new URL(builtURL).openStream()));
            String rs = new JSONObject(br.readLine()).getString("url");
            
            //if the result is null, display the error message here.
            if(rs == null)
                throw new Exception();
            
            return rs;
        } catch(Throwable t) {
            t.printStackTrace();
            return "https://raw.githubusercontent.com/untitledbot/untitled-bot/master/ohno.png";
        }
        
    }
    
}
