package com.play4ubot.audiopackage;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEvent;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import net.dv8tion.jda.api.entities.Guild;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class TrackQueue extends AudioSource{
    private final AudioPlayer player;
    private BlockingQueue<AudioTrack> playlist;
    private static Set<Guild> guilds = new HashSet<>();

    public TrackQueue(AudioPlayer player){
        this.player = player;
        this.playlist = new LinkedBlockingQueue<>();
        }
    public void nextTrack() {
        this.player.startTrack(this.getPlaylist().poll(), false);
        }

    public void queuePlaylist(AudioTrack track){
        if (!this.player.startTrack(track, true)){
                boolean offer = this.playlist.offer(track);
            }
        }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
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
        for (Guild g: TrackQueue.getGuild()){
            if (MainPlayer.isLoop().get(g) && MainPlayer.getITEM().getMusicManager(g).getPlayer().getPlayingTrack() == track){
                this.getPlaylist().add(track.makeClone());
                break;
            }
        }
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

    public AudioPlayer getPlayer() {
        return player;
    }

    public BlockingQueue<AudioTrack> getPlaylist() {
        return playlist;
    }

    public void setPlaylist(BlockingQueue<AudioTrack> playlist) {
        this.playlist = playlist;
    }


    public static Set<Guild> getGuild() {
        return TrackQueue.guilds;
    }

    public static void setGuild(Set<Guild> guild) {
        TrackQueue.guilds = guild;
    }
}
