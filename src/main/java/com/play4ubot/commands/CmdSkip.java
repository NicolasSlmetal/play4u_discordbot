package com.play4ubot.commands;

import com.play4ubot.audiopackage.MainPlayer;
import com.play4ubot.listeners.MessageReader;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import com.play4ubot.utilities.BotConstants;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class CmdSkip extends CommandLimiter implements CommandAction{
    public CmdSkip(){
        super("SKIP");
    }
    @Override
    public void getCommand(String cmd, String user, MessageReceivedEvent event) {
        cmd = cmd.replaceFirst(MessageReader.getPrefix().get(event.getGuild()) + "SKIP", "");
        verifyCommand(cmd, user, event);

    }

    @Override
    public void verifyCommand(String cmd, String user, MessageReceivedEvent event) throws IllegalArgumentException {
        if (!event.getGuild().getAudioManager().isConnected()) {
            throw new UnsupportedOperationException(BotConstants.SELF_NOT_IN_VOICE_CHANNEL.getConstants());
        } else if (event.getMember().getVoiceState().getChannel() == null){
            throw new IllegalArgumentException(BotConstants.NOT_IN_VOICE_CHANNEL.getConstants());
        } else{
            VoiceChannel usCh = event.getMember().getVoiceState().getChannel();
            VoiceChannel botCh = event.getGuild().getAudioManager().getConnectedChannel();
            if (!usCh.getName().equals(botCh.getName())){
                throw new IllegalArgumentException(BotConstants.NOT_IN_SAME_VOICE_CHANNEL.getConstants());
            } else if (!MainPlayer.isPlaying().get(event.getGuild())){
                event.getChannel().sendMessage("Não estou reproduzindo música :drooling_face:").queue();
                return;
            } else {
                executeCommand(cmd, user, event);
            }
        }
    }

    @Override
    public void executeCommand(String cmd, String user, MessageReceivedEvent event) {
        Runnable r = () -> MainPlayer.getITEM().skip(event.getGuild(), event.getTextChannel());
        final List<Member> members = new ArrayList<>(event.getMember().getVoiceState().getChannel().getMembers());
        members.removeIf(member -> member.getUser().isBot());
        List<Member> copy = new ArrayList<>(members);
        copy.remove(event.getMember());
        if (!copy.isEmpty()) {
            if (this.getMutableList().isEmpty()) {
                this.verifyLimit(members, event.getMember(), event.getMember().getVoiceState().getChannel(), event.getTextChannel(), r);
            } else if (this.getMutableList().contains(event.getMember())){
                this.setMember(event.getMember());
                synchronized (getMutableList()) {
                    this.getMutableList().notify();
                }
            } else if (this.getSendeds().contains(event.getMember())) {
                event.getChannel().sendMessage(user + ", Você já enviou o comando :rage:").queue();
            }
        } else {
            r.run();
        }
    }



    @Override
    public EmbedBuilder getHelp(String cmd, MessageReceivedEvent event) {
        return new EmbedBuilder().setTitle("SKIP")
                .setAuthor(event.getJDA().getSelfUser().getName())
                .setThumbnail(event.getJDA().getSelfUser().getAvatarUrl())
                .setDescription("Pula uma música e segue para a próxima na playlist, se houver")
                .setColor(new Color(0x04D7D7));
    }
}
