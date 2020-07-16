package dev.alexisok.untitledbot.plugin;

import dev.alexisok.untitledbot.logging.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * 
 * loads all of the plugins needed.
 * 
 * @author AlexIsOK
 * @since 1.0.1
 */
public class PluginLoader {
    
    public static final transient String PLUGIN_DIRECTORY = "./plugins/";
    public static final transient String PLUGIN_INFORMATION = "./plugin-info.txt";
    
    public static void loadPlugins() {
        try(BufferedReader br = new BufferedReader(new FileReader(new File(PLUGIN_INFORMATION)))) {
            ArrayList<String> pluginMainClasses = new ArrayList<>();
            
            String line;
            while((line = br.readLine()) != null) {
                pluginMainClasses.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private static void loadClassesToRegistrar(ArrayList<String> classes) {
        
        for(String s : classes) {
            try {
                ((UBPlugin) Class.forName(s).newInstance()).onRegister();
            } catch(ClassNotFoundException | InstantiationException | IllegalAccessException ignored) {
                Logger.critical("Error loading " + s + " as a plugin, is there a typo in the plugin-info.txt file?", 0, false);
            }
        }
    } 
    
}
