package dev.alexisok.untitledbot.modules.basic.catfact;

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
import org.json.JSONObject;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * Gives you a cat fact
 * 
 * @author AlexIsOK
 * @since 1.4.0
 */
public final class CatFact extends UBPlugin {
    
    @NotNull
    @Override
    @Contract(pure = true)
    public MessageEmbed onCommand(String[] args, @NotNull Message message) {
        EmbedBuilder eb = new EmbedBuilder();
        EmbedDefaults.setEmbedDefaults(eb, message);
        
        try {
            URL url = new URL("https://catfact.ninja/fact");
            BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
            String fact = new JSONObject(br.readLine()).getString("fact");
            eb.addField("Cat Fact", fact, false);
            eb.setColor(Color.GREEN);
            return eb.build();
        } catch(IOException ignored) {
            eb.addField("Error", "There was an error connecting to catfact.ninja, maybe try again?", false);
            eb.setColor(Color.RED);
            return eb.build();
        }
    }
    
    @Override
    public void onRegister() {
        CommandRegistrar.register("catfact", this);
        Manual.setHelpPage("catfact", "Displays a cat fact from [catfact.ninja](https://catfact.ninja).");
        CommandRegistrar.registerAlias("catfact", "cat");
    }
}
