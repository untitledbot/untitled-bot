package dev.alexisok.untitledbot.plugin;

import dev.alexisok.untitledbot.Main;
import dev.alexisok.untitledbot.logging.Logger;
import org.jetbrains.annotations.NotNull;
import org.xeustechnologies.jcl.JarClassLoader;

import java.io.*;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * @author AlexIsOK
 * @since 0.0.1
 */
public class PluginLoader {
    
    private static boolean hasLoaded = false;
    
    private static final ArrayList<String> PLUGIN_CLASSES = new ArrayList<>();
    
    //load all plugin main classes
    static {
        try(BufferedReader b = new BufferedReader(new FileReader("pluginClasses.txt"))) {
            String currentLine;
            while((currentLine = b.readLine()) != null)
                PLUGIN_CLASSES.add(currentLine);
        } catch (IOException e) {
            e.printStackTrace();
            Logger.critical("Could not find the pluginClasses.txt file!  Read the GitHub wiki page on this " +
                                    "repository for more information.", 12, true);
        }
    }
    
    /**
     * Load ALL the plugins.  This should NOT be run more than once.
     */
    public static void loadPlugins() {
        if(hasLoaded)
            Logger.critical("PLUGIN LOADER HAS ALREADY BEEN CALLED.  THIS IS A PROBLEM!", 11, true);
        hasLoaded = true;
        Arrays.stream(new File("./plugins/")
                .listFiles())
                .filter(f -> !f.isDirectory())
                .filter(f -> f.getName().endsWith(".jar"))
                .forEachOrdered(PluginLoader::loadJAR);
        hasLoaded = true;
    }
    
    private static void loadJAR(@NotNull File f) {
        JarClassLoader jcl = new JarClassLoader();
        jcl.add(f);
    }
    
}
