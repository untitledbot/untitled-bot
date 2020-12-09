package dev.alexisok.untitledbot.modules.rpg;

import dev.alexisok.untitledbot.command.CommandRegistrar;
import dev.alexisok.untitledbot.command.EmbedDefaults;
import dev.alexisok.untitledbot.command.Manual;
import dev.alexisok.untitledbot.plugin.UBPlugin;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * Handles the RPG command.
 * 
 * @author AlexIsOK
 * @since 1.3.25
 */
public final class RPGHandle extends UBPlugin {
    
    private static final Map<String, RPGData> DATA = new HashMap<>();
    
    static {
        
    }
    
    @Override
    public synchronized @NotNull MessageEmbed onCommand(@NotNull String[] args, @NotNull Message message) {
        EmbedBuilder eb = new EmbedBuilder();
        EmbedDefaults.setEmbedDefaults(eb, message);
        
        if(args.length == 1) {
            eb.setTitle("RPG");
            eb.setDescription("Please refer to https://untitled-bot.xyz/rpg for help with the RPG.");
            return eb.build();
        }
        
        return null;
    }
    
    @Override
    public void onRegister() {
        CommandRegistrar.register("rpg", this);
        Manual.setHelpPage("rpg", "Please refer to https://untitled-bot.xyz/rpg for help with the RPG.");
    }
}
