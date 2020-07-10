package dev.alexisok.untitledbot.plugin;

import dev.alexisok.untitledbot.logging.Logger;
import org.jetbrains.annotations.NotNull;
import org.xeustechnologies.jcl.JarClassLoader;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

/**
 * 
 * loads all of the plugins needed.
 * 
 * @author AlexIsOK
 * @since 0.0.1
 */
public class PluginLoader {
    
    private static boolean hasLoaded = false;
    
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
    
    /**
     * Load all class files to a JAR.
     * @param f the file.
     */
    private static void loadJAR(@NotNull File f) {
        //i have to admit i couldn't find this anywhere so i just kind of made this
        //from eight different sites including the javadoc.  this took WAY too long to make.
        try {
            JarEntry je;
            JarInputStream jarStream = new JarInputStream(new FileInputStream(f));
            while(null != (je = jarStream.getNextJarEntry())) {
                if(!je.getName().endsWith(".class"))
                    continue;
                String name = je.getName().replaceAll("/", "\\.").replace(".class", "");
                Logger.log("Loading " + name + " into memory from " + f.getName());
                ClassLoader.getSystemClassLoader().setClassAssertionStatus(name, true);
                ClassLoader.getSystemClassLoader().loadClass(name);
                
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            Logger.critical("There was an error loading the JAR file " + f.getName() +
                                    "!\nYou may want to report this to the plugin author.",
                    0,
                    false);
        }
    }
    
}
