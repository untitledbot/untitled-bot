package dev.alexisok.untitledbot.modules.basic.owo;

import dev.alexisok.untitledbot.command.CommandRegistrar;
import dev.alexisok.untitledbot.command.EmbedDefaults;
import dev.alexisok.untitledbot.command.Manual;
import dev.alexisok.untitledbot.plugin.UBPlugin;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.Arrays;

/**
 * OwOfies strings
 * 
 * i apologise in advance for this
 * 
 * @author AlexIsOK
 * @since 1.3
 */
public class Owo extends UBPlugin {
    
    @Override
    public @NotNull MessageEmbed onCommand(@NotNull String[] args, @NotNull Message message) {
        EmbedBuilder eb = new EmbedBuilder();
        EmbedDefaults.setEmbedDefaults(eb, message);
        
        if(args.length == 1) {
            eb.addField("OwO", ",,,sowwy,,, but youw stwing cannowt be emptwy,,,,,,,,", false);
            eb.setColor(Color.RED);
            return eb.build();
        }
        
        String messageToOwO = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
        
        eb.addField("", owofy(messageToOwO), false);
        eb.setColor(Color.PINK); //...
        return eb.build();
    }
    
    @Override
    public void onRegister() {
        CommandRegistrar.register("owo", this);
        Manual.setHelpPage("owo", "make a stwing mowe owo okay!!1");
    }
    
    @NotNull
    @Contract(pure=true)
    private static String owofy(@NotNull String str) {
        return str
                       .replace("r", "w")
                       .replace("l", "w")
                       .replace("!", "!!!")
                       .replace("?", "?!")
                       .replace("-", "~")
                       .replace(".", ",")
                       .replace("no", "nyo")
                       .replace("owo", " OwO ");
    }
}
