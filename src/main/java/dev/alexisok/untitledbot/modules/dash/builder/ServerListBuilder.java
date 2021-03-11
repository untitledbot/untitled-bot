package dev.alexisok.untitledbot.modules.dash.builder;

import dev.alexisok.untitledbot.Main;
import net.dv8tion.jda.api.entities.Guild;

import java.util.ArrayList;
import java.util.List;

/**
 * Builds server lists.
 */
public final class ServerListBuilder {
    
    public static final String DEFAULT_DISCORD_ICON = "https://cdn.discordapp.com/embed/avatars/1.png";
    
    /**
     * Builds servers with a list of server IDs.
     * @param guildIDs the IDs of the guilds.
     * @return the built server chunk.
     */
    public static String buildServer(String[] guildIDs) {
        long[] guilds = new long[guildIDs.length];
        
        for(int i = 0; i < guildIDs.length; i++) {
            guilds[i] = Long.parseLong(guildIDs[i]);
        }

        return buildServer(guilds);
    }
    
    /**
     * Builds servers with a list of server IDs.
     * @param guildIDs the IDs of the guilds.
     * @return the built server chunk.
     */
    public static String buildServer(long[] guildIDs) {
        Guild[] guilds = new Guild[guildIDs.length];
        for (int i = 0; i < guildIDs.length; i++) {
            try {
                guilds[i] = Main.getGuildFromID(guildIDs[i]);
            } catch(Throwable ignored) {
                guilds[i] = null;
            }
        }
        return buildServer(guilds);
    }

    /**
     * Build the servers.
     * @param guilds the guilds.
     * @return the built server chunk to be displayed to the user.
     */
    public static String buildServer(Guild[] guilds) {
        //guilds the bot is in
        List<String> inside = new ArrayList<>();
        
        for(Guild g : guilds) {
            if(g == null)
                continue;
            if(g.isMember(Main.jda[0].getSelfUser()))
                inside.add(buildSingleton(g));
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("<html><head><meta charset='UTF-8'></head><body>");
        
        inside.forEach(sb::append);
        
        return sb.append("</body></html>").toString();
    }
    
    private static String buildSingleton(Guild g) {
        String url = g.getIconUrl();
        if(url == null)
            url = DEFAULT_DISCORD_ICON;
        
        return String.format("" +
                "<div class='server'>" +
                "<img src='%s' class='server_icon'>" +
                "<h1>%s</h1>" +
                "<br><br>" +
                "</div>", url, g.getName());
    }
    
}
