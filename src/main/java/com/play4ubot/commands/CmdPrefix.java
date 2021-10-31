package com.play4ubot.commands;

import com.play4ubot.listeners.MessageReader;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import com.play4ubot.utilities.BotConstants;
import java.awt.*;
import java.util.Arrays;

public class CmdPrefix implements CommandAction{
    @Override
    public void getCommand(String cmd, String user, MessageReceivedEvent event) {
        cmd = cmd.replaceFirst(MessageReader.getPrefix().get(event.getGuild()) + "PREFIXO", "").trim();
        verifyCommand(cmd, user, event);
    }

    @Override
    public void verifyCommand(String cmd, String user, MessageReceivedEvent event) throws IllegalArgumentException {
        String[] invalid = {"{", "}", "?", "#", "@", "*", ".", "$", ":", "|", "(", ")", "[", "]", "\\"
        , "+", "^"};
        if (cmd.isEmpty() || cmd.matches("[A-Z]") || cmd.matches("[0-9]") || cmd.length() > 1 || Arrays.stream(invalid)
                .anyMatch(s -> s.equals(cmd))){
            throw new IllegalArgumentException(BotConstants.INVALID_PREFIX.getConstants());
        } else{
            executeCommand(cmd, user, event);
        }
    }

    @Override
    public void executeCommand(String cmd, String user, MessageReceivedEvent event) {
        MessageReader.getPrefix().replace(event.getGuild(), cmd);
        event.getChannel().sendMessage(String.format("**SUCESSO**, pré-fixo definido como **\"%s\"**", MessageReader.getPrefix().get(event.getGuild())
        )).queue();
    }

    @Override
    public EmbedBuilder getHelp(String cmd, MessageReceivedEvent event) {
        return new EmbedBuilder().setTitle("Prefixo")
                .setAuthor(event.getJDA().getSelfUser().getName())
                .setThumbnail(event.getJDA().getSelfUser().getAvatarUrl())
                .setColor(new Color(0x04D7D7))
                .setDescription("Altera o pré-fixo utilizado para comandos do Play4U")
                .addField("Sintaxe", BotConstants.PREFIX_SYNTAX.getConstants(), true);
    }
}
