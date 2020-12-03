package dev.alexisok.untitledbot.data;

import dev.alexisok.untitledbot.BotClass;
import dev.alexisok.untitledbot.Main;
import dev.alexisok.untitledbot.command.CommandRegistrar;
import dev.alexisok.untitledbot.command.EmbedDefaults;
import dev.alexisok.untitledbot.logging.Logger;
import dev.alexisok.untitledbot.modules.vault.Vault;
import dev.alexisok.untitledbot.plugin.UBPlugin;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.io.File;
import java.io.FileFilter;
import java.net.URI;
import java.nio.file.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * Allow users to get their data.
 * All data is returned to users in a direct message.
 * 
 * The data is in text files and is sent to the users with one file per message.
 * They are rate-limited to getting their data to once/day.
 * 
 * @author AlexIsOK
 * @since 1.3
 */
public final class GetUserData extends UBPlugin {
    
    @Override
    public @NotNull MessageEmbed onCommand(String[] args, @NotNull Message message) {
    
        EmbedBuilder eb = new EmbedBuilder();
        EmbedDefaults.setEmbedDefaults(eb, message);
        
        eb.addField("Data", "Your data is being fetched.  It will be returned to you in a direct message.", false);
    
        Callable<Void> task = () -> {
            String userID = message.getAuthor().getId();
            
            if(isRateLimit(userID)) {
                message.getAuthor().openPrivateChannel().queue((channel) -> channel.sendMessage("Error: you have hit " +
                                                                                                        "the rate limit for getting " +
                                                                                                        "your data.  Please try again " +
                                                                                                        "in a day or so.").queue(
                        r -> BotClass.addToDeleteCache(message.getId(), r)
                ));
                return null;
            }
            
            ArrayList<File> dataFiles = new ArrayList<>();
            
            for(File f : new File(Main.DATA_PATH).listFiles()) {
                if(!f.isDirectory())
                    continue;
                for(File a : f.listFiles()) {
                    if(a.getName().equals(message.getAuthor().getId() + ".properties")) {
                        dataFiles.add(a);
                    }
                }
            }
            
            if(new File(Main.parsePropertiesLocation(userID, null)).exists()) {
                dataFiles.add(new File(Main.parsePropertiesLocation(userID, null)));
            }
            
            Map<String, String> e = new HashMap<>();
            e.put("create", "true");
            
            URI uri = URI.create("jar:file:/tmp/EXPORT_" + message.getId() + ".zip");
            
            try(FileSystem fs = FileSystems.newFileSystem(uri, e)) {
                for(File f : dataFiles) {
                    Path a = Paths.get(f.getAbsolutePath());
                    
                    String folder = f.getPath().split("/")[f.getPath().split("/").length - 2];
                    
                    Path z = fs.getPath("/" + folder);
                    Files.copy(a, z, StandardCopyOption.REPLACE_EXISTING);
                }
            }
            
            message.getAuthor().openPrivateChannel().queue(r -> {
                r.sendFile(new File("/tmp/EXPORT_" + message.getId() + ".zip")).queue(r2 -> {
                    new File("/tmp/EXPORT_" + message.getId() + ".zip").delete();
                });
            });
            
            setRateLimiter(userID);
            
            return null;
        };
        
        try {
            task.call();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return eb.build();
    }
    
    /**
     * Registers the `data` command.
     */
    @Override
    public void onRegister() {
        CommandRegistrar.register("data", "core.data", this);
    }
    
    /**
     * Checks if the user is currently under rate-limit.
     * 
     * @param userID the ID of the user.
     * @return true if there is a rate limit, false otherwise.
     */
    private static boolean isRateLimit(String userID) {
        String epochOldString = Vault.getUserDataLocal(userID, null, "data.ratelimit");
        
        if(epochOldString == null) return false;
        
        long epochPrevious = Long.parseLong(epochOldString);
        long epochCurrent  = Instant.now().getEpochSecond();
        
        return epochCurrent - epochPrevious <= 86400;
    }
    
    /**
     * Set the rate limit of the user so they can't spam the command.
     * @param userID the discord ID of the user.
     */
    private static void setRateLimiter(String userID) {
        Vault.storeUserDataLocal(userID, null, "data.ratelimit", String.valueOf(Instant.now().getEpochSecond()));
    }
}
