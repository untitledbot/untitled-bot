package dev.alexisok.untitledbot.modules.basic.uptime;

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
import java.lang.management.ManagementFactory;
import java.util.concurrent.TimeUnit;

/**
 * @author AlexIsOK
 * @since 1.3
 */
public final class Uptime extends UBPlugin {

    public static String humanReadable() {
        return String.format("%.3f days", ManagementFactory.getRuntimeMXBean().getUptime() / 86400000.0);
    }

    @Override
    public @NotNull MessageEmbed onCommand(String[] args, @NotNull Message message) {
        EmbedBuilder eb = new EmbedBuilder();
        EmbedDefaults.setEmbedDefaults(eb, message);
    
        eb.addField("Uptime",
                String.format("Current uptime: %.2f days or %.2f hours.",
                        ManagementFactory.getRuntimeMXBean().getUptime() / 86400000.0, //86400000 ms in a day
                        ManagementFactory.getRuntimeMXBean().getUptime() / 86400000.0 * 24.0),
                false);
        eb.setColor(Color.GREEN);
        
        return eb.build();
    }
    
    @Override
    public void onRegister() {
        CommandRegistrar.register("uptime", this);
        Manual.setHelpPage("uptime", "literally get the uptime, what else would it do?");
    }
}
