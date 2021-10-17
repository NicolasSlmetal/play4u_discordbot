package com.play4ubot.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public interface CommandAction {
   void getCommand(String cmd, String user, MessageReceivedEvent event);
   void verifyCommand(String cmd, String user, MessageReceivedEvent event) throws IllegalArgumentException;
   void executeCommand(String cmd, String user, MessageReceivedEvent event);
   EmbedBuilder getHelp(String cmd, MessageReceivedEvent event);
}
