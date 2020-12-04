package dev.alexisok.untitledbot.modules.music;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManager;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.*;
import dev.alexisok.untitledbot.Main;
import dev.alexisok.untitledbot.logging.Logger;
import dev.alexisok.untitledbot.modules.music.audio.MusicManager;
import dev.alexisok.untitledbot.util.ShutdownHook;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.util.*;

import static dev.alexisok.untitledbot.modules.music.MusicKernel.INSTANCE;

/**
 * Saves the queue in a temp file in case the bot ever disconnects.
 * 
 * Will auto-join voice channels as well.
 * 
 * This is added as a shutdown hook in {@link MusicKernel}
 * 
 * @author AlexIsOK
 * @since 1.3.24
 */
public final class SaveQueue implements ShutdownHook {
    
    private static final File SHUTDOWN_DIR = new File("./shutdown/");
    
    static {
        try {
            FileUtils.forceMkdir(SHUTDOWN_DIR);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Load the queue to the kernel.
     * 
     * Yes, I know it says save queue and it loads the queue, don't think about it too much.
     */
    protected SaveQueue() {
        Arrays.stream(SHUTDOWN_DIR.listFiles()).forEach(file -> {
            
            if(file.getName().contains(".gitignore"))
                return;
            
            String vcID = file.getName().split("\\.")[0].split("_")[0];
            String tcID = file.getName().split("\\.")[0].split("_")[1];
            Properties p = new Properties();
            try {
                p.load(new FileReader(file.getAbsoluteFile()));
                VoiceChannel vc = Main.jda.getVoiceChannelById(vcID);
                TextChannel tc  = Main.jda.getTextChannelById(tcID);
                
                assert vc != null;
                assert tc != null;
                
                //pos, uri
                Map<Integer, String> queue = new HashMap<>();
                
                p.forEach((o1, o2) -> queue.put(Integer.parseInt(o1.toString()), o2.toString()));
                
                Map<Integer, String> sortedQueue = new TreeMap<>(queue);
                
                sortedQueue.forEach((i, s) -> INSTANCE.loadAndPlay(tc, s, vc));
                
                INSTANCE.setLast(tc.getGuild().getId(), tc);
                
            } catch(Exception e) {
                e.printStackTrace();
            } catch(AssertionError e) {
                Logger.critical("A voice channel to load was null!");
                e.printStackTrace();
            }
            
            if(!file.delete()) {
                Logger.critical("Could not delete a shutdown file!");
            }
        });
        
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                MusicKernel.setStillLoading(false);
            }
        }, 10000L);
    }
    
    @Override
    public void onShutdown() {
        
        Map<String, String> current = INSTANCE.getCurrentlyPlaying();
        Map<String[], List<String>> queues = INSTANCE.getQueues();
        
        queues.forEach((s, l) -> {
            if(current.containsKey(s[0]))
                l.add(0, current.get(s[0]));
            Logger.debug("foreach");
            Properties p = new Properties();
            try {
                for(int i = 0; i < l.size(); i++) {
                    Logger.debug(i + ": " + l.get(i));
                    p.put(String.valueOf(i), l.get(i));
                }
                p.store(new FileOutputStream(SHUTDOWN_DIR + "/" + s[0] + "_" + s[1] + ".properties"), "new Date().toString()");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        
    }
    
}
