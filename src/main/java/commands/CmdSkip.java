package commands;

import audiopackage.MainPlayer;
import listeners.MessageReader;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import utilities.BotConstants;

import java.awt.*;
import java.util.ArrayList;

public class CmdSkip implements CommandAction{
    @Override
    public void getCommand(String cmd, String user, MessageReceivedEvent event) {
        cmd = cmd.replaceFirst(MessageReader.getPrefix() + "SKIP", "");
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
            } else if (!MainPlayer.isPlaying()){
                event.getChannel().sendMessage("Não estou reproduzindo música :drooling_face:").queue();
                return;
            } else {
                executeCommand(cmd, user, event);
            }
        }
    }

    @Override
    public void executeCommand(String cmd, String user, MessageReceivedEvent event) {
        ArrayList<String> names = MainPlayer.getITEM().skip();
        event.getChannel().sendMessage("Música **" + names.get(0) + "** pulada...").queue();
        if (!names.get(1).equals("null")){
            EmbedBuilder embed = new EmbedBuilder().setColor(new Color(0xFF0707))
                    .setAuthor(event.getJDA().getSelfUser().getName())
                    .setTitle("Tocando ")
                    .setDescription(names.get(1))
                    .setThumbnail(event.getJDA().getSelfUser().getAvatarUrl());
            event.getChannel().sendMessageEmbeds(embed.build()).queue();
        }else{
            event.getChannel().sendMessage("Sem músicas para reproduzir").queue();
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
