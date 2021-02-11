package dev.alexisok.untitledbot.command;

import dev.alexisok.untitledbot.BotClass;
import dev.alexisok.untitledbot.command.enums.UBPerm;
import dev.alexisok.untitledbot.plugin.UBPlugin;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

public final class DisableCategory extends UBPlugin {
    
    @Override
    public @Nullable MessageEmbed onCommand(String[] args, @NotNull Message message) {
        EmbedBuilder eb = new EmbedBuilder();
        EmbedDefaults.setEmbedDefaults(eb, message);
        
        if(args.length == 1) {
            eb.addField("untitled-bot", String.format("Usage: `disable-category <category>`\nUsing the command again enables the category.\nYou can see a list of categories by using `%shelp`", BotClass.getPrefixNice(message.getGuild().getId())), false);
            eb.setColor(Color.RED);
            return eb.build();
        }
        
        return null;
    }
    
    @Override
    public void onRegister() {
        CommandRegistrar.register("disable-category", UBPerm.MANAGE_SERVER,  this);
        Manual.setHelpPage("disable-category", "Disable a category for this server.\n" +
                "Usage: `disable-category <category>`\n" +
                "Use the command again to toggle the category back on.");
        CommandRegistrar.registerAlias("disable-category", "enable-category");
    }
}
