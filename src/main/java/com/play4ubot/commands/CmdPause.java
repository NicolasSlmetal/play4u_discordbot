package com.play4ubot.commands;

import com.play4ubot.audiopackage.MainPlayer;
import com.play4ubot.listeners.MessageReader;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import com.play4ubot.utilities.BotConstants;

import java.awt.*;
import java.util.Objects;


public class CmdPause implements CommandAction{

    @Override
    public void getCommand(String cmd, String user, MessageReceivedEvent event) {
        cmd = cmd.replaceFirst(MessageReader.getPrefix(), "").trim();
        verifyCommand(cmd, user, event);
    }

    @Override
    public void verifyCommand(String cmd, String user, MessageReceivedEvent event) throws IllegalArgumentException {
        if (!event.getGuild().getAudioManager().isConnected()){
            throw new IllegalArgumentException(BotConstants.SELF_NOT_IN_VOICE_CHANNEL.getConstants());
        }
        if (Objects.requireNonNull(event.getMember().getVoiceState()).getChannel() == null){
            throw new IllegalArgumentException(BotConstants.NOT_IN_VOICE_CHANNEL.getConstants());
        } else{
            VoiceChannel userCh = event.getMember().getVoiceState().getChannel();
            VoiceChannel botCh = event.getGuild().getAudioManager().getConnectedChannel();
            if (!userCh.getName().equals(Objects.requireNonNull(botCh).getName())) {
                throw new IllegalArgumentException(BotConstants.NOT_IN_SAME_VOICE_CHANNEL.getConstants());
            } else if (!MainPlayer.isPlaying()){
                event.getChannel().sendMessage("Não estou reproduzindo música :drooling_face:").queue();
                return;
            } else if (MainPlayer.isPaused()){
                event.getChannel().sendMessage("A música já está pausada :drooling_face:").queue();
            } else{
                executeCommand(cmd, user, event);
            }
        }
    }

    @Override
    public void executeCommand(String cmd, String user, MessageReceivedEvent event) {
        MainPlayer.setPaused(true);
        event.getChannel().sendMessage("**Música em pause :pause_button:**").queue();
    }

    @Override
    public EmbedBuilder getHelp(String cmd, MessageReceivedEvent event) {
        return new EmbedBuilder().setTitle("Pause")
                .setThumbnail(event.getJDA().getSelfUser().getAvatarUrl())
                .setDescription("Pausa uma música. Não faz nada se uma música não estiver sendo reproduzida")
                .setAuthor(event.getJDA().getSelfUser().getName())
                .setColor(new Color(0x04D7D7));
    }
}
