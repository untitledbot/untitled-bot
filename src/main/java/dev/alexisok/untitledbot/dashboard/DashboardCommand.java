package dev.alexisok.untitledbot.dashboard;

import dev.alexisok.untitledbot.command.CommandRegistrar;
import dev.alexisok.untitledbot.command.EmbedDefaults;
import dev.alexisok.untitledbot.plugin.UBPlugin;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

/**
 * Dashboard command.
 */
public final class DashboardCommand extends UBPlugin {
    
    @NotNull
    @Override
    public MessageEmbed onCommand(String[] args, @NotNull Message message) {
        EmbedBuilder eb = new EmbedBuilder();
        EmbedDefaults.setEmbedDefaults(eb, message);
        
        eb.setTitle("Dashboard");
        eb.setDescription("You can view the untitled-bot dashboard at [dash.untitled-bot.xyz](https://dash.untitled-bot.xyz)");
        eb.setColor(Color.GREEN);
        
        return eb.build();
    }
    
    @Override
    public void onRegister() {
        CommandRegistrar.register("dashboard", this);
        DashboardServer.init();
    }
}
