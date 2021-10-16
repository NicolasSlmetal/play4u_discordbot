package commands;

import listeners.MessageReader;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;


public class CmdHelp implements CommandAction{
    private final HashMap<String, CommandAction> cmd_map = new HashMap<>();

    public HashMap<String, CommandAction> getCmd_map() {
        return cmd_map;
    }

    public CmdHelp(){
        String[] names = {"OI", "PING", "PLAY", "PAUSE", "SKIP", "LOOP", "STOP", "ADD", "BANCO", "ENTRAR",
        "SAIR", "DEL", "PREFIXO"};
        CommandAction[] cmds = {new CmdHello(), new CmdPing(), new CmdPlay(), new CmdPause(), new CmdSkip(),
                new CmdLoop(), new CmdStop(), new CmdAdd(), new CmdBank(), new CmdJoin(), new CmdExit()
        , new CmdDel(), new CmdPrefix()};
        for (int c=0;c< names.length;c++){
            cmd_map.put(names[c], cmds[c]);
        }
    }
    @Override
    public void getCommand(String cmd, String user, MessageReceivedEvent event) {
        cmd = cmd.replaceFirst(MessageReader.getPrefix(), "").trim();
        verifyCommand(cmd, user, event);
    }

    @Override
    public void verifyCommand(String cmd, String user, MessageReceivedEvent event) throws IllegalArgumentException, UnsupportedOperationException {
        String cmd_request = cmd.trim().replaceFirst("HELP", "").trim();
        if (!cmd_request.isEmpty()){
                for (String key : this.getCmd_map().keySet()){
                    if(key.equals(cmd_request)){
                        MessageEmbed embed = this.getCmd_map().get(key).getHelp(key, event).build();
                        event.getChannel().sendMessageEmbeds(embed).queue();
                        break;
                    }
                }
        } else {
                executeCommand(cmd, user, event);
        }
    }

    @Override
    public void executeCommand(String cmd, String user, MessageReceivedEvent event) {
        String description = "";
        String[] command_desc = {"Envia um oi ao bot. Seja simpático com ele!\n", "O clássico ping. Retorna a latência da resposta\n"
                ,"Reproduz ou retoma a reprodução de uma música. É necessário estar\n" +
                "                                conectado a um canal de voz. Além disso, precisa do nome da música, caso não\n" +
                "                                 haja uma pausada. Como música, aceito o nome, arquivo(mp3, m4a, wav, Opus, mp4) e\n" +
                "                                 URLs do Vimeo e BandCamp\n", "Pausa uma música\n", "Pula uma música e segue para a próxima na playlist, se houver\n",
        "Para a reprodução de uma playlist, cancelando todas as músicas\n", "Ativa um loop de reprodução. Não é necessário estar num canal de voz\n", "" +
                "Adicona uma música ao banco de músicas do bot. É necessário anexar" +
                "um arquivo, com extensão válida\n", "Exibe as músicas presentes no banco musical do bot." +
                "Tem um parâmetro opcional, tornando a busca mais precisa\n", "Entra no canal de voz onde o usuário está conectado\n",
                "Retira o bot de um canal de voz\n", "Deleta uma música do banco de músicas. É um comando perigoso, use com sabedoria...\n", "Altera o pré-fixo utilizado para comandos do Play4U"};
        Iterator<String> desc = Arrays.stream(command_desc).iterator();
        String[] names = {"OI", "PING", "PLAY", "PAUSE", "SKIP", "LOOP", "STOP", "ADD", "BANCO", "ENTRAR",
                "SAIR", "DEL", "PREFIXO"};
        for (String cmdN: names){
            if (desc.hasNext()){
                description += String.format("**%s%s**:%s", MessageReader.getPrefix(), cmdN, desc.next());
            }
        }
        EmbedBuilder embed = new EmbedBuilder()
                .setAuthor(event.getJDA().getSelfUser().getName())
                .addField("Préfixo atual: ", MessageReader.getPrefix(), true)
                .setThumbnail(event.getJDA().getSelfUser().getAvatarUrl())
                .setColor(new Color(0x04D7D7))
                .setTitle("Ajuda: Comandos do Play4U")
                        .setDescription(description);
        event.getChannel().sendMessageEmbeds(embed.build()).queue();
    }

    @Override
    public EmbedBuilder getHelp(String cmd, MessageReceivedEvent event) {
        return null;
    }
}
