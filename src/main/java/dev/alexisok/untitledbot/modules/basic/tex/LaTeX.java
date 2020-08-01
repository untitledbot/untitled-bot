package dev.alexisok.untitledbot.modules.basic.tex;

import dev.alexisok.untitledbot.command.CommandRegistrar;
import dev.alexisok.untitledbot.command.Manual;
import dev.alexisok.untitledbot.plugin.UBPlugin;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jetbrains.annotations.Nullable;

/**
 * Latex maker
 * 
 * TODO finish this
 * 
 * @author AlexIsOK
 * @since 1.3
 */
public class LaTeX extends UBPlugin {
    @Override
    public @Nullable MessageEmbed onCommand(String[] args, Message message) {
        return null;
    }
    
    @Override
    public void onRegister() {
        CommandRegistrar.register("tex", "core.tex", this);
        CommandRegistrar.registerAlias("tex", "latex");
        Manual.setHelpPage("tex", "LaTeX generator.\n" +
                                          "Usage: `tex <expression>`\n");
    }
}
