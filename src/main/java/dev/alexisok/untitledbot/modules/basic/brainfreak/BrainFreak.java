package dev.alexisok.untitledbot.modules.basic.brainfreak;

import dev.alexisok.untitledbot.command.CommandRegistrar;
import dev.alexisok.untitledbot.command.EmbedDefaults;
import dev.alexisok.untitledbot.command.Manual;
import dev.alexisok.untitledbot.plugin.UBPlugin;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Replace freak with a different word and you got the original name.
 * 
 * Handles the `brainfuck` command.
 * 
 * @author AlexIsOK
 * @since 1.3
 */
public final class BrainFreak extends UBPlugin {
    
    @NotNull
    @Override
    @Contract(pure = true)
    public MessageEmbed onCommand(@NotNull String[] args, @NotNull Message message) {
        EmbedBuilder eb = new EmbedBuilder();
        EmbedDefaults.setEmbedDefaults(eb, message);
        
        if(args.length == 1) {
            eb.addField("Brainfuck", "Please provide a string to parse :)\n" +
                                             "For help with the command, do `help brainfuck`", false);
            eb.setColor(Color.RED);
            return eb.build();
        }
        
        String returnString;
        
        //for stdin
        ArrayList<String> feedArray = new ArrayList<>(Arrays.asList(args));
        feedArray.remove(0);
        feedArray.remove(0);
        
        try {
            returnString = BFToNormalString.output(args[1], String.join("", feedArray));
        } catch(NoMoreStandardInputException ignored) {
            returnString = "Error: there was no more standard input to read!  Make sure the amount of commas run in the code\n" +
                                   "matches the amount of characters you input.";
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
                                                "Example: `brainfuck ++++++++[>++++[>++>+++>+++>+<<<<-]>+>+>->>+[<]<-]>>.>---.+++++++..+++.>>.<-.<.+++.------.--------.>>+.>++.`\n" +
                                                "\n" +
                                                "Note: the brainfuck code must be in one word, anything after a space will be recognized as input.");
    }
}
