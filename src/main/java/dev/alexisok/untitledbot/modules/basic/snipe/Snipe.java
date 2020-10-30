package dev.alexisok.untitledbot.modules.basic.snipe;

import com.google.common.cache.Cache;
import dev.alexisok.untitledbot.command.EmbedDefaults;
import dev.alexisok.untitledbot.modules.vault.Vault;
import dev.alexisok.untitledbot.plugin.UBPlugin;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author AlexIsOK
 * @since 1.3.22
 */
public final class Snipe extends UBPlugin {
    
    private static final HashMap<String, Boolean> ENABLED_CACHE = new HashMap<>();
    
    @Override
    public @Nullable MessageEmbed onCommand(String[] args, @NotNull Message message) {
        EmbedBuilder eb = new EmbedBuilder();
        EmbedDefaults.setEmbedDefaults(eb, message);
        
        if(!ENABLED_CACHE.containsKey(message.getGuild().getId()))
            ENABLED_CACHE.put(message.getGuild().getId(),
                    Boolean.parseBoolean(Vault.getUserDataLocalOrDefault(null,
                            message.getGuild().getId(),
                            "snipe.enabled",
                            "false")));
        
        if(!ENABLED_CACHE.get(message.getGuild().getId()).equals("true")) {
            eb.setTitle("Snipe");
            eb.setDescription("The server does not have the `snipe` command enabled.  Please have a " +
                    "server administrator use the (dashboard)[DASHLINK] " +
                    "to enable it.");//TODO
            return eb.build();
        }
        return null;
    }
    
}
