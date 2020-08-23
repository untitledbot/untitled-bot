package dev.alexisok.untitledbot.modules.reactions;

import dev.alexisok.untitledbot.command.CommandRegistrar;
import dev.alexisok.untitledbot.command.EmbedDefaults;
import dev.alexisok.untitledbot.command.Manual;
import dev.alexisok.untitledbot.logging.Logger;
import dev.alexisok.untitledbot.plugin.UBPlugin;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Random;

/**
 * Hug another user
 * 
 * @author AlexIsOK
 * @since 1.3
 */
public final class Hug extends UBPlugin {
    
    private static final ArrayList<String> GIF_URLS = new ArrayList<>();
    
    private static final String LOCATION = "./reactions_dir/hug.txt";
    
    @Override
    public void onRegister() {
        try {
            GIF_URLS.addAll(Files.readAllLines(Paths.get(LOCATION), StandardCharsets.UTF_8));
        } catch(IOException ignored) {
            Logger.critical(String.format("Could not load %s for reading.", LOCATION), -1, false);
            return; //do not register the command if it fails
        }
        
        CommandRegistrar.register("hug", this);
        Manual.setHelpPage("hug", "Hug another user <3");
    }
    
    @Override
    public @NotNull MessageEmbed onCommand(@NotNull String[] args, @NotNull Message message) {
        EmbedBuilder eb = new EmbedBuilder();
        EmbedDefaults.setEmbedDefaults(eb, message);
        
        //use the message ID as a seed to ensure maximum randomness
        int rand = new Random(message.getIdLong()).nextInt(GIF_URLS.size());
        
        eb.setDescription(message.getMentionedMembers().size() == 0 ? null : String.format("*<@%s> has been hugged <3*", message.getMentionedMembers().get(0).getId()));
        eb.setImage(GIF_URLS.get(rand));
        eb.setColor(Color.GREEN);
        
        return eb.build();
    }
}
