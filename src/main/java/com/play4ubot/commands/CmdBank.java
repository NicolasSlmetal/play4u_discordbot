package com.play4ubot.commands;

import com.play4ubot.listeners.MessageReader;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import com.play4ubot.utilities.BotConstants;
import com.play4ubot.utilities.FileManager;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CmdBank implements CommandAction{
    private ArrayList<String> musics = new ArrayList<>();
    @Override
    public void getCommand(String cmd, String user, MessageReceivedEvent event) {
        cmd = cmd.replaceFirst(MessageReader.getPrefix() + "BANCO", "").trim();
        verifyCommand(cmd, user, event);
    }

    @Override
    public void verifyCommand(String cmd, String user, MessageReceivedEvent event) throws IllegalArgumentException {
        executeCommand(cmd, user, event);
    }

    @Override
    public void executeCommand(String cmd, String user, MessageReceivedEvent event) {
        String description = "";
        String title;
        this.getMusics().clear();
        this.setMusics();
        if (cmd.isEmpty()){
            title = "Todas as músicas do banco";
            List<String> sorted_musics = this.getMusics().stream().sorted().collect(Collectors.toCollection(ArrayList::new));
            for (int c = 0;c< sorted_musics.size();c++){
                description += (c + 1) +"º -" + sorted_musics.get(c) + "\n";
            }
        } else{
            title = String.format("Todas as músicas com \"%s\"", cmd.toUpperCase());
            List<String> musics = new FileManager().searchFiles(cmd);
            if (cmd.length() == 1){
                Stream<String> stream = musics.stream().filter(m -> m.toUpperCase().startsWith(cmd));
                musics = stream.collect(Collectors.toCollection(ArrayList::new));
            }
            for (int c =0; c< musics.size();c++){
                description += (c + 1) +"º -"+ musics.get(c) + "\n";
            }
        }
        if (!description.isEmpty()){
            event.getChannel().sendMessageEmbeds(new EmbedBuilder()
                    .setThumbnail(event.getJDA().getSelfUser().getAvatarUrl())
                    .setAuthor(event.getJDA().getSelfUser().getName())
                    .setDescription(description)
                            .setTitle(title)
                    .setColor(new Color(0xFF0707))
                    .build()).queue();
        } else{
           if (!this.getMusics().isEmpty()){
               event.getChannel().sendMessage("Não há músicas com \"" + cmd + "\"").queue();
           } else{
               event.getChannel().sendMessage("Não há músicas no banco ainda. Adicione!").queue();
           }
        }
    }

    @Override
    public EmbedBuilder getHelp(String cmd, MessageReceivedEvent event) {
        return new EmbedBuilder().setTitle("Banco")
                .setDescription("Exibe as músicas presentes no banco musical do bot." +
                        "Tem um parâmetro opcional, tornando a busca mais precisa.")
                .setThumbnail(event.getJDA().getSelfUser().getAvatarUrl())
                .addField("Sintaxe", BotConstants.BANK_SYNTAX.getConstants(), true)
                .setAuthor(event.getJDA().getSelfUser().getName())
                .setColor(new Color(0x04D7D7));
    }

    public ArrayList<String> getMusics() {
        return musics;
    }

    public void setMusics() {
        File file = new FileManager().getDir();
        for (File f : file.listFiles()){
            this.musics.add(f.getName());
        }
    }
}
