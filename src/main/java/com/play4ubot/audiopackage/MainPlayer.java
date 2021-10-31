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
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;

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
        float seconds = (float) milisecs / 1000;
        double min = (double) seconds / 60;
        seconds = (float) (min - Math.floor(min)) * 60;
        int minutes = (int) min;
        double hour = 0;
        if (minutes >= 60){
            hour = (double) minutes/60;
            min = (hour - Math.floor(hour)) * 60;
            minutes = (int) min;
        }
        String current;
        if (seconds >= 60){
            minutes++;
            seconds = 0;
        }
        if (Math.round(seconds) < 10){
            current = String.format("%d:0%.0f", minutes, seconds);
        } else {
            current = String.format("%d:%.0f", minutes, seconds);
        }
        current = hour != 0? String.format("%.0f:%s", hour, minutes < 10?String.format("0%d:%.0f", minutes, seconds):current):current;
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
                List<AudioTrack> old = new ArrayList<>();
                if (MainPlayer.isLoop().get(textCh.getGuild()) && main.getTrackQueue().getPlaylist().size() > 0){
                    ArrayList<AudioTrack> reversed = new ArrayList<>(main.getTrackQueue().getPlaylist());
                    if (!reversed.get(reversed.size() - 1).getInfo().title.equalsIgnoreCase(main.getTrackQueue().getFirstInLoop().getInfo().title)){
                        for(int c=0;c< reversed.size();c++){
                            if (reversed.get(c).getInfo().title.equalsIgnoreCase(main.getTrackQueue().getFirstInLoop().getInfo().title))
                                if (c == 0){
                                    old.addAll(reversed);
                                    break;
                                } else{
                                    for(int i=c; i< reversed.size();i++){
                                        old.add(reversed.get(i));
                                    }
                                    break;
                                }
                        }
                    } else {
                        old.add(reversed.get(reversed.size() - 1));
                    }
                    for (AudioTrack t: old) {
                        reversed.remove(t);
                    }
                    main.getTrackQueue().getPlaylist().clear();
                    main.getTrackQueue().getPlaylist().addAll(reversed);
                }
                MainPlayer.setPaused(false, textCh.getGuild());
                String title = MainPlayer.isPlaying().get(textCh.getGuild()) ? "Adicionado na fila" : "Tocando";
                main.getTrackQueue().queuePlaylist(track);
                EmbedBuilder embed;
                if (!track.getInfo().title.equalsIgnoreCase("Unknown Title")) {
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
                    for (AudioTrack t : main.getTrackQueue().getPlaylist()) {
                        if (t != track && t != main.getPlayer().getPlayingTrack()) {
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
                if (!old.isEmpty()){
                    for (AudioTrack t: old){
                        main.getTrackQueue().getPlaylist().add(t);
                    }
                }
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
            if (!MainPlayer.getITEM().getMusicManager(g).getTrackQueue().getPlaylist().isEmpty()){
                ArrayList<AudioTrack> reversed = new ArrayList<>(MainPlayer.getITEM().getMusicManager(g).getTrackQueue().getPlaylist());
                ArrayList <AudioTrack> old = new ArrayList<>();
                if (reversed.get(reversed.size() - 1).getInfo().title.equals(MainPlayer.getITEM().getMusicManager(g).getTrackQueue().getFirstInLoop().getInfo()
                        .title)){
                    old.add(reversed.get(reversed.size() - 1));
                } else{
                    for (int c=0;c< reversed.size();c++){
                        if (reversed.get(c).getInfo().title.equals(MainPlayer.getITEM().getMusicManager(g).getTrackQueue().getFirstInLoop().getInfo().title)){
                            for (int i=c;i<reversed.size();i++){
                                old.add(reversed.get(i));
                            }
                            break;
                        }
                    }
                }
                for (AudioTrack t: old){
                    reversed.remove(t);
                }
                MainPlayer.getITEM().getMusicManager(g).getTrackQueue().getPlaylist().clear();
                MainPlayer.getITEM().getMusicManager(g).getTrackQueue().getPlaylist().addAll(reversed);
            }
        } else {
            MainPlayer.isLoop().replace(g, true);
            if (MainPlayer.isPlaying().get(g)) {
                AudioTrack track = MainPlayer.getITEM().getMusicManager(g).getPlayer().getPlayingTrack().makeClone();
                MainPlayer.getITEM().getMusicManager(g).getTrackQueue().getPlaylist().add(track);
                MainPlayer.getITEM().getMusicManager(g).getTrackQueue().setFirstInLoop(track);
            }
        }
    }
    public HashMap<Long, MainAudioManager> getMusicMapManagers() {
        return musicMapManagers;
    }

}

