package dev.alexisok.untitledbot.modules.basic.casetoggle;

import dev.alexisok.untitledbot.command.CommandRegistrar;
import dev.alexisok.untitledbot.command.EmbedDefaults;
import dev.alexisok.untitledbot.command.Manual;
import dev.alexisok.untitledbot.plugin.UBPlugin;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Command `caps` and stuff
 * 
 * @author AlexIsOK
 * @since 1.3.21
 */
public final class Uppercase extends UBPlugin {
    
    @Override
    public @NotNull MessageEmbed onCommand(String[] args, @NotNull Message message) {
        EmbedBuilder eb = new EmbedBuilder();
        EmbedDefaults.setEmbedDefaults(eb, message);
        
        eb.addField("UPPERCASE", String.join(" ", ArrayUtils.remove(args, 0)).toUpperCase(), false);
        
        return eb.build();
    }
    
    @Override
    public void onRegister() {
        CommandRegistrar.register("caps", this);
        Manual.setHelpPage("caps", "Convert a string to be ALL UPPERCASE LETTERS");
        CommandRegistrar.registerAlias("caps", "uppercase");
    }
}
