package dev.alexisok.untitledbot.modules.basic.bruh;

import dev.alexisok.untitledbot.command.CommandRegistrar;
import dev.alexisok.untitledbot.command.EmbedDefaults;
import dev.alexisok.untitledbot.plugin.UBPlugin;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * idk man
 * 
 * @author AlexIsOK
 * @since 1.3.23
 */
public final class Bruh extends UBPlugin {
    
    @Override
    public @NotNull MessageEmbed onCommand(String[] args, @NotNull Message message) {
        EmbedBuilder eb = new EmbedBuilder();
        EmbedDefaults.setEmbedDefaults(eb, message);
        eb.setImage("https://tenor.com/view/bruh-seriously-bro-shutup-come-gif-14409930");
        return eb.build();
    }

    @Override
    public void onRegister() {
        CommandRegistrar.register("bruh", this);
    }
}
