package dev.alexisok.untitledbot.modules.basic.rot13;

import dev.alexisok.untitledbot.command.CommandRegistrar;
import dev.alexisok.untitledbot.command.EmbedDefaults;
import dev.alexisok.untitledbot.command.Manual;
import dev.alexisok.untitledbot.plugin.UBPlugin;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.awt.Color;

/**
 * Do ROT13 stuffs
 * 
 * @author AlexIsOK
 * @since 1.4.0
 */
public final class ROT13 extends UBPlugin {
    
    @NotNull
    @Override
    @Contract(pure = true)
    public MessageEmbed onCommand(@NotNull String[] args, @NotNull Message message) {
        EmbedBuilder eb = new EmbedBuilder();
        EmbedDefaults.setEmbedDefaults(eb, message);
        
        if(args.length == 1) {
            eb.addField("ROT13", "Please input a string to ROT13", false);
            eb.setColor(Color.RED);
            return eb.build();
        }
        
        char[] data = String.join(" ", ArrayUtils.remove(args, 0)).toCharArray();
        
        for(int i = 0; i < data.length; i++) {
            data[i] = switchChar(data[i]);
        }
        
        String total = new String(data);
        
        if(total.length() >= 1000) {
            eb.addField("ROT13", "The input string must be less than 1000 characters.", false);
            eb.setColor(Color.RED);
            return eb.build();
        }
        
        eb.addField("ROT13", total, false);
        eb.setColor(Color.GREEN);
        return eb.build();
    }
    
    @Contract(pure = true)
    private static char switchChar(char c) {
        if(c >= 65 && c <= 77) //A-M
            return (char) (c + 13);
        else if(c >= 78 && c <= 90)
            return (char) (c - 13);
        else if(c >= 97 && c <= 109)
            return (char) (c + 13);
        else if(c >= 110 && c <= 122)
            return (char) (c - 13);
        else
            return c;
    }
    
    @Override
    public void onRegister() {
        CommandRegistrar.register("rot13", this);
        Manual.setHelpPage("rot13", "Run a string through [ROT13](https://en.wikipedia.org/wiki/ROT13)");
    }
}
