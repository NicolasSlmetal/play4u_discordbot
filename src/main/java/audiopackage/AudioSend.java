package audiopackage;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.playback.MutableAudioFrame;
import net.dv8tion.jda.api.audio.AudioSendHandler;

import java.nio.ByteBuffer;

public class AudioSend implements AudioSendHandler {
    private final AudioPlayer provider;
    private final MutableAudioFrame frame;
    private final ByteBuffer buffer;
    public AudioSend(AudioPlayer p){
        this.provider = p;
        this.buffer = ByteBuffer.allocate(1024);
        this.frame = new MutableAudioFrame();
        this.frame.setBuffer(this.buffer);
    }
    @Override
    public boolean canProvide() {
        return this.provider.provide(frame);
    }

    @Override
    public ByteBuffer provide20MsAudio() {
        return (ByteBuffer) this.buffer.flip();
    }

    @Override
    public boolean isOpus() {
        return true;
    }
}
