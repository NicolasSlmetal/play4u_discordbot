package com.play4ubot.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import com.play4ubot.utilities.BotConstants;
import com.play4ubot.utilities.FileManager;
import com.play4ubot.listeners.MessageReader;

import java.awt.*;
import java.util.HashMap;

public class CmdDel implements CommandAction{
    private String music;
    private FileManager manager = new FileManager();
    private static HashMap<Guild, String> member = new HashMap<>();

    public String getMusic() {
        return music;
    }

    public void setMusic(String music) {
        this.music = music;
    }

    public FileManager getManager() {
        return manager;
    }

    public void setManager(FileManager manager) {
        this.manager = manager;
    }

    public static HashMap<Guild, String> getMember() {
        return member;
    }

    public static void setMember(HashMap<Guild, String> member) {
        CmdDel.member = member;
    }

    @Override
    public void getCommand(String cmd, String user, MessageReceivedEvent event) {
        cmd = cmd.replaceFirst(MessageReader.getPrefix().get(event.getGuild()) + "DEL", "").trim();
        getMember().put(event.getGuild(), user);
        verifyCommand(cmd, user, event);
    }

    @Override
    public void verifyCommand(String cmd, String user, MessageReceivedEvent event) throws IllegalArgumentException {
        if (cmd.isEmpty()){
            throw new IllegalArgumentException(BotConstants.NO_ARGUMENT_TO_DELETE.getConstants());
        } else {
            String music_file = manager.searchFile(cmd);
            if (music_file != null) {
                String[] dir = music_file.split("audiofiles");
                this.setMusic(dir[dir.length - 1].substring(1));
            } else{
                event.getChannel().sendMessage(user + ", Não encontrei a música :cry:").queue();
                return;
            }
                event.getChannel().sendMessage(user +", Estou prestes a deletar a música **" + this.getMusic() +
                "**. Tem ceteza que deseja prosseguir?:face_with_monocle: [Responda com \"Sim\" ou \"Não\"]  ").queue();
                MessageReader.isWait_answer().replace(event.getGuild(), true);
            }
        }

    @Override
    public void executeCommand(String cmd, String user, MessageReceivedEvent event) {
        this.getManager().deleteFile(this.getMusic());
        getMember().remove(event.getGuild(), user);
    }
    public void executeCommand(){

    }

    @Override
    public EmbedBuilder getHelp(String cmd, MessageReceivedEvent event) {
        return new EmbedBuilder().setThumbnail(event.getJDA().getSelfUser().getAvatarUrl())
                .setAuthor(event.getJDA().getSelfUser().getName())
                .setTitle("Del")
                .setColor(new Color(0x04D7D7))
                .setDescription("Deleta uma música do banco de músicas. É um comando perigoso, use com sabedoria...")
                .addField("Sintaxe", BotConstants.DEL_SYNTAX.getConstants(), true);
    }
}
