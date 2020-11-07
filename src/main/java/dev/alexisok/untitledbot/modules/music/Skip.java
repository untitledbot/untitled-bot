package dev.alexisok.untitledbot.modules.music;

import dev.alexisok.untitledbot.command.CommandRegistrar;
import dev.alexisok.untitledbot.command.EmbedDefaults;
import dev.alexisok.untitledbot.command.Manual;
import dev.alexisok.untitledbot.plugin.UBPlugin;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

/**
 * @author AlexIsOK
 * @since 1.3.23
 */
public final class Skip extends UBPlugin {
    
    @Override
    public @Nullable MessageEmbed onCommand(String[] args, @NotNull Message message) {

        EmbedBuilder eb = new EmbedBuilder();
        EmbedDefaults.setEmbedDefaults(eb, message);
        
        MusicKernel.INSTANCE.skip(message.getGuild());
        
        eb.addField("Skip", "Le track has been skipped", false);
        eb.setColor(Color.GREEN);
        return eb.build();
    }
    
    @Override
    public void onRegister() {
        CommandRegistrar.register("skip", this);
        Manual.setHelpPage("skip", "Skips the currently playing track.");
        CommandRegistrar.registerAlias("skip", "s", "next", "notthisagain", "pass");
    }
}
