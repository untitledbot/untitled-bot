package dev.alexisok.untitledbot.modules.dash.handlers;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dev.alexisok.untitledbot.Main;
import dev.alexisok.untitledbot.logging.Logger;
import dev.alexisok.untitledbot.modules.dash.builder.ServerListBuilder;
import dev.alexisok.untitledbot.modules.dash.util.Cookies;
import dev.alexisok.untitledbot.modules.music.MusicKernel;
import dev.alexisok.untitledbot.modules.music.NowPlaying;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import org.apache.commons.lang.StringEscapeUtils;
import org.intellij.lang.annotations.Language;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * Handles the /music endpoint
 */
public final class MusicHandler implements HttpHandler {
    
    private static final String DEFAULT_AVATAR_URL = "https://cdn.discordapp.com/embed/avatars/1.png";
    
    @Language("HTML")
    private static final String NOT_FOUND = "<!DOCTYPE HTML><html><body>error: could not find the guild requested.  please tell me on <a href='https://discord.alexisok.dev'>the discord server</a> if you think this is an error.</body></html>";
    
    private static final String NOT_PLAYING = "Doesn't look like anything is playing right now.";
    
    @Language("HTML")
    protected static final String AUTH_REDIRECT = "<!DOCTYPE HTML><html><head><meta charset='UTF-8'><script>window.location='/'</script></head></html>";
    
    @Override
    public void handle(HttpExchange e) throws IOException {
        User u = Cookies.getSessionID(e);
        
        if(u == null) {
            e.sendResponseHeaders(200, AUTH_REDIRECT.length());
            OutputStream os = e.getResponseBody();
            os.write(AUTH_REDIRECT.getBytes(StandardCharsets.UTF_8));
            os.flush();
            os.close();
            return;
        }
        
        if(!e.getRequestURI().toString().matches("/music/[0-9]{14,23}")) {
            StringBuilder response = new StringBuilder("<!DOCTYPE HTML><html><head><meta charset='UTF-8'><style>.card-body{width:20vw;height:10vh}.card{width:20vw;height:10vh}.all-servers{box-sizing: border-box;}.server{padding: 1vw; float:left}.top-header{}.bottom-header{float:none;text-align:center}.container{}img{max-width:10vw;width:10vw}body{background-color:#2C2F33;color:#FFFFFF}a{color:white;}</style></head><body>");
            
            response.append("<h1 class='bottom-header'>See the music playing in your servers.</h1><div class='all-servers'>");
            response.append("</div><h1 class='bottom-header' style='text-align:center;float:none;'>Don't see your server?  <a href='")
                    .append("https://untitled-bot.xyz/invite")
                    .append("'>Invite the bot!</a></h1>");
            
            for(int i = 0; i < 20; i++)
            for(Guild g : u.getMutualGuilds()) {
                response.append("<div class='card' style='float:left;display:inline-block;max-width:18vw;margin:2vw;background-color:#2C2F33'><img style='width:6vw;height:auto' class='card-image-top' src='")
                        .append(g.getIconUrl() != null ? g.getIconUrl() : DEFAULT_AVATAR_URL)
                        .append("'><br><div class='card-body'><div class='card-title'><p><a href='/music/")
                        .append(g.getId())
                        .append("'>")
                        .append(es(g.getName()))
                        .append("</a></p></div></div></div>");
            }
            
            response.append("</body></html>");
            
            e.sendResponseHeaders(200, response.length());
            OutputStream os = e.getResponseBody();
            os.write(response.toString().getBytes(StandardCharsets.UTF_8));
            os.flush();
            os.close();
            return;
        }
        
        AudioTrack track;
        Guild g;
        
        StringBuilder nowPlaying = new StringBuilder("<!DOCTYPE HTML><html><head><meta charset='UTF-8'><script src='https://code.jquery.com/jquery-3.5.1.min.js'></script><script>function rl(){window.location.reload();};$.ajax({type:'POST',url:'https://dash.untitled-bot.xyz/api/isDJ'});'</script><meta name=\"viewport\" content=\"width=device-width, initial-scale=1\"><link href=\"https://fonts.googleapis.com/css?family=Ubuntu\" rel=\"stylesheet\"><style>a{color:white}</style></head><body style='margin: 3vw;font-family:Ubuntu,serif;background-color:#2C2F33;color:white;'>");
        
        try {
            g = Objects.requireNonNull(Main.getGuildFromID(e.getRequestURI().toString().substring(7)));
            
            //make sure that the user that is fetching is a member of the server.
            if(!g.isMember(u)) {
                Logger.log("User " + u.getId() + " tried accessing guild " + g.getId() + " that they do not have access to.");
                e.sendResponseHeaders(200, AUTH_REDIRECT.length());
                OutputStream os = e.getResponseBody();
                os.write(AUTH_REDIRECT.getBytes(StandardCharsets.UTF_8));
                os.flush();
                os.close();
                return;
            }
            
            //server icon
            String iconURL = g.getIconUrl();
            if(iconURL == null)
                iconURL = ServerListBuilder.DEFAULT_DISCORD_ICON;
            
            nowPlaying.append(String.format("<div style='text-align:center;'><img src='%s' alt='%s'><br><h1>%s</h1><br><br><br><br><br></div>", iconURL, es(g.getName()), es(g.getName())));
            
            //dj controls
            nowPlaying.append("<div class='dj-controls'></div>");
            
            
            
            //if the player is not playing, send a special message.
            if(!MusicKernel.INSTANCE.isPlaying(Objects.requireNonNull(g))) {
                String b = nowPlaying.append("<h1>Nothing is playing right now.</h1>").toString();
                
                e.sendResponseHeaders(200, b.length());
                OutputStream os = e.getResponseBody();
                os.write(b.getBytes(StandardCharsets.UTF_8));
                os.flush();
                os.close();
                return;
            }
            
            track = MusicKernel.INSTANCE.nowPlaying(Objects.requireNonNull(g));
        } catch(NullPointerException ignored) {
            end404(e);
            return;
        }
        
        
        nowPlaying.append(String.format("<h1>Now playing: <a href='%s'>%s</a> by %s.</h1>", track.getInfo().uri, es(track.getInfo().title), es(track.getInfo().author)));
        
        try {
            nowPlaying.append(String.format("<h2 id='timestamp'>%d%% (%s / %s)</h2>", NowPlaying.getProgress(g), NowPlaying.current(g), NowPlaying.max(g)));
        } catch(Exception ignored) {}
        nowPlaying.append("<h2>Queued songs:</h2>");
        
        for(AudioTrack n : MusicKernel.INSTANCE.queue(g)) {
            if(n == null)
                continue;
            nowPlaying.append(String.format("<p class='queued-song'><a href='%s'>%s</a> by %s</p>", es(n.getInfo().uri), es(n.getInfo().title), es(n.getInfo().author)));
        }
        nowPlaying.append("</body></html>");
        
        e.sendResponseHeaders(200, nowPlaying.toString().length());
        
        OutputStream os = e.getResponseBody();
        os.write(nowPlaying.toString().getBytes(StandardCharsets.UTF_8));
        os.flush();
        os.close();
    }
    
    /**
     * Escape HTML
     * @param normal the normal string
     * @return escaped HTML
     */
    public static String es(String normal) {
        return StringEscapeUtils.escapeHtml(normal);
    }
    
    static void end404(HttpExchange e) throws IOException {
        e.sendResponseHeaders(404, NOT_FOUND.length());
        OutputStream os = e.getResponseBody();
        os.write(NOT_FOUND.getBytes(StandardCharsets.UTF_8));
        os.flush();
        os.close();
        e.close();
    }
}
