package dev.alexisok.untitledbot.modules.rank;

import dev.alexisok.untitledbot.command.EmbedDefaults;
import dev.alexisok.untitledbot.modules.vault.Vault;
import dev.alexisok.untitledbot.plugin.UBPlugin;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

/**
 * Settings for ranks.
 * 
 * @author AlexIsOK
 * @since 1.3
 */
public final class RankSettings extends UBPlugin {
    
    @Override
    public @NotNull MessageEmbed onCommand(String[] args, Message message) {
        EmbedBuilder eb = new EmbedBuilder();
        EmbedDefaults.setEmbedDefaults(eb, message);
        
        if(args.length != 3) {
            eb.addField("Ranking", "Usage: `rank-settings <setting> <true|false>`\n" +
                                           "See `help rank-settings` for more information.", false);
            eb.setColor(Color.RED);
            return eb.build();
        }
        
        if(args[1].equalsIgnoreCase("announce-xp-boost")) {
            boolean enable = Boolean.parseBoolean(args[2]);
            Vault.storeUserDataLocal(null, message.getGuild().getId(), "ranks-broadcast.boost", String.valueOf(enable));
            
            eb.setColor(Color.GREEN);
            eb.addField("Ranking", "XP boost broadcasts have been set to `" + enable + "`.", false);
            
            return eb.build();
        }
        
        eb.setColor(Color.RED);
        eb.addField("Ranking", "See `help rank-settings` for specific keys.", false);
        
        return eb.build();
    }
}
