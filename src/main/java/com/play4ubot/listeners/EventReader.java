package com.play4ubot.listeners;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;


public class EventReader extends ListenerAdapter {
    private static boolean wait_prefix = false;
    public EventReader(){
    }

    public static boolean isWait_prefix() {
        return wait_prefix;
    }

    public static void setWait_prefix(boolean wait_prefix) {
        EventReader.wait_prefix = wait_prefix;
    }

    @Override
    public void onGenericEvent(@NotNull GenericEvent event) {
        if (event instanceof ReadyEvent) {
            for (Guild g : event.getJDA().getGuilds()) {
                System.out.println(g.getName() + ":" + g.getOwnerId());
                System.out.print("Membros: ");
                for (Member u : g.getMembers()) {
                    System.out.printf("%s -> %s ", u.getUser().getName(), u.getId());
                }
                System.out.println("\n===========================");
                System.out.println(System.getProperty("user.dir") + "\\audiofiles");
                File dir = new File(System.getProperty("user.dir") + "\\audiofiles");
                try {
                    boolean created = dir.mkdir();
                    System.out.println(created?"Diretório criado":"Diretório não criado");
                } catch (Exception e){
                    e.fillInStackTrace();
                }
            }
        }

    }

    @Override
    public void onGuildJoin(@NotNull GuildJoinEvent event) {
        TextChannel defaultCh = event.getGuild().getDefaultChannel();
        defaultCh.sendMessage("@everyone, Olá a todos," +
                " eu sou o bot Play4U. Preciso que" +
                "definam um pré-fixo para utilizar meus comandos. Digite :** prefix \"símbolo\"**. Se preferirem" +
                "o pré-fixo padrão \"=\", digite:** prefix =**").queue();
        setWait_prefix(true);
    }
}
