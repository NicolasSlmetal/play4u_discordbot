package commands;

import audiopackage.MainPlayer;
import listeners.MessageReader;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import utilities.BotConstants;

import java.awt.*;

public class CmdStop implements CommandAction{
    @Override
    public void getCommand(String cmd, String user, MessageReceivedEvent event) {
        cmd = cmd.replaceFirst(MessageReader.getPrefix() + "STOP", "");
        verifyCommand(cmd, user, event);
    }

    @Override
    public void verifyCommand(String cmd, String user, MessageReceivedEvent event) throws IllegalArgumentException {
        if (!event.getGuild().getAudioManager().isConnected()) {
            throw new UnsupportedOperationException(BotConstants.SELF_NOT_IN_VOICE_CHANNEL.getConstants());

        } else if (event.getMember().getVoiceState().getChannel() == null) {
            throw new IllegalArgumentException(BotConstants.NOT_IN_VOICE_CHANNEL.getConstants());
        } else {
            VoiceChannel userCh = event.getMember().getVoiceState().getChannel();
            VoiceChannel botCh = event.getGuild().getAudioManager().getConnectedChannel();
            if (!userCh.getName().equals(botCh.getName())){
                throw new IllegalArgumentException(BotConstants.NOT_IN_SAME_VOICE_CHANNEL.getConstants());
            } else if (!MainPlayer.isPlaying()){
                event.getChannel().sendMessage("Não estou reproduzindo música :drooling_face:").queue();
                return;
            }else{
                executeCommand(cmd, user, event);
            }
        }
    }
    @Override
    public void executeCommand(String cmd, String user, MessageReceivedEvent event) {
        MainPlayer.getITEM().stop();
        event.getChannel().sendMessage("**Reprodução interrompida** :stop_button: ").queue();
    }

    @Override
    public EmbedBuilder getHelp(String cmd, MessageReceivedEvent event) {
        return new EmbedBuilder().setTitle("STOP")
                .setThumbnail(event.getJDA().getSelfUser().getAvatarUrl())
                .setAuthor(event.getJDA().getSelfUser().getName())
                .setDescription("Para a reprodução de uma playlist, cancelando todas as músicas")
                .setColor(new Color(0x04D7D7));
    }
}
