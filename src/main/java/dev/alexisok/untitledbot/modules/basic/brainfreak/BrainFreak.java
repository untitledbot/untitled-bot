package dev.alexisok.untitledbot.modules.basic.brainfreak;

import dev.alexisok.untitledbot.command.CommandRegistrar;
import dev.alexisok.untitledbot.command.EmbedDefaults;
import dev.alexisok.untitledbot.command.Manual;
import dev.alexisok.untitledbot.plugin.UBPlugin;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jetbrains.annotations.Nullable;

/**
 * Replace freak with a different word and you got the original name.
 * 
 * Handles the `brainfuck` command.
 * 
 * @author AlexIsOK
 * @since 1.3
 */
public class BrainFreak extends UBPlugin {
    
    @Override
    public @Nullable MessageEmbed onCommand(String[] args, Message message) {
        EmbedBuilder eb = new EmbedBuilder();
        EmbedDefaults.setEmbedDefaults(eb, message);
        
        String returnString;
        
        try {
             returnString = BFToNormalString.output(message.getContentRaw());
        } catch(Exception ignored) {
            returnString = "Oops!  An error occurred!  Is the BF code correct?";
        }
        
        //more than 5000 just in case...
        if(returnString.length() > 5100)
            returnString = "Output exceeded 5000 characters...";
        
        eb.addField("", returnString, false);
        
        return eb.build();
    }
    
    @Override
    public void onRegister() {
        CommandRegistrar.register("brainfuck", "core.brainfuck", this);
        Manual.setHelpPage("brainfuck", "Brainfuck executor.\n\n" +
                                                "Usage: `brainfuck <code>`\n\n" +
                                                "[Help page](https://en.wikipedia.org/wiki/Brainfuck)\n\n" +
                                                "Example: `brainfuck ++++++++[>++++[>++>+++>+++>+<<<<-]>+>+>->>+[<]<-]>>.>---.+++++++..+++.>>.<-.<.+++.------.--------.>>+.>++.`");
    }
}
