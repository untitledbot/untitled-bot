package dev.alexisok.untitledbot.plugin;

import dev.alexisok.untitledbot.command.Command;
import dev.alexisok.untitledbot.logging.Logger;
import org.jetbrains.annotations.NotNull;
import org.xeustechnologies.jcl.JarClassLoader;
import org.xeustechnologies.jcl.JclObjectFactory;
import org.xeustechnologies.jcl.context.DefaultContextLoader;
import org.xeustechnologies.jcl.proxy.CglibProxyProvider;
import org.xeustechnologies.jcl.proxy.ProxyProviderFactory;

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
    
    public static final String PLUGIN_DIRECTORY = "plugins/";
    
    public static void loadPlugins() {
        if(true)
            return;
        JarClassLoader jcl = new JarClassLoader();
        jcl.add(PLUGIN_DIRECTORY);

        ProxyProviderFactory.setDefaultProxyProvider(new CglibProxyProvider());

        JclObjectFactory factory = JclObjectFactory.getInstance(true);
        
        UBPlugin plugin = (UBPlugin) factory.create(jcl, "dev.alexisok.untitledbot.plugin.UBPlugin");
        plugin.onRegister();
    }
    
}
