package com.play4ubot.commands;

import com.play4ubot.audiopackage.MainPlayer;
import com.play4ubot.listeners.MessageReader;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.managers.AudioManager;
import com.play4ubot.utilities.BotConstants;
import com.play4ubot.utilities.FileManager;
import com.play4ubot.utilities.URLFinder;

import java.awt.*;

public class CmdPlay implements CommandAction{
    private FileManager manager;
    private URLFinder finder;
    public CmdPlay(){
        this.manager = new FileManager();
        this.finder = new URLFinder();
    }

    public void conectChannel(Member member) {
        try {
            VoiceChannel channel = member.getVoiceState().getChannel();
            AudioManager manager = member.getGuild().getAudioManager();
            manager.openAudioConnection(channel);
        }catch (NullPointerException n){
            throw new IllegalArgumentException(BotConstants.NOT_IN_VOICE_CHANNEL.getConstants());
        }
    }
    @Override
    public void getCommand(String cmd, String user, MessageReceivedEvent event) {
        cmd = cmd.replaceFirst(MessageReader.getPrefix(), "").trim();
        verifyCommand(cmd, user, event);
    }

    @Override
    public void verifyCommand(String cmd, String user, MessageReceivedEvent event) throws IllegalArgumentException {
        if (!MainPlayer.isPaused()) {
            String music = cmd.trim().replaceFirst("PLAY", "").trim();
            if (music.isEmpty()) {
                if (!event.getMessage().getAttachments().isEmpty()) {
                    Message.Attachment file = event.getMessage().getAttachments().get(0);
                    music = this.manager.downloadFile(file.getFileName(), file, event.getMessage().getChannel(), user);
                } else {
                    throw new IllegalArgumentException(BotConstants.NO_ARGUMENT_TO_MUSIC.getConstants());
                }
            } else if (!finder.isUrl(music)) {
                try {
                    music = this.manager.searchFile(this.manager.removeSymbols(music));
                    System.out.println(music);
                    MainPlayer.setName_music(music);
                    if (music == null) {
                        event.getChannel().sendMessage("Não encontrei a música :cry:").queue();
                        return;
                    }
                } catch (NullPointerException e) {
                    e.printStackTrace();
                } catch (IllegalArgumentException e) {
                    throw new UnsupportedOperationException(BotConstants.NOT_IN_VOICE_CHANNEL.getConstants());
                }
            } else{
                if (music.contains("BANDCAMP") || music.contains("VIMEO")) {
                    music = event.getMessage().getContentRaw().replaceFirst(MessageReader.getPrefix(),
                            "").trim().substring(4).trim();
                } else {
                    event.getChannel().sendMessage(user + ", use URLs do Vimeo ou Bandcamp").queue();
                    return;
                }
            }
            try {
                conectChannel(event.getMember());
            } catch (IllegalArgumentException e) {
                throw new UnsupportedOperationException(BotConstants.NOT_IN_VOICE_CHANNEL.getConstants());
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
            executeCommand(music, user, event);
        } else{
            try {
                conectChannel(event.getMember());
            }catch (IllegalArgumentException e){
                throw new UnsupportedOperationException(BotConstants.NOT_IN_VOICE_CHANNEL.getConstants());
            }
            executeCommand();
            event.getChannel().sendMessage("**Música retomada :play_pause:**").queue();
        }
    }
    public void executeCommand(){
        MainPlayer.setPaused(false);
    }
    @Override
    public void executeCommand(String music, String user, MessageReceivedEvent event) {
        TextChannel textCh = event.getTextChannel();
        MainPlayer.getITEM().loadPlay(event.getTextChannel(), music);
    }
    @Override
    public EmbedBuilder getHelp(String cmd, MessageReceivedEvent event) {
        return new EmbedBuilder()
                .setAuthor(event.getJDA().getSelfUser().getName())
                .setThumbnail(event.getJDA().getSelfUser().getAvatarUrl())
                .setTitle("Play")
                .setDescription("Reproduz ou retoma a reprodução de uma música. É necessário estar " +
                        "em um canal de voz para ser executado. Além disso, precisa da música como parâmetro. " +
                        "A música pode ser passada com o nome, arquivo (mp3, m4a, opus, wav, etc) ou URLs do " +
                        "https://vimeo.com/watch e https://bandcamp.com/. Se uma música estiver tocando, adiciona a música declara a playlist. Se uma " +
                        "música estiver em pause, resume ela")
                .addField("Sintaxe", BotConstants.PLAY_SYNTAX.getConstants(), true)
                        .setColor(new Color(0x04D7D7));
    }

}
