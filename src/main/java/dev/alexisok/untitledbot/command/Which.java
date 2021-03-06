package dev.alexisok.untitledbot.command;

import dev.alexisok.untitledbot.modules.basic.source.Source;
import dev.alexisok.untitledbot.plugin.UBPlugin;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

/**
 * Get the package and link to a command
 * 
 * @author AlexIsOK
 * @since 1.3
 */
public class Which extends UBPlugin {
    
    @Nullable
    @Override
    @Contract(pure = true)
    public MessageEmbed onCommand(@NotNull String[] args, @NotNull Message message) {
        EmbedBuilder eb = new EmbedBuilder();
        EmbedDefaults.setEmbedDefaults(eb, message);
        
        if(args.length == 1) {
            return new Source().onCommand(args, message);
        }
        
        if(!CommandRegistrar.hasCommand(args[1])) {
            eb.addField("Source", "I couldn't find the command " + args[1] + ", are you sure it exists?", false);
            eb.setColor(Color.RED);
            return eb.build();
        }
        
        Class<?> clazz = CommandRegistrar.getClassOfCommand(args[1]);
    
        eb.addField("Source",
                String.format("Package of %s is %s%n" +
                                      "[Source code for %s](https://github.com/untitledbot/untitled-bot/tree/master/src/main/java/%s.java)",
                        args[1],
                        clazz.getName().split("\\$")[0], //$ to escape lambda functions
                        clazz.getSimpleName().split("\\$")[0],
                        clazz.getName().replace(".", "/").split("\\$")[0]),
                false);
        return eb.build();
        
    }
    
    @Override
    public void onRegister() {
        CommandRegistrar.register("which", this);
        Manual.setHelpPage("which", "Get the name of the package where the command is located as well as the source code link.");
        CommandRegistrar.registerAlias("which", "sauce");
    }
}
