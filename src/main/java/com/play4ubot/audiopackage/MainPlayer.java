package com.play4ubot.audiopackage;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import java.awt.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

public class MainPlayer{
    private static MainPlayer ITEM;
    private static HashMap<Guild,String> name_music = new HashMap<>();
    private static HashMap<Guild, Boolean> playing = new HashMap<>();
    private static HashMap<Guild, Boolean> paused = new HashMap<>();
    private static HashMap<Guild, Boolean> loop = new HashMap<>();
    private final HashMap<Long, MainAudioManager> musicMapManagers;
    private final AudioPlayerManager manager;
    public MainPlayer(){
        this.musicMapManagers = new HashMap<>();
        this.manager = new DefaultAudioPlayerManager();
        AudioSourceManagers.registerRemoteSources(this.manager);
        AudioSourceManagers.registerLocalSource(this.manager);
    }
    public EmbedBuilder setMusicEmbed(String url, String title, String duration, String time) {
        String name;
        if (Files.exists(Paths.get(url))) {
            name = url.split("audiofiles")[url.split("audiofiles").length - 1];
            name = name.substring(1);
        } else {
            name = url;
        }
        return new EmbedBuilder()
                .setColor(new Color(0x0B2DDE))
                .setTitle(String.format("%s ", title))
                .setDescription(name + "\nDuração: " + duration + "\nTempo até reproduzir: " + time);
    }
    public EmbedBuilder setMusicEmbed(String url, String title, String duration) {
        String name;
        if (Files.exists(Paths.get(url))) {
            name = url.split("audiofiles")[url.split("audiofiles").length - 1];
            name = name.substring(1);
        } else {
            name = url;
        }
        return new EmbedBuilder()
                .setColor(new Color(0x0B2DDE))
                .setTitle(String.format("%s ", title))
                .setDescription(name + "\nDuração: " + duration);
    }
    public String sendDuration(long milisecs){
        float seconds = milisecs / 1000;
        double min = (double) seconds / 60;
        seconds = (float) (min - Math.floor(min)) * 60;
        int minutes = (int) min;
        String current;
        if (seconds >= 60){
            minutes++;
            seconds = 0;
        }
        if (seconds < 10){
            current = String.format("%d:0%.0f", minutes, seconds);
        } else {
            current = String.format("%d:%.0f", minutes, seconds);
        }
        return  current;
    }
    public MainAudioManager getMusicManager(Guild g){
        return this.musicMapManagers.computeIfAbsent(g.getIdLong(), (guildID) -> {
        final MainAudioManager guildManager = new MainAudioManager(this.manager);
        g.getAudioManager().setSendingHandler(guildManager.getAudioSend());
        return guildManager;
        });
    }
    public void loadPlay(TextChannel textCh, String trackURL){
        final MainAudioManager main = this.getMusicManager(textCh.getGuild());
        this.manager.loadItemOrdered(main, trackURL, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                MainPlayer.getITEM().sendDuration(track.getDuration());
                MainPlayer.setPaused(false, textCh.getGuild());
                String title = MainPlayer.isPlaying().get(textCh.getGuild())?"Adicionado na fila":"Tocando";
                main.getTrackQueue().queuePlaylist(track);
                EmbedBuilder embed;
                if (!track.getInfo().title.equalsIgnoreCase("Unknown Title")){
                    MainPlayer.getName_music().replace(textCh.getGuild(), track.getInfo().title);
                }
                if (!MainPlayer.isPlaying().get(textCh.getGuild())) {
                    MainPlayer.isPlaying().put(textCh.getGuild(), true);
                    embed = setMusicEmbed(MainPlayer.getName_music().get(textCh.getGuild()),
                            title, sendDuration(track.getDuration()));
                } else {
                    long timeActual = main.getPlayer().getPlayingTrack().getPosition();
                    long timeToEnd = main.getPlayer().getPlayingTrack().getDuration();
                    long totalTime = timeToEnd - timeActual;
                    for (AudioTrack t : main.getTrackQueue().getPlaylist()){
                        if (t != track && t != main.getPlayer().getPlayingTrack()){
                            totalTime += t.getDuration();
                        }
                    }
                    String timeToNext = sendDuration(totalTime);
                    embed = setMusicEmbed(MainPlayer.getName_music()
                                    .get(textCh.getGuild()), title, sendDuration(track.getDuration()),
                            timeToNext);
                }
                textCh.sendMessageEmbeds(embed.setAuthor(textCh.getJDA().getSelfUser().getName())
                        .setThumbnail(textCh.getJDA().getSelfUser().getAvatarUrl()).build()).queue();
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                trackLoaded(playlist.getTracks().get(0));
            }

            @Override
            public void noMatches() {
                textCh.sendMessage("Não encontrei a música :cry:").queue();
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                exception.printStackTrace();
                textCh.sendMessage("Não consegui reproduzir a música :cry:").queue();
            }
        });
    }
    public static MainPlayer getITEM() {
        if (ITEM == null){
            ITEM = new MainPlayer();
        }
        return ITEM;
    }

    public static HashMap<Guild, String> getName_music() {
        return name_music;
    }

    public static void setName_music(HashMap<Guild, String> name_music) {
        MainPlayer.name_music = name_music;
    }

    public static void setPlaying(HashMap<Guild, Boolean> playing) {
        MainPlayer.playing = playing;
    }

    public static HashMap<Guild, Boolean> isPlaying() {
        return playing;
    }

    public static HashMap<Guild, Boolean> isPaused() {
        return paused;
    }

    public static void setPaused(boolean option, Guild g) {
        MainPlayer.getITEM().getMusicManager(g).getPlayer().setPaused(option);
        MainPlayer.isPaused().replace(g, option);
    }

    public AudioPlayerManager getManager() {
        return manager;
    }

    public static void setITEM(MainPlayer ITEM) {
        MainPlayer.ITEM = ITEM;
    }

    public static HashMap<Guild, Boolean> isLoop() {
        return loop;
    }

    public static void setLoop(Guild g) {
        if (MainPlayer.isLoop().get(g)) {
            MainPlayer.isLoop().replace(g, false);
        } else {
            MainPlayer.isLoop().replace(g, true);
            if (MainPlayer.isPlaying().get(g)) {
                AudioTrack track = MainPlayer.getITEM().getMusicManager(g).getPlayer().getPlayingTrack().makeClone();
                System.out.println(MainPlayer.getITEM().getMusicManager(g).getTrackQueue().getPlaylist().add(track));
            }
        }
    }
    public HashMap<Long, MainAudioManager> getMusicMapManagers() {
        return musicMapManagers;
    }

}

