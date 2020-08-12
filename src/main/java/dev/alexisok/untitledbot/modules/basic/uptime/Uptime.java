package dev.alexisok.untitledbot.modules.basic.uptime;

import dev.alexisok.untitledbot.command.CommandRegistrar;
import dev.alexisok.untitledbot.command.EmbedDefaults;
import dev.alexisok.untitledbot.command.Manual;
import dev.alexisok.untitledbot.plugin.UBPlugin;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.lang.management.ManagementFactory;
import java.util.concurrent.TimeUnit;

/**
 * @author AlexIsOK
 * @since 1.3
 */
public class Uptime extends UBPlugin {
    
    @Override
    public @Nullable MessageEmbed onCommand(String[] args, Message message) {
        EmbedBuilder eb = new EmbedBuilder();
        EmbedDefaults.setEmbedDefaults(eb, message);
    
        eb.addField("Uptime",
                String.format("Current uptime: %dms (%.2f days).",
                        ManagementFactory.getRuntimeMXBean().getUptime(),
                        ManagementFactory.getRuntimeMXBean().getUptime() / 86400000.0), //86400000 ms in a day
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
