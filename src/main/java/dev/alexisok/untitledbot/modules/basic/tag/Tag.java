package dev.alexisok.untitledbot.modules.basic.tag;

import dev.alexisok.untitledbot.command.CommandRegistrar;
import dev.alexisok.untitledbot.command.EmbedDefaults;
import dev.alexisok.untitledbot.command.Manual;
import dev.alexisok.untitledbot.plugin.UBPlugin;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.intellij.lang.annotations.RegExp;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

/**
 * @author AlexIsOK
 * @since 1.4.2
 */
public class Tag extends UBPlugin {
    
    @RegExp
    private static final String TAG_REGEX = "^[a-z0-9_-]";
    
    @Override
    public @Nullable MessageEmbed onCommand(String[] args, @NotNull Message message) {
        EmbedBuilder eb = new EmbedBuilder();
        EmbedDefaults.setEmbedDefaults(eb, message);
        
        if(args.length == 1) {
            return eb.setTitle("Tag")
                    .setDescription("Usage:\n" +
                            "```" +
                            "tag create <tagname> <tag description>\n" +
                            "tag delete <tagname>\n" +
                            "tag <tagname>\n" +
                            "```")
                    .setColor(Color.RED)
                    .build();
        }
        
        switch(args[1].toLowerCase()) {
            case "create": {
                if(!message.getMember().hasPermission(Permission.MESSAGE_MANAGE)) {
                    return noPerms(eb);
                }
                
            }
        }
        return null;
    }
    
    @Contract(mutates = "param1")
    private static MessageEmbed noPerms(@NotNull EmbedBuilder eb) {
        return eb.setTitle("Tag")
                .setDescription("You must have the permission \"Manage Messages\" to create or delete tags.")
                .setColor(Color.RED)
                .build();
    }
    
    @Override
    public void onRegister() {
        CommandRegistrar.register("tag", this);
        Manual.setHelpPage("tag", "\n" +
                "Usage: `tag `");
        CommandRegistrar.registerAlias("tag", "t");
    }
}
