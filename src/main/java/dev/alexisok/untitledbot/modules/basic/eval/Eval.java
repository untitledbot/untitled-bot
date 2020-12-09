package dev.alexisok.untitledbot.modules.basic.eval;

import dev.alexisok.untitledbot.command.CommandRegistrar;
import dev.alexisok.untitledbot.command.EmbedDefaults;
import dev.alexisok.untitledbot.command.Manual;
import dev.alexisok.untitledbot.command.enums.UBPerm;
import dev.alexisok.untitledbot.plugin.UBPlugin;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.util.Arrays;

/**
 * @author AlexIsOK
 * @since 1.3.22
 */
public class Eval extends UBPlugin {
    
    @Override
    public @Nullable MessageEmbed onCommand(String[] args, @NotNull Message message) {
        EmbedBuilder eb = new EmbedBuilder();
        EmbedDefaults.setEmbedDefaults(eb, message);
        
        if(args.length < 3) {
            eb.addField("Eval", "Please select an engine:\n" +
                                        "js", false);
            return eb.build();
        }
        
        ScriptEngine e = new ScriptEngineManager().getEngineByName(args[1]);
        eb.setTitle("Eval return");
        ArrayUtils.shift(args, 2);
        
        try {
            eb.setDescription((CharSequence) e.eval(Arrays.toString(args)));
        } catch(Throwable ignored) {
            eb.addField("Error", "Error occurred while doing eval.", false);
        }
        return eb.build();
    }
    
    @Override
    public void onRegister() {
        CommandRegistrar.register("eval", UBPerm.OWNER, this);
        Manual.setHelpPage("eval", "Eval command");
    }
}
