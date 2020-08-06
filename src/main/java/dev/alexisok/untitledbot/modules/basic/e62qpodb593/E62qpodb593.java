package dev.alexisok.untitledbot.modules.basic.e62qpodb593;

import dev.alexisok.untitledbot.command.CommandRegistrar;
import dev.alexisok.untitledbot.command.Manual;
import dev.alexisok.untitledbot.plugin.UBPlugin;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jetbrains.annotations.Nullable;

/**
 * @author AlexIsOK
 * @since 1.3
 */
public class E62qpodb593 extends UBPlugin {
    
    private static final char LOOP_START = 'e'; //start the loop
    private static final char COPY       = 'z'; //copy current byte to clipboard
    private static final char INCREMENT  = 'q'; //increment current position on stack by one
    private static final char DECREMENT  = 'p'; //decrement current position on stack by one
    private static final char POINTER_L  = 'b'; //move the pointer to the left
    private static final char POINTER_R  = 'd'; //move the pointer to the right
    private static final char OUTPUT     = 'o'; //output the current byte
    private static final char PASTE      = '2'; //set the current byte to the clipboard
    private static final char LOOP_END   = '3'; //end the loop
    
    private static final char CLIPBOARD_IF_LO = '9'; //decrement if lower than clipboard
    private static final char CLIPBOARD_IF_UP = '6'; //increment if lower than clipboard
    
    private static final char DASH_UP = 'y'; //increase the value at pointer by 5
    private static final char DASH_DOWN = 'k'; //same thing as above but minus 5
    
    private static final char GOTO_HOME = '5'; //goto the HOME address
    
    private static final char ACTION_POINTER_ZERO = '0'; //set the value of the pointer pos to 0
    private static final char ACTION_POINTER_ADDR_64 = 't'; //set the stack value of pointer pos to 64 (@)
    
    //address for the amount of times to loop through
    private static final int LOOP_TIMES_ADDR = 4;
    
    private static final int HOME_ADDRESS = 0;
    
    @Override
    public @Nullable MessageEmbed onCommand(String[] args, Message message) {
        final byte[] STACK = new byte[65536];
        int loopTimes = 0;
        int loopTimesCurrent = 0;
        int gotoMark = 0;
        int pointer = 0;
        byte clipboard = 0;
        return null;
    }
    
    @Override
    public void onRegister() {
        CommandRegistrar.register("e62qpodb593", this);
        Manual.setHelpPage("e62qpodb593", "run e62qpodb593 code (https://github.com/alexisok/e62qpodb593)");
        CommandRegistrar.registerAlias("e62qpodb593", "e62", "593");
    }
}
