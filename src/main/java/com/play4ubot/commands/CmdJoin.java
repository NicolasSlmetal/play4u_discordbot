package com.play4ubot.commands;

import com.play4ubot.listeners.MessageReader;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.managers.AudioManager;
import com.play4ubot.utilities.BotConstants;
import java.awt.*;

public class CmdJoin implements CommandAction{
    @Override
    public void getCommand(String cmd, String user, MessageReceivedEvent event) {
        cmd = cmd.replaceFirst(MessageReader.getPrefix().get(event.getGuild()) + "ENTRAR", "");
        verifyCommand(cmd, user, event);
    }

    @Override
    public void verifyCommand(String cmd, String user, MessageReceivedEvent event) throws IllegalArgumentException {
        if (!event.getMember().getVoiceState().inVoiceChannel()){
            throw new UnsupportedOperationException(BotConstants.NOT_IN_VOICE_CHANNEL.getConstants());
        } else{
            executeCommand(cmd, user, event);
        }
    }

    @Override
    public void executeCommand(String cmd, String user, MessageReceivedEvent event) {
        VoiceChannel channel = event.getMember().getVoiceState().getChannel();
        AudioManager main = event.getGuild().getAudioManager();
        main.openAudioConnection(channel);
    }

    @Override
    public EmbedBuilder getHelp(String cmd, MessageReceivedEvent event) {
        return new EmbedBuilder().setColor(new Color(0x04D7D7))
                .setAuthor(event.getJDA().getSelfUser().getName())
                .setThumbnail(event.getJDA().getSelfUser().getAvatarUrl())
                .setTitle("Entrar")
                .setDescription("Entra no canal de voz onde o usuário está conectado");
    }
}
