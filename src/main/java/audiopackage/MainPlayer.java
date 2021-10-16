package audiopackage;

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
import java.util.stream.Collectors;

public class MainPlayer extends AudioSource{
    private static MainPlayer ITEM;
    private static String name_music;
    private static boolean playing = false;
    private static boolean paused = false;
    private static boolean loop = false;
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
                MainPlayer.setPaused(false);
                String title = MainPlayer.isPlaying()?"Adicionado na fila":"Tocando";
                main.getTrackQueue().queuePlaylist(track);
                EmbedBuilder embed;
                if (!track.getInfo().title.equals("Unknown Title")){
                    MainPlayer.setName_music(track.getInfo().title);
                }
                if (!MainPlayer.isPlaying()) {
                    MainPlayer.setPlaying(true);
                    embed = setMusicEmbed(MainPlayer.getName_music(), title, sendDuration(track.getDuration()));
                } else {
                    long timeActual = main.getPlayer().getPlayingTrack().getPosition();
                    long timeToEnd = main.getPlayer().getPlayingTrack().getDuration();
                    long totalTime = timeToEnd - timeActual;
                    for (AudioTrack t : main.getTrackQueue().getPlaylist()){
                        if (t != track){
                            totalTime += t.getDuration();
                        } else {
                            break;
                        }
                    }
                    String timeToNext = sendDuration(totalTime);
                    embed = setMusicEmbed(MainPlayer.getName_music(), title, sendDuration(track.getDuration()),
                            timeToNext);
                }
                textCh.sendMessageEmbeds(embed.setAuthor(textCh.getJDA().getSelfUser().getName())
                        .setThumbnail(textCh.getJDA().getSelfUser().getAvatarUrl()).build()).queue();
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                main.getPlayer().startTrack(playlist.getTracks().get(0), true);
            }

            @Override
            public void noMatches() {

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

    public static String getName_music() {
        return name_music;
    }

    public static void setName_music(String name_music) {
        MainPlayer.name_music = name_music;
    }

    public static void setPlaying(boolean playing) {
        MainPlayer.playing = playing;
    }

    public static boolean isPlaying() {
        return playing;
    }

    public static boolean isPaused() {
        return paused;
    }

    public static void setPaused(boolean option) {
        long id = MainPlayer.getITEM().musicMapManagers.keySet().stream().collect(Collectors.toList()).get(0);
        MainPlayer.getITEM().musicMapManagers.get(id).getPlayer().setPaused(option);
        MainPlayer.paused = option;
    }
    @Override
    public ArrayList<String> skip(){
        long id = MainPlayer.getITEM().musicMapManagers.keySet().stream().collect(Collectors.toList()).get(0);
        String name_skiped = MainPlayer.getITEM().musicMapManagers.get(id).getPlayer().getPlayingTrack().getInfo().title;
        MainPlayer.getITEM().musicMapManagers.get(id).getTrackQueue().nextTrack();
        String name_actual;

        try {
            name_actual = MainPlayer.getITEM().musicMapManagers.get(id).getPlayer().getPlayingTrack().getInfo().title
            + "\nDuração: " + MainPlayer.getITEM().sendDuration(MainPlayer.getITEM().getMusicMapManagers()
                    .get(id).getPlayer().getPlayingTrack().getDuration());

        }catch (NullPointerException n){
            name_actual = "null";
            MainPlayer.paused = false;
            MainPlayer.setPlaying(false);
        }
        ArrayList<String> names = new ArrayList<>();
        names.add(name_skiped);
        names.add(name_actual);
        return names;
    }
    @Override
    public void stop(){
        long id = MainPlayer.getITEM().musicMapManagers.keySet().stream().collect(Collectors.toList()).get(0);
        MainPlayer.getITEM().musicMapManagers.get(id).getPlayer().stopTrack();
        MainPlayer.getITEM().musicMapManagers.get(id).getTrackQueue().getPlaylist().clear();
        MainPlayer.getITEM().musicMapManagers.get(id).getTrackQueue().nextTrack();
        MainPlayer.paused = false;
        MainPlayer.setPlaying(false);
    }

    public AudioPlayerManager getManager() {
        return manager;
    }

    public static void setITEM(MainPlayer ITEM) {
        MainPlayer.ITEM = ITEM;
    }

    public static boolean isLoop() {
        return loop;
    }

    public static void setLoop() {
        MainPlayer.loop = !loop;
        if (!MainPlayer.isLoop()){
            TrackQueue.getTracks().clear();
        }
        if (MainPlayer.isPlaying() && MainPlayer.isLoop()){
            long id = MainPlayer.getITEM().musicMapManagers.keySet().stream().collect(Collectors.toList()).get(0);
            AudioTrack track = MainPlayer.getITEM().musicMapManagers.get(id).getPlayer().getPlayingTrack();
            TrackQueue.getTracks().clear();
            TrackQueue.addTracksToLoop(track.makeClone());
            System.out.println(TrackQueue.getTracks());
        }
    }
    public HashMap<Long, MainAudioManager> getMusicMapManagers() {
        return musicMapManagers;
    }

}

