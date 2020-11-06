package dev.alexisok.untitledbot.util;

import dev.alexisok.untitledbot.Main;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import org.jetbrains.annotations.Contract;

import java.util.Objects;

/**
 * @author AlexIsOK
 * @since 1.3.23
 */
public class LoadableMessageObject implements Loadable {
    
    private Message loadedMessage;
    
    public LoadableMessageObject(Message m) {
        this.loadedMessage = m;
    }
    
    @Override
    @Contract
    public void load(String load) {
        String[] data = load.split(";");
        TextChannel tc = Main.jda.getTextChannelById(data[0]);
        Objects.requireNonNull(tc).retrieveMessageById(data[1]).queue(r -> {
            this.loadedMessage = r;
        });
    }
    
    @Override
    @Contract(pure = true)
    public String store() {
        return this.loadedMessage.getChannel().getId() + ";" + this.loadedMessage.getId();
    }
    
    @Override
    @Contract(pure = true)
    public String toString() {
        return this.loadedMessage.getChannel().getId() + ";" + this.loadedMessage.getId();
    }
    
    @Contract(pure = true)
    public Message getMessage() {
        return this.loadedMessage;
    }
}
