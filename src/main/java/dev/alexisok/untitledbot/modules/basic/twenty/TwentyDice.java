package dev.alexisok.untitledbot.modules.basic.twenty;

import dev.alexisok.untitledbot.command.CommandRegistrar;
import dev.alexisok.untitledbot.command.EmbedDefaults;
import dev.alexisok.untitledbot.plugin.UBPlugin;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author AlexIsOK
 * @since 1.3
 */
public class TwentyDice extends UBPlugin {
    
    private static final String[] ROLL_FOR = {
            "luck", "gold", "attack", "damage", "size", "stats", "abilities", "health", "initiative", "charisma"
    };
    
    @Override
    public @Nullable MessageEmbed onCommand(String[] args, @NotNull Message message) {
        EmbedBuilder eb = new EmbedBuilder();
        EmbedDefaults.setEmbedDefaults(eb, message);
    
        int diceRoll = ThreadLocalRandom.current().nextInt(1, 21);
        int rollFor  = ThreadLocalRandom.current().nextInt(1, ROLL_FOR.length);
    
        eb.addField(String.format("Rolling for %s.", args.length == 1 ? ROLL_FOR[rollFor] : args[1]),
                String.format("You rolled %d", diceRoll),
                false);
        
        if(diceRoll < 8)
            eb.setColor(Color.RED);
        else if(diceRoll < 14)
            eb.setColor(Color.YELLOW);
        else
            eb.setColor(Color.GREEN);
        
        return eb.build();
    }
    
    @Override
    public void onRegister() {
        CommandRegistrar.register("20", "fun.twenty", this);
    }
}
