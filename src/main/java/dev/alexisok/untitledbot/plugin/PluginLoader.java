package dev.alexisok.untitledbot.plugin;

import dev.alexisok.untitledbot.command.Command;
import dev.alexisok.untitledbot.logging.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
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
        try(BufferedReader br = new BufferedReader(new FileReader((PLUGIN_INFORMATION)))) {
            ArrayList<String> pluginMainClasses = new ArrayList<>();
            
            String line;
            while((line = br.readLine()) != null) {
                if(line.equals(""))
                    continue;
                Logger.log("Trying to load " + line + " as a plugin.");
                pluginMainClasses.add(line);
            }
            
            loadClassesToRegistrar(pluginMainClasses);
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
    
    private static void loadClassesToRegistrar(@NotNull ArrayList<String> classes) {
        for(String s : classes) {
            try {
                Logger.log("Loading " + s + " as an untitled-bot plugin.");
                type(s).onRegister();
            } catch(ClassNotFoundException e) {
                e.printStackTrace();
                Logger.critical("Error loading package " + s + " as a plugin, is there a typo in the plugin-info.txt file?", 0, false);
            } catch(Throwable t) {
                t.printStackTrace();
            }
        }
    }
    
    private static <E extends UBPlugin> E type(String packName) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        ClassLoader.getSystemClassLoader().loadClass(packName).newInstance();
        return (E) Class.forName(packName).newInstance();
    }
    
}
