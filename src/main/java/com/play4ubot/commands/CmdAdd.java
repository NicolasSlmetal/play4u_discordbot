package com.play4ubot.commands;

import com.play4ubot.listeners.MessageReader;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import com.play4ubot.utilities.BotConstants;
import com.play4ubot.utilities.FileManager;

import java.awt.*;
import java.util.List;

public class CmdAdd implements CommandAction{
    private FileManager fileManager;
    public CmdAdd(){
        this.fileManager = new FileManager();
    }
    @Override
    public void getCommand(String cmd, String user, MessageReceivedEvent event) {
        cmd = cmd.replaceFirst(MessageReader.getPrefix() + "ADD", "");
        verifyCommand(cmd, user, event);
    }

    @Override
    public void verifyCommand(String cmd, String user, MessageReceivedEvent event) throws IllegalArgumentException {
        List<Message.Attachment> file = event.getMessage().getAttachments();
        if (file.size() == 1){
            executeCommand(cmd, user, event);
        } else if (file.isEmpty()){
            throw new IllegalArgumentException(BotConstants.NOT_FOUND_FILE.getConstants());
        } else{
            throw new IllegalArgumentException(BotConstants.EXCESS_OF_FILES.getConstants());
        }
    }

    @Override
    public void executeCommand(String cmd, String user, MessageReceivedEvent event) {
        Message.Attachment file = event.getMessage().getAttachments().get(0);
        String music = file.getFileName();
        this.getFileManager().downloadFile(music, file, event.getChannel(), user);
    }

    @Override
    public EmbedBuilder getHelp(String cmd, MessageReceivedEvent event) {
        return new EmbedBuilder().setAuthor(event.getJDA().getSelfUser().getName())
                .setThumbnail(event.getJDA().getSelfUser().getAvatarUrl())
                .setTitle("Add")
                .setDescription("Adicona uma música ao banco de músicas do bot. É necessário " +
                        "anexar um arquivo, com extensão válida")
                .addField("Sintaxe", BotConstants.ADD_SYNTAX.getConstants(), true)
                .setColor(new Color(0x04D7D7));
    }

    public FileManager getFileManager() {
        return fileManager;
    }

    public void setFileManager(FileManager fileManager) {
        this.fileManager = fileManager;
    }
}
