package com.play4ubot.audiopackage;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEvent;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import net.dv8tion.jda.api.entities.Guild;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class TrackQueue extends AudioSource{
    private final AudioPlayer player;
    private BlockingQueue<AudioTrack> playlist;
    private static Set<Guild> guilds = new HashSet<>();
    private AudioTrack firstInLoop;

    public TrackQueue(AudioPlayer player){
        this.player = player;
        this.playlist = new LinkedBlockingQueue<>();
        }
    public void nextTrack() {
        this.getPlayer().startTrack(this.getPlaylist().poll(), false);
        }

    public void queuePlaylist(AudioTrack track){
        if (!this.player.startTrack(track, true)){
                boolean offer = this.playlist.offer(track);
            }
        }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        if (this.getPlaylist().isEmpty() && this.getPlayer().getPlayingTrack() == null){
            for (Guild g : getGuilds()){
                if (MainPlayer.isPlaying().get(g)){
                    MainPlayer.isPlaying().replace(g, false);
                    break;
                }
            }
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
        for (Guild g: TrackQueue.getGuild()){
            if (MainPlayer.isLoop().get(g)){
                AudioTrack trackClone = track.makeClone();
                this.getPlaylist().add(trackClone);
                if (this.getPlaylist().size() == 1) {
                    this.firstInLoop = trackClone;
                }
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

    public static Set<Guild> getGuilds() {
        return guilds;
    }

    public static void setGuilds(Set<Guild> guilds) {
        TrackQueue.guilds = guilds;
    }

    public AudioTrack getFirstInLoop() {
        return firstInLoop;
    }

    public void setFirstInLoop(AudioTrack firstInLoop) {
        this.firstInLoop = firstInLoop;
    }
}
