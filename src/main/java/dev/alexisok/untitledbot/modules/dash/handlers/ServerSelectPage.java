package dev.alexisok.untitledbot.modules.dash.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dev.alexisok.untitledbot.BotClass;
import dev.alexisok.untitledbot.Main;
import dev.alexisok.untitledbot.logging.Logger;
import dev.alexisok.untitledbot.modules.dash.builder.ServerListBuilder;
import dev.alexisok.untitledbot.modules.dash.util.Cookies;
import dev.alexisok.untitledbot.modules.music.MusicKernel;
import dev.alexisok.untitledbot.util.vault.LoadedVaultObject;
import dev.alexisok.untitledbot.util.vault.Vault;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import org.intellij.lang.annotations.Language;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static dev.alexisok.untitledbot.modules.dash.handlers.MusicHandler.*;

/**
 * Handles /servers requests.
 */
public class ServerSelectPage implements HttpHandler {
    
    @Language("HTML")
    private static final String NOT_FOUND = "<!DOCTYPE HTML><html><body>error: could not find the guild requested.  please tell me on <a href='https://discord.alexisok.dev'>the discord server</a> if you think this is an error.</body></html>";
    
    @Language("HTML")
    private static final String PAGE_CSS = "html{scroll-behavior:smooth}body{background-color:#23272A;font-family:Ubuntu, serif;max-width:100%}a,h1,h2,h3,p{color:white}.topnav{overflow:hidden;background-color:#23272A;position:fixed;top:0;left:0;width:100%;align-content:center}.topnav a{float:left;display:block;color:#ffffff;text-align:center;padding:14px 16px;text-decoration:none;font-size:17px;align-content:center}.topnav a:hover{background-color:#ffffff;color:black}.topnav a.active{background-color:#28b6cb;color:white}.topnav .icon{display:none}@media screen and (max-width: 600px){.topnav a:not(:first-child){display:none}.topnav a.icon{float:left;display:block}}@media screen and (max-width: 600px){.topnav.responsive .icon{position:center;right:0;top:0}.topnav.responsive a{float:none;display:block;text-align:left}}.tab{float:left;width:30%;height:300px}.tab button{display:block;background-color:inherit;color:white;padding:22px 16px;width:100%;border:none;outline:none;text-align:left;cursor:pointer;font-size:17px}.tab button:hover{transition:0.5s;background-color:#494949}.tab button.active{background-color:#494949}.tabcontent{float:left;padding:0 12px;width:70%;height:300px;display:none}.clearfix::after{transition:1s;content:\"\";clear:both;display:table}*{box-sizing:border-box;transition-timing-function:ease}ul{list-style:none;color:white;line-height:200%}ul li::before{content:\">\";color:white;font-family:Ubuntu,serif;line-height:200%}";
    
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
        
        StringBuilder response = new StringBuilder(1000);
        response.append("<!DOCTYPE HTML><html><head><meta charset='UTF-8'><script src='https://code.jquery.com/jquery-3.5.1.min.js'></script><script>function rl(){window.location.reload();}" +
                "function saveSettings(){}" +
                "</script><meta name=\"viewport\" content=\"width=device-width, initial-scale=1\"><link href=\"https://fonts.googleapis.com/css?family=Ubuntu\" rel=\"stylesheet\"><style>a{color:white}</style></head><body style='margin: 3vw;font-family:Ubuntu,serif;background-color:#2C2F33;color:white;'>");
        if(!e.getRequestURI().toString().matches("/servers/[0-9]{14,23}")) {
            
            for(Guild g : u.getMutualGuilds()) {
                Member m = g.getMember(u);
                if(m == null)
                    continue;
                try {
                    if(!m.hasPermission(Permission.MESSAGE_MANAGE))
                        continue;
                } catch(Throwable ignored) {continue;}
                response.append("<div class='card' style='float:left;display:inline-block;max-width:18vw;margin:2vw;background-color:#2C2F33'><img style='width:6vw;height:auto' class='card-image-top' src='")
                        .append(g.getIconUrl() != null ? g.getIconUrl() : u.getDefaultAvatarUrl())
                        .append("'><br><div class='card-body'><div class='card-title'><p><a href='/servers/")
                        .append(g.getId())
                        .append("'>")
                        .append(es(g.getName()))
                        .append("</a></p></div></div></div>");
                
            }
            String built = response.append("</body></html>").toString();
            e.sendResponseHeaders(200, built.length());
            OutputStream os = e.getResponseBody();
            os.write(built.getBytes(StandardCharsets.UTF_8));
            os.flush();
            os.close();
            e.close();
            return;
        }
        Guild g;
        
        try {
            g = Objects.requireNonNull(Main.getGuildFromID(e.getRequestURI().toString().substring(9)));
            
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
            
            response.append(String.format("<div style='text-align:center;'><img src='%s' alt='%s'><br><h1>%s</h1><br><br><br><br><br></div>", iconURL, es(g.getName()), es(g.getName())));
            
            String prefix = BotClass.getPrefix(g.getId(), null);
            
            //guild channels the bot can talk in
            List<TextChannel> channels = g.getTextChannels()
                    .stream()
                    .filter(TextChannel::canTalk)
                    .collect(Collectors.toList());
            
            //get the roles the bot can interact with
            List<Role> guildRoles = g.getRoles()
                    .stream()
                    .filter(r -> g.getSelfMember().canInteract(r))
                    .collect(Collectors.toList());
            
            //disk-optimized object created specifically for this.
            LoadedVaultObject o = new LoadedVaultObject(null, g.getId());
            
            //roles for leveling up
            Map<Integer, Role> levelUpRoles = new HashMap<>();
            
            for(int i = 0; i < 65536; i++) {
                String roleID = o.getUserDataLocal("role.reward." + i);
                
                if(roleID == null || roleID.equals("none")) continue;
                
                Role r = g.getRoleById(roleID);
                
                if(r == null) {
                    o.storeUserDataLocal("role.reward." + i, "none");
                    continue;
                }
                
                levelUpRoles.put(i, r);
            }
            
            int workMin = Integer.parseInt(Vault.getUserDataLocalOrDefault(null, g.getId(), "work.limit.minimum", "100"));
            int workMax = Integer.parseInt(Vault.getUserDataLocalOrDefault(null, g.getId(), "work.limit.maximum", "500"));
            
            int workTimeout = Integer.parseInt(Vault.getUserDataLocalOrDefault(null, g.getId(), "work.cooldown", "86400")); //1 day
            
            int stealMin = Integer.parseInt(Vault.getUserDataLocalOrDefault(null, g.getId(), "steal.limit.minimum", "50"));
            int stealMax = Integer.parseInt(Vault.getUserDataLocalOrDefault(null, g.getId(), "steal.limit.maximum", "300"));
            int stealChance = Integer.parseInt(Vault.getUserDataLocalOrDefault(null, g.getId(), "steal.chance", "50"));
            int stealTimeout = Integer.parseInt(Vault.getUserDataLocalOrDefault(null, g.getId(), "steal.cooldown", "300"));
            
            String levelUpMessage = Vault.getUserDataLocalOrDefault(null, g.getId(), "levelup-message", "Congrats [user], you have leveled up to level [level]!!");
            
            response.append("<br><br>").append("<form>").append("<label for='workMin'>Minimum amount to get from working</label><input type='number' id='workMin' max='9998' min='1' value='").append(workMin).append("'>").append("<label for='workMax'>Maximum amount to get from working</label><input type='number' id='workMax' max='9999' min='2' value='").append(workMax).append("'>").append("<label for='workTimeout'>Time in seconds between work command</label><input type='number' id='workTimeout' max='99999' min='10' value='").append(workTimeout).append("'>").append("<label for='stealMin'>Minimum amount to get from the steal command</label><input type='number' id='stealMin' max='9998' min='1' value='").append(stealMin).append("'>").append("<label for='stealMax'>Maximum amount to get from the steal command</label><input type='number' id='stealMax' max='9999' min='2' value='").append(stealMax).append("'>").append("<label for='stealChance'>Percentage chance for the steal command to work</label><input type='number' id='stealChance' max='99' min='1' value='").append(stealChance).append("'>").append("<label for='stealTimeout'>Timeout to use the steal command</label><input type='number' id='stealTimeout' max='99999' min='10' value='").append(stealTimeout).append("'>").append("<label for='levelUpMessage'>Message to send level up messages (use [user] and [level] for inline mentions)</label><input type='text' id='levelUpMessages' maxlength='512' value='").append(levelUpMessage).append("'>").append("</form>").append("<br><br><br><button onclick='saveSettings()'>Save changes</button>");
            
            e.sendResponseHeaders(200, response.toString().length());
            OutputStream os = e.getResponseBody();
            os.write(response.toString().getBytes(StandardCharsets.UTF_8));
            os.flush();
            os.close();
            e.close();
        } catch(NullPointerException ignored) {
            end404(e);
            return;
        }
    }
    
    private static String loadEmptyPage() {
        return "<!DOCTYPE HTML><html><head><title>untitled-bot-dashboard</title><link rel='stylesheet' href='/pub/dashboardPage.css'></head><body></body></html>";
    }
}
