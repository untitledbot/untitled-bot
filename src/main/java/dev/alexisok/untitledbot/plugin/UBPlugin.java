package dev.alexisok.untitledbot.plugin;

import dev.alexisok.untitledbot.command.Command;

/**
 * untitled-bot plugin.  Plugins and modules must be a subclass
 * of this class in order to work.
 * 
 * @author AlexIsOK
 * @since 0.0.1
 */
public abstract class UBPlugin implements Command {
    
    /**
     * Run when the bot is started (after registration).
     */
    public void onStartup() {}
    
    /**
     * Run when the plugin or module is being registered.  This
     * is where things like permissions and command should be
     * registered, as this will only be called once.
     */
    public void onRegister() {}
    
}
