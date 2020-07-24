package dev.alexisok.untitledbot.modules.basic.ship;

import dev.alexisok.untitledbot.command.CommandRegistrar;
import dev.alexisok.untitledbot.command.EmbedDefaults;
import dev.alexisok.untitledbot.logging.Logger;
import dev.alexisok.untitledbot.plugin.UBPlugin;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Some people in the server I tried this in wanted it so here it is.
 * 
 * @author AlexIsOK
 * @since 1.0.0
 */
public final class Ship extends UBPlugin {
    
    @Override
    public @NotNull MessageEmbed onCommand(String[] args, Message message) {
        EmbedBuilder eb = new EmbedBuilder();
        EmbedDefaults.setEmbedDefaults(eb, message);
        
        if(message.getMentionedMembers().size() != 2) {
            eb.addField("Ship", "Usage: `ship <user 1> <user 2>`", false);
            eb.setColor(Color.RED);
            
            return eb.build();
        }
        
        int rand = ThreadLocalRandom.current().nextInt(100) + 1;
    
        Logger.debug((String.valueOf(rand)));
        
        String response = rand + "% ";
        
        if (rand < 10) {
            response += "Definitely not :nauseated_face:";
        } else if (rand < 20) {
            response += "Doubt it :unamused:";
        } else if (rand < 30) {
            response += "Not a good match :confused:";
        } else if (rand < 40) {
            response += "Not bad :neutral_face:";
        } else if (rand < 50) {
            response += "Could go either way :slight_smile:";
        } else if (rand < 60) {
            response += "Has potential :smiley:";
        } else if (rand < 70) {
            response += "Would make an okay couple! :grin:";
        } else if (rand < 80) {
            response += "Would make a good couple! :blush:";
        } else if (rand < 90) {
            response += "Would make a great couple! :heart_eyes:";
        } else if (rand < 100) {
            response += "Pretty close to perfect! :kissing_heart::heart:";
        } else if (rand == 100) {
            response += "Meant to be!! :ring::smiling_face_with_3_hearts::two_hearts::two_hearts:";
        }
        
        eb.addField("", response, false);
        return eb.build();
    }
    
    @Override
    public void onRegister() {
        CommandRegistrar.register("ship", "core.fun.ship", this);
        CommandRegistrar.setDefaultPermissionForNode("core.fun.ship", true);
    }
}
