package commands;

import listeners.MessageReader;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.*;

public class CmdPing implements CommandAction{
    @Override
    public void getCommand(String cmd, String user, MessageReceivedEvent event) {
        cmd = cmd.replaceFirst(MessageReader.getPrefix(), "").trim();
        verifyCommand(cmd, user, event);
    }

    @Override
    public void verifyCommand(String cmd, String user, MessageReceivedEvent event) throws IllegalArgumentException, UnsupportedOperationException{
        if (event.isFromType(ChannelType.PRIVATE)) {
            throw new UnsupportedOperationException("Não executo comandos em canais privados");
        } else {
            executeCommand(cmd, user, event);
        }
    }

    @Override
    public void executeCommand(String cmd, String user, MessageReceivedEvent event) {
        long lat = System.currentTimeMillis();
        event.getChannel().sendMessage("pong!").queue(r ->
                r.editMessageFormat(":ping_pong: Pong!\nms %d", System.currentTimeMillis() - lat).queue());
    }

    @Override
    public EmbedBuilder getHelp(String cmd, MessageReceivedEvent event) {
        return new EmbedBuilder()
                .setThumbnail(event.getJDA().getSelfUser().getAvatarUrl())
                .setTitle("Ping")
                .setAuthor(event.getJDA().getSelfUser().getName())
                .setDescription("O clássico ping. Retorna a  latência da resposta")
                .setColor(new Color(0x04D7D7))
                ;
    }
}
