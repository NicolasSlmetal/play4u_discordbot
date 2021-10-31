package com.play4ubot.listeners;

import com.play4ubot.audiopackage.MainPlayer;
import com.play4ubot.audiopackage.TrackQueue;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import java.io.File;
import java.util.HashMap;

public class EventReader extends ListenerAdapter {
    private static HashMap<Guild, Boolean> wait_prefix = new HashMap<>();
    public EventReader(){
    }

    public static HashMap<Guild, Boolean> isWait_prefix() {
        return wait_prefix;
    }

    public static void setWait_prefix(HashMap<Guild, Boolean> wait_prefix) {
        EventReader.wait_prefix = wait_prefix;
    }

    @Override
    public void onGenericEvent(@NotNull GenericEvent event) {
        if (event instanceof ReadyEvent) {
            for (Guild g : event.getJDA().getGuilds()) {
                System.out.println(g.getName() + ":" + g.getId());
                System.out.print("Membros: ");
                for (Member u : g.getMembers()) {
                    System.out.printf("%s -> %s ", u.getUser().getName(), u.getId());
                }
                System.out.println("\n===========================");
                System.out.println(System.getProperty("user.dir") + "/audiofiles");
                File dir = new File(System.getProperty("user.dir") + "/audiofiles");
                try {
                    boolean created = dir.mkdir();
                    System.out.println(created?"Diretório criado":"Diretório não criado");
                } catch (Exception e){
                    e.fillInStackTrace();
                }
                MainPlayer.isPlaying().put(g, false);
                MainPlayer.isPaused().put(g, false);
                MainPlayer.isLoop().put(g, false);
                MainPlayer.getName_music().put(g, null);
                MessageReader.getPrefix().put(g, "=");
                isWait_prefix().put(g, false);
                MessageReader.isWait_answer().put(g, false);
                TrackQueue.getGuild().add(g);
            }
        }

    }

    @Override
    public void onGuildJoin(@NotNull GuildJoinEvent event) {
        MainPlayer.isPlaying().put(event.getGuild(), false);
        MainPlayer.isPaused().put(event.getGuild(), false);
        MainPlayer.isLoop().put(event.getGuild(), false);
        MainPlayer.getName_music().put(event.getGuild(), null);
        MessageReader.getPrefix().put(event.getGuild(), "=");
        MessageReader.isWait_answer().put(event.getGuild(), false);
        TextChannel defaultCh = event.getGuild().getDefaultChannel();
        defaultCh.sendMessage("@everyone, Olá a todos," +
                " eu sou o bot Play4U. Preciso que " +
                "definam um pré-fixo para utilizar meus comandos. Digite :** prefix \"símbolo\"**. Se preferirem " +
                "o pré-fixo padrão \"=\", digite:** prefix =**").queue();
        isWait_prefix().put(event.getGuild(), true);
        TrackQueue.getGuild().add(event.getGuild());
    }

    @Override
    public void onGuildLeave(@NotNull GuildLeaveEvent event) {
        MainPlayer.isPlaying().remove(event.getGuild());
        MainPlayer.getName_music().remove(event.getGuild());
        MainPlayer.isLoop().remove(event.getGuild());
        MainPlayer.isPaused().remove(event.getGuild());
        TrackQueue.getGuilds().remove(event.getGuild());
        EventReader.isWait_prefix().remove(event.getGuild());
        MessageReader.getPrefix().remove(event.getGuild());
        MessageReader.isWait_answer().remove(event.getGuild());
    }
}
