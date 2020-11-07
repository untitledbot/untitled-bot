package dev.alexisok.untitledbot.modules.music.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.playback.MutableAudioFrame;
import net.dv8tion.jda.api.audio.AudioSendHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.Buffer;
import java.nio.ByteBuffer;

/**
 * @author AlexIsOK
 * @since 1.3.23
 */
public final class AudioHandler implements AudioSendHandler {
    
    private final AudioPlayer player;
    private final ByteBuffer buff;
    private final MutableAudioFrame maf;
    
    public AudioHandler(@NotNull AudioPlayer player) {
        this.player = player;
        this.buff = ByteBuffer.allocate(4096);
        this.maf = new MutableAudioFrame();
        this.maf.setBuffer(buff);
    }
    
    @Override
    public boolean canProvide() {
        return this.player.provide(maf);
    }
    
    @Override
    public @NotNull ByteBuffer provide20MsAudio() {
        return (ByteBuffer) this.buff.flip();
    }
    
    @Override
    public boolean isOpus() {
        return true;
    }
}
