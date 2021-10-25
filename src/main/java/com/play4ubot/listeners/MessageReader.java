package com.play4ubot.listeners;

import com.play4ubot.commands.CmdDel;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import java.util.Arrays;
import java.util.HashMap;


public class MessageReader extends ListenerAdapter {
    private static HashMap<Guild, String> prefix = new HashMap<>();
    public static HashMap<Guild, Boolean> wait_answer = new HashMap<>();
    public CommandChecker loader = new CommandChecker();

    public CommandChecker getLoader() {
        return loader;
    }

    public void setLoader(CommandChecker loader) {
        this.loader = loader;
    }

    public static HashMap<Guild, Boolean> isWait_answer() {
        return wait_answer;
    }

    public static void setWait_answer(HashMap<Guild, Boolean> wait_answer) {
        MessageReader.wait_answer = wait_answer;
    }

    public static HashMap<Guild, String> getPrefix() {
        return prefix;
    }

    public static void setPrefix(HashMap<Guild, String> prefix) {
        MessageReader.prefix = prefix;
    }

    public void onMessageReceived(MessageReceivedEvent message) {
        if (message.getAuthor().isBot() || message.isFromType(ChannelType.PRIVATE)) {
            return;
        }
        String user_mention = message.getAuthor().getAsMention();
        String msg = message.getMessage().getContentRaw();
        msg = msg.trim().toUpperCase();
        if (msg.startsWith(getPrefix().get(message.getGuild())) && !wait_answer.get(message.getGuild())) {
            this.getLoader().loadCommand(msg, message, user_mention);
        } else if (isWait_answer().get(message.getGuild()) && message.getAuthor().getName().equals(CmdDel.getMember().get(message.getGuild()))) {
            if (msg.contains("SIM")) {
                this.getLoader().executeDangerousOperation(user_mention, message);
                message.getChannel().sendMessage("Música deletada com sucesso").queue();
                MessageReader.isWait_answer().replace(message.getGuild(), false);
            } else if (msg.contains("NÃO")) {
                message.getChannel().sendMessage("Ok, não vou deletar").queue();
                MessageReader.isWait_answer().replace(message.getGuild(), false);
            } else {
                message.getChannel().sendMessage("Digite uma opção válida [S/N]").queue();
            }
        }if (EventReader.isWait_prefix().get(message.getGuild()) && !message.getAuthor().isBot()) {
            String[] invalid = {"{", "}", "?", "#", "@", "*", ".", "$", ":", "|", "(", ")", "[", "]"};
            if (message.getMessage().getContentRaw().toUpperCase().startsWith("PREFIX")) {
                String symbol = message.getMessage().getContentRaw().toUpperCase().replace("PREFIX", "").trim();
                if (symbol.matches("[A-Z]") || symbol.matches("[0-9]") || symbol.length() > 1 || Arrays.stream(invalid)
                        .anyMatch(s -> s.equals(symbol))){
                    message.getChannel().sendMessage("Pré-fixo inválido, use símbolos válidos").queue();
                } else {
                    prefix.replace(message.getGuild(), symbol);
                    message.getChannel().sendMessage("**SUCESSO**, o pré-fixo agora é **\"" + prefix.get(message.getGuild()) + "\"**. Se desejar" +
                            " alterar, use o comando **" + prefix.get(message.getGuild()) + "prefixo**").queue();
                    EventReader.isWait_prefix().replace(message.getGuild(), false);
                }
            }
        }
    }
}