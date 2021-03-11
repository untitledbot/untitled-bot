package dev.alexisok.untitledbot.modules.dash;

import dev.alexisok.untitledbot.command.CommandRegistrar;
import dev.alexisok.untitledbot.command.EmbedDefaults;
import dev.alexisok.untitledbot.command.Manual;
import dev.alexisok.untitledbot.logging.Logger;
import dev.alexisok.untitledbot.plugin.UBPlugin;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public final class Dashboard extends UBPlugin {
    
    static {
        try {
            Logger.log("Staring the dashboard...");
            DashHost.init();
            Logger.log("Dashboard initialized.");
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
    
    @Override
    public @NotNull MessageEmbed onCommand(String[] args, @NotNull Message message) {
        EmbedBuilder eb = new EmbedBuilder();
        EmbedDefaults.setEmbedDefaults(eb, message);
        
        eb.addField("Dashboard", "You can access the Dashboard for untitled-bot [here](https://dash.untitled-bot.xyz/)", false);
        return eb.build();
    }
    
    @Override
    public void onRegister() {
        CommandRegistrar.register("dashboard", this);
        Manual.setHelpPage("dashboard", "Placeholder command for the dashboard.");
    }
}
