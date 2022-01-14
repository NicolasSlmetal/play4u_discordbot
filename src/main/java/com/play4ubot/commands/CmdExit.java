package com.play4ubot.commands;

import com.play4ubot.listeners.MessageReader;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import com.play4ubot.utilities.BotConstants;
import net.dv8tion.jda.api.managers.AudioManager;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class CmdExit extends CommandLimiter implements CommandAction{
    public CmdExit(){
        super("SAIR");
    }
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
        AudioManager manager = event.getGuild().getAudioManager();
        Runnable r = manager::closeAudioConnection;
        List<Member> members = new ArrayList<>(event.getMember().getVoiceState().getChannel().getMembers());
        members.removeIf(member -> member.getUser().isBot());
        List<Member> copy = new ArrayList<>(members);
        copy.remove(event.getMember());
        if (!copy.isEmpty()){
            if (this.getMutableList().isEmpty()){
                this.verifyLimit(members, event.getMember(), event.getMember().getVoiceState().getChannel(), event.getTextChannel(), r);
            } else if (this.getMutableList().contains(event.getMember())){
                this.setMember(event.getMember());
                synchronized (this.getMutableList()){
                    this.getMutableList().notify();
                }
            } else if (this.getSendeds().contains(event.getMember())) {
                event.getChannel().sendMessage(user + ",Você já enviou o comando :rage:").queue();
            }
        }else{
            r.run();
        }
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
