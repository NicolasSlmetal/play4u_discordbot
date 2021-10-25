package com.play4ubot.commands;

import com.play4ubot.listeners.MessageReader;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import com.play4ubot.utilities.BotConstants;

import java.awt.*;

public class CmdExit implements CommandAction{
    @Override
    public void getCommand(String cmd, String user, MessageReceivedEvent event) {
        cmd = cmd.replaceFirst(MessageReader.getPrefix().get(event.getGuild()) + "SAIR", "");
        verifyCommand(cmd, user, event);
    }

    @Override
    public void verifyCommand(String cmd, String user, MessageReceivedEvent event) throws IllegalArgumentException {
        if (!event.getGuild().getAudioManager().isConnected()){
            throw new UnsupportedOperationException(BotConstants.SELF_NOT_IN_VOICE_CHANNEL.getConstants());
        } else{
            executeCommand(cmd, user, event);
        }
    }

    @Override
    public void executeCommand(String cmd, String user, MessageReceivedEvent event) {
        event.getGuild().getAudioManager().closeAudioConnection();
    }

    @Override
    public EmbedBuilder getHelp(String cmd, MessageReceivedEvent event) {
        return new EmbedBuilder().setTitle("Sair")
                .setDescription("Retira o bot de um canal de voz")
                .setColor(new Color(0x04D7D7))
                .setAuthor(event.getJDA().getSelfUser().getName())
                .setThumbnail(event.getJDA().getSelfUser().getAvatarUrl());
    }
}
