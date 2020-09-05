package dev.alexisok.untitledbot.modules.basic.ping;

import dev.alexisok.untitledbot.Main;
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

/**
 * @author AlexIsOK
 * @since 1.3.21
 */
public final class Ping extends UBPlugin {
    
    @NotNull
    @Override
    @Contract(pure = true)
    public MessageEmbed onCommand(String[] args, @NotNull Message message) {
        EmbedBuilder eb = new EmbedBuilder();
        EmbedDefaults.setEmbedDefaults(eb, message);
    
        eb.addField("Pong!",
                String.format("%d ms.", Main.jda.getGatewayPing()),
                false);
        eb.setColor(Color.GREEN);
        return eb.build();
    }
    
    @Override
    public void onRegister() {
        CommandRegistrar.register("ping", this);
        Manual.setHelpPage("ping", "Get the ping of the bot.");
    }
}
