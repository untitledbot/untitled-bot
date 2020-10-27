package dev.alexisok.untitledbot.modules.cache;

import dev.alexisok.untitledbot.BotClass;
import dev.alexisok.untitledbot.Main;
import dev.alexisok.untitledbot.command.CommandRegistrar;
import dev.alexisok.untitledbot.command.EmbedDefaults;
import dev.alexisok.untitledbot.command.Manual;
import dev.alexisok.untitledbot.logging.Logger;
import dev.alexisok.untitledbot.modules.starboard.Starboard;
import dev.alexisok.untitledbot.modules.starboard.StarboardHandle;
import dev.alexisok.untitledbot.plugin.UBPlugin;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jetbrains.annotations.NotNull;

/**
 * 
 * @author AlexIsOK
 * @since 1.3.22
 */
public final class VoidCache extends UBPlugin {
    
    @Override
    public @NotNull MessageEmbed onCommand(String[] args, @NotNull Message message) {
        EmbedBuilder eb = new EmbedBuilder();
        EmbedDefaults.setEmbedDefaults(eb, message);
        Logger.log("Voiding the cache.");

        Starboard.voidAllCache();
        BotClass.voidPrefixCache();
        
        eb.addField("Void", "Cache has been voided.  Expect a slowdown for the next few minutes.", false);
        return eb.build();
    }

    @Override
    public void onRegister() {
        CommandRegistrar.register("void", "owner", this);
        Manual.setHelpPage("void", "Void ALL of the caches.");
    }
}
