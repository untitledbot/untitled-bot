package dev.alexisok.untitledbot.modules.basic.eightball;

import dev.alexisok.untitledbot.command.Command;
import dev.alexisok.untitledbot.command.CommandRegistrar;
import dev.alexisok.untitledbot.command.EmbedDefaults;
import dev.alexisok.untitledbot.plugin.UBPlugin;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 
 * 8ball command example.  This class will have the bot listen for the
 * command "8ball" and run {@link EightBall#onCommand(String[], Message)} when it
 * is triggered.
 * 
 * @author AlexIsOK
 * @since 0.0.1
 */
public final class EightBall extends UBPlugin implements Command {
    
    private static final String[] RESPONSES = {
            ":green_circle: It is certain.", ":green_circle: It is decidedly so.", ":green_circle: Without a doubt",
            ":green_circle: Yes - definitely.", ":green_circle: You may rely on it.",
            ":green_circle: As I see it, yes.", ":green_circle: Most likely.", ":green_circle: Outlook good.",
            ":green_circle: Yes.", ":green_circle: Signs point to yes.",
        
            ":yellow_circle: Reply hazy, try again.", ":yellow_circle: Ask again later.", ":yellow_circle: Better not tell you now.",
            ":yellow_circle: Cannot predict now.", ":yellow_circle: Concentrate and ask again.",
        
            ":red_circle: Don't count on it.", ":red_circle: My reply is no.", ":red_circle: My sources say no.",
            ":red_circle: Outlook not so good.", ":red_circle: Very doubtful.",
        
            "This is the secret reply.  It is not possible to get and if you get it you should tell <@541763812676861952> RIGHT NOW."
    };
    
    @Override
    public void onRegister() {
        //in a real plugin, you would not use module as the first word.
        CommandRegistrar.register("8ball", new EightBall());
        CommandRegistrar.registerAlias("8ball", "8");
    }
    
    @Override
    public MessageEmbed onCommand(String[] args, @NotNull Message message) {
        
        EmbedBuilder eb = new EmbedBuilder();
        EmbedDefaults.setEmbedDefaults(eb, message);
        
        int a = ThreadLocalRandom.current().nextInt(0, 20);
        Color returnColor = Color.BLACK;
        if(RESPONSES[a].contains(":red_circle:"))
            returnColor = Color.RED;
        else if(RESPONSES[a].contains(":yellow_circle:"))
            returnColor = Color.YELLOW;
        else if(RESPONSES[a].contains(":green_circle:"))
            returnColor = Color.GREEN;
        
        eb.setColor(returnColor);
        eb.addField("8Ball", RESPONSES[a], false);
        return eb.build();
    }
}
