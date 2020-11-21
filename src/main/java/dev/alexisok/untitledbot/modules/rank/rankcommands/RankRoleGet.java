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
        
        //[0] is level 1, there are 65536 levels in total
        String[] levels = new String[65536];
        
        int level = 0;
        
        if(args.length == 2 && args[1].matches("^[123456789]$"))
            level = Integer.parseInt(args[1]);
        
        for(int i = 1; i <= 65535; i++) {
            String role = Vault.getUserDataLocal(null, message.getGuild().getId(), "role.reward." + i);
            if(role == null || role.equals("none"))
                continue;
            levels[i] = String.format("<@&%s>", role);
        }
        
        boolean tooMuch = false;
        
        StringBuilder addStr = new StringBuilder();
        for(int i = 65535; i > 0; i--) {
            if(levels[i] == null || levels[i].isEmpty())
                continue;
            if(addStr.length() >= 1950) {
                if(level == 0) {
                    tooMuch = true;
                    break;
                } else {
                    level--;
                    addStr.setLength(0);
                }
            }
            addStr.append(String.format("Level %d: %s%n", i, levels[i]));
        }
        
        if(tooMuch) {
            eb.addField("Note:", "I am only displaying some of the roles as there are a lot on here!\n" +
                    "Do `rank-roles [page]` to list more roles.", false);
        }
        
        eb.setTitle("Rank Roles");
        eb.setDescription(addStr);
        eb.setColor(Color.GREEN);
        
        return eb.build();
    }
    
    @Override
    public void onRegister() {
        CommandRegistrar.register("rank-roles", this);
        Manual.setHelpPage("rank-roles", "Get the rank roles this server has.");
    }
}
