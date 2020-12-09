package dev.alexisok.untitledbot.modules.rank.xpcommands;

import dev.alexisok.untitledbot.command.CommandRegistrar;
import dev.alexisok.untitledbot.command.EmbedDefaults;
import dev.alexisok.untitledbot.command.Manual;
import dev.alexisok.untitledbot.plugin.UBPlugin;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

/**
 * @author AlexIsOK
 * @since 1.3.25
 */
public class Use extends UBPlugin {
    
    @Override
    public @NotNull MessageEmbed onCommand(@NotNull String[] args, @NotNull Message message) {
        EmbedBuilder eb = new EmbedBuilder();
        EmbedDefaults.setEmbedDefaults(eb, message);
        
        for(int i = 0; i < Shop.ITEMS.size(); i++) {
            
        }
        
        eb.setDescription("I couldn't find an item with that name in your inventory, are you sure you have it?");
        return eb.setColor(Color.RED).build();
    }
    
    @Override
    public void onRegister() {
        CommandRegistrar.register("use", this);
        Manual.setHelpPage("use", "Use an item in your inventory.");
    }
}
