package com.play4ubot.audiopackage;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEvent;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;

import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class TrackQueue extends AudioSource{
    private final AudioPlayer player;
    private BlockingQueue<AudioTrack> playlist;
    private static ArrayList<AudioTrack> tracks;

    public TrackQueue(AudioPlayer player){
        this.player = player;
        this.playlist = new LinkedBlockingQueue<>();
        tracks = new ArrayList<>();
        }
    public void nextTrack() {
        this.player.startTrack(this.getPlaylist().poll(), false);
        if (MainPlayer.isLoop() && this.getPlaylist().isEmpty()){
            tracks.forEach(t -> System.out.println(t.getInfo().title));
            this.getPlaylist().addAll(tracks);
            tracks.clear();
        }
        }

    public void queuePlaylist(AudioTrack track){
        if (!this.player.startTrack(track, true)){
                boolean offer = this.playlist.offer(track);
            }
        }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        if (MainPlayer.isLoop()){
            TrackQueue.getTracks().add(track.makeClone());
        }
        if (this.playlist.isEmpty() && !MainPlayer.isLoop()){
            MainPlayer.setPlaying(false);
        }
        if (endReason.mayStartNext){
            this.nextTrack();
            }
            }


    @Override
    public void onPlayerPause(AudioPlayer player) {
        super.onPlayerPause(player);
    }

    @Override
    public void onPlayerResume(AudioPlayer player) {
        super.onPlayerResume(player);
    }

    @Override
    public void onTrackStart(AudioPlayer player, AudioTrack track) {
        super.onTrackStart(player, track);
    }

    @Override
    public void onTrackException(AudioPlayer player, AudioTrack track, FriendlyException exception) {
        super.onTrackException(player, track, exception);
    }

    @Override
    public void onTrackStuck(AudioPlayer player, AudioTrack track, long thresholdMs) {
        super.onTrackStuck(player, track, thresholdMs);
    }

    @Override
    public void onTrackStuck(AudioPlayer player, AudioTrack track, long thresholdMs, StackTraceElement[] stackTrace) {
        super.onTrackStuck(player, track, thresholdMs, stackTrace);
    }

    @Override
    public void onEvent(AudioEvent event) {
        super.onEvent(event);
    }
    public static ArrayList<AudioTrack> getTracks(){
        return TrackQueue.tracks;
    }
    public static void addTracksToLoop(AudioTrack track){
        TrackQueue.tracks.add(track);
    }

    public AudioPlayer getPlayer() {
        return player;
    }

    public BlockingQueue<AudioTrack> getPlaylist() {
        return playlist;
    }

    public void setPlaylist(BlockingQueue<AudioTrack> playlist) {
        this.playlist = playlist;
    }

    public static void setTracks(ArrayList<AudioTrack> tracks) {
        TrackQueue.tracks = tracks;
    }
}
