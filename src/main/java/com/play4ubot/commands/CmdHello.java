package com.play4ubot.commands;

import com.play4ubot.listeners.MessageReader;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.*;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class CmdHello implements CommandAction {
    @Override
    public void getCommand(String cmd, String user, MessageReceivedEvent event) {
        cmd = cmd.replaceFirst(MessageReader.getPrefix(), "").trim();
        verifyCommand(cmd, user, event);
    }
    @Override
    public void verifyCommand(String cmd, String user, MessageReceivedEvent event) {
        executeCommand(cmd, user, event);
    }

    public void executeCommand(String cmd, String user, MessageReceivedEvent event) {
        int hour = ZonedDateTime.now(ZoneId.of("America/Sao_Paulo")).getHour();
        String hello;
        System.out.println(hour);
        if (hour < 12 && hour > 5) {
            hello = "Bom dia";
        } else if (hour < 18 && hour >= 12) {
            hello = "Boa tarde";
        } else {
            hello = "Boa noite";
        }
        event.getChannel().sendMessage(hello + " " + user).queue();
    }
    @Override
    public EmbedBuilder getHelp(String cmd, MessageReceivedEvent event){
        return new EmbedBuilder()
                .setAuthor(event.getJDA().getSelfUser().getName())
                .setThumbnail(event.getJDA().getSelfUser().getAvatarUrl())
                .setTitle("OI")
                .setColor(new Color(0x04D7D7))
                .setDescription("Envia um oi ao bot, e é respondido com uma saudação dele. Seja simpático com o bot!");
    }
}