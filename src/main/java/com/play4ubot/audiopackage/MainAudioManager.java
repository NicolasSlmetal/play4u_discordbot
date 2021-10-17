package com.play4ubot.audiopackage;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;

public class MainAudioManager {
    private final AudioPlayer player;
    private final TrackQueue trackQueue;
    private AudioSend audioSend;
    public MainAudioManager(AudioPlayerManager manager){
        this.player = manager.createPlayer();
        this.trackQueue = new TrackQueue(this.player);
        this.player.addListener(this.trackQueue);
        this.audioSend = new AudioSend(this.player) {
        };

        }

    public AudioSend getAudioSend() {
        return audioSend;
    }

    public void setAudioSend(AudioSend audioSend) {
        this.audioSend = audioSend;
    }

    public AudioPlayer getPlayer() {
        return player;
    }

    public TrackQueue getTrackQueue() {
        return trackQueue;
    }
}

