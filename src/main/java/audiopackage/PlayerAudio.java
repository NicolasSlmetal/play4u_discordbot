package audiopackage;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;

public class PlayerAudio extends AudioSource {
    private AudioPlayerManager manager;
    public PlayerAudio(){
        AudioPlayer player = manager.createPlayer();
        TrackQueue track = new TrackQueue(player);
    }
}
