package dev.alexisok.untitledbot.modules.rank.rankcommands;

import dev.alexisok.untitledbot.command.CommandRegistrar;
import dev.alexisok.untitledbot.command.EmbedDefaults;
import dev.alexisok.untitledbot.command.Manual;
import dev.alexisok.untitledbot.modules.vault.Vault;
import dev.alexisok.untitledbot.plugin.UBPlugin;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

/**
 * 
 * Get a list of all Rank Roles
 * 
 * @author AlexIsOK
 * @since 1.3
 */
public final class RankRoleGet extends UBPlugin {
    
    @NotNull
    @Override
    @Contract(pure=true)
    public MessageEmbed onCommand(@NotNull String[] args, @NotNull Message message) {
        EmbedBuilder eb = new EmbedBuilder();
        EmbedDefaults.setEmbedDefaults(eb, message);
        
        //[0] is level 1, there are 100 levels in total
        String[] levels = new String[101];
        
        for(int i = 1; i <= 100; i++) {
            String role = Vault.getUserDataLocal(null, message.getGuild().getId(), "role.reward." + i);
            if(role == null || role.equals("none"))
                continue;
            levels[i] = String.format("<@&%s>", role);
        }
        
        StringBuilder addStr = new StringBuilder();
        for(int i = 100; i > 0; i--) {
            if(levels[i] == null || levels[i].isEmpty())
                continue;
            addStr.append(String.format("Level %d: %s%n", i, levels[i]));
        }
        
        eb.addField("Rank Roles", addStr.toString(), false);
        eb.setColor(Color.GREEN);
        
        return eb.build();
    }
    
    @Override
    public void onRegister() {
        CommandRegistrar.register("rank-roles", this);
        Manual.setHelpPage("rank-roles", "Get the rank roles this server has.");
    }
}
