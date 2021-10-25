package com.play4ubot.commands;

import com.play4ubot.audiopackage.MainPlayer;
import com.play4ubot.listeners.MessageReader;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class CmdLoop implements CommandAction{
    @Override
    public void getCommand(String cmd, String user, MessageReceivedEvent event) {
        cmd = cmd.replaceFirst(MessageReader.getPrefix().get(event.getGuild()) +"LOOP", "");
        verifyCommand(cmd, user, event);
    }

    @Override
    public void verifyCommand(String cmd, String user, MessageReceivedEvent event) throws IllegalArgumentException {
        executeCommand(cmd, user, event);
    }

    @Override
    public void executeCommand(String cmd, String user, MessageReceivedEvent event) {
        MainPlayer.setLoop(event.getGuild());
        event.getChannel().sendMessage(String.format("**Loop %s :repeat:**", MainPlayer.isLoop().get(event.getGuild())?
                "ativado":"desativado")).queue();
    }

    @Override
    public EmbedBuilder getHelp(String cmd, MessageReceivedEvent event) {
        return null;
    }
}
