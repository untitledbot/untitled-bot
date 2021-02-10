package dev.alexisok.untitledbot.modules.anime;

import dev.alexisok.untitledbot.command.CommandRegistrar;
import dev.alexisok.untitledbot.command.EmbedDefaults;
import dev.alexisok.untitledbot.command.Manual;
import lombok.AllArgsConstructor;
import net.dv8tion.jda.api.EmbedBuilder;
import org.jetbrains.annotations.Contract;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public final class AllEndpoints {
    
    private static final List<ShiroEndpoint> ENDPOINTS = new ArrayList<>();
    
    static {
        ENDPOINTS.add(new ShiroEndpoint("avatars", "/avatars", "Here is your avatar", null, "Get an anime avatar", false));
        ENDPOINTS.add(new ShiroEndpoint("blush", "/blush", ":flushed:", null, "Blush image/gif", false));
        ENDPOINTS.add(new ShiroEndpoint("cry", "/cry", ":(", null, "Cry image/gifs", false));
        ENDPOINTS.add(new ShiroEndpoint("hug", "/hug", ":)", "%AUTHOR; hugs %MENTIONED; <3", "Hug image/gif", false));
        ENDPOINTS.add(new ShiroEndpoint("neko", "/neko", "here is the neko image i guess", null, "uhm", false));
        ENDPOINTS.add(new ShiroEndpoint("nom", "/nom", "Om-nom-nom", null, "Eat foodstuffs", false));
        ENDPOINTS.add(new ShiroEndpoint("pat", "/pat", "Here come the pats", "%AUTHOR; pats %MENTIONED; :)", "Pat someone", true));
        ENDPOINTS.add(new ShiroEndpoint("poke", "/poke", "Poke", "%AUTHOR; pokes %MENTIONED; >:)", "Poke someone", true));
        ENDPOINTS.add(new ShiroEndpoint("pout", "/pout", "Here is your pout image", null, "Pout image/gif", false));
        ENDPOINTS.add(new ShiroEndpoint("slap", "/slap", "Ouch!!1", "%AUTHOR; slaps %MENTIONED;!!!!", "Slap someone", true));
        ENDPOINTS.add(new ShiroEndpoint("smug", "/smug", "Here is the smug image", null, "Smug image/gif", false));
        ENDPOINTS.add(new ShiroEndpoint("tickle", "/tickle", ":D", "%AUTHOR; tickles %MENTIONED; :D", "Tickle someone", true));
        ENDPOINTS.add(new ShiroEndpoint("wallpaper", "/wallpapers", "Here is your wallpaper", null, "Get an anime wallpaper", false));
    }
    
    /**
     * Register all endpoints.
     */
    @Contract
    @SuppressWarnings("ConstantConditions")
    public static void addEndpoints() {
        ENDPOINTS.forEach(e -> {
            CommandRegistrar.register(e.name, (args, message) -> {
                EmbedBuilder eb = new EmbedBuilder();
                EmbedDefaults.setEmbedDefaults(eb, message);
                
                //the main thing
                eb.setImage(GetShiroImage.get(e.endpoint));
                
                //if the message has special mention things
                if(e.usesMentions && message.getMentionedMembers().size() == 1)
                    eb.setDescription(e.titleB
                            .replace("%AUTHOR;", message.getMember().getEffectiveName())
                            .replace("%MENTIONED;", message.getMentionedMembers().get(0).getEffectiveName()));
                else
                    eb.setDescription(e.titleA);
                
                //why is it pink?  because anime.
                eb.setColor(Color.PINK);
                return eb.build();
            });
            
            //set the help page of the command
            Manual.setHelpPage(e.name, String.format("%s%nUsage: `%s%s`%nImages obtained from the shiro.gg API.",
                    e.help,
                    e.name,
                    e.usesMentions ? " [@user]" : ""));
        });
        
    }
    
    @AllArgsConstructor
    private static final class ShiroEndpoint {
        
        //the name of the command
        private final String name;
        
        //endpoint with a leading /
        private final String endpoint;
        
        //A is without a mention, B is with a mention
        private final String titleA, titleB;
        
        //help command start (just the first line)
        private final String help;
        
        //if the bot uses mentions for special messages.
        private final boolean usesMentions;
        
    }
    
}
